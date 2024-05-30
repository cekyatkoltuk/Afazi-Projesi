package com.aze.afaziapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AiActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    private ImageButton addAiCardButton;
    private RecyclerView recyclerView;
    private List<String> imagePaths;
    private ImageAdapter adapter;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AiActivity.this, HomeActivity.class));
            }
        });

        addAiCardButton = findViewById(R.id.addAiCardBtn);
        recyclerView = findViewById(R.id.aiCardContainer);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        addAiCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            loadImages();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Handle error
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.aze.afaziapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(getFilesDir(), "images");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            if (photoUri != null) {
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    processAndSaveImage(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void processAndSaveImage(Bitmap imageBitmap) {
        saveImageToFile(imageBitmap);
        loadImages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages();
    }


    private void saveImageToFile(Bitmap imageBitmap) {
        File directory = new File(getFilesDir(), "images");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImages() {
        File directory = new File(getFilesDir(), "images");
        if (!directory.exists() || directory.listFiles() == null || directory.listFiles().length == 0) {
            imagePaths = new ArrayList<>();
        } else {
            imagePaths = new ArrayList<>();
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    imagePaths.add(file.getAbsolutePath());
                }
            }
        }

        adapter = new ImageAdapter(this, imagePaths);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String imagePath) {
                Intent intent = new Intent(AiActivity.this, FullscreenImageActivity.class);
                intent.putExtra("imagePath", imagePath);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImages();
            } else {
                showCameraPermissionDeniedDialog();
            }
        }
    }

    private void showCameraPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Kamera İzni Gerekli")
                .setMessage("Kamera kullanımı için izin vermeniz gerekiyor. Lütfen ayarlara giderek izinleri etkinleştirin.")
                .setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("İptal", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TextToSpeechHelper.release();
    }
}