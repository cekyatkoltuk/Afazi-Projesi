package com.aze.afaziapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.MediaController;
import android.widget.VideoView;

import com.aze.afaziapp.databinding.ActivityMemoryCardsAddBinding;
import com.aze.afaziapp.databinding.ActivityMemoryCardsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.UUID;

public class MemoryCardsAddActivity extends AppCompatActivity {

    private static final int IMAGE_AVTION_CODE = 102;
    private static final int CAMERA_ACTION_CODE = 103;

    private FirebaseStorage firebaseStorage;
    private Uri imageData;
    private ActivityMemoryCardsAddBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;

    private DatabaseReference databaseReference;

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemoryCardsAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerLauncher();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Lütfen bekleyin");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.selectFolderBtn.setOnClickListener(v -> selectImage(v));
        binding.addImgBtn.setOnClickListener(v -> uploadBtn(v));
        binding.cameraBtn.setOnClickListener(v -> openCamera());
    }

    public void uploadBtn(View view) {



        if(binding.nameEt.getText().toString().isEmpty() || imageData == null){
            Toast.makeText(this, "Lütfen resim ve yazı yükleyiniz. ", Toast.LENGTH_SHORT).show();
        }

        else {

            progressDialog.setMessage("Kart ekleniyor...");
            progressDialog.show();

            UUID uuid = UUID.randomUUID();
            String imageName = "cards/" + uuid + ".jpg";

            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(taskSnapshot -> {
                storageReference.child(imageName).getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    String title = binding.nameEt.getText().toString();
                    String uid = firebaseAuth.getUid();

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String email = user.getEmail();

                    HashMap<String, Object> cardData = new HashMap<>();
                    cardData.put("uid",uid);
                    cardData.put("useremail", email);
                    cardData.put("downloadurl", downloadUrl);
                    cardData.put("title", title);

                    String cardId = databaseReference.child("Cards").push().getKey();
                    if (cardId != null) {
                        cardData.put("cardId", cardId);
                        databaseReference.child("Cards").child(cardId).setValue(cardData).addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(MemoryCardsAddActivity.this, MemoryCardsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(MemoryCardsAddActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(MemoryCardsAddActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    public void selectImage(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Galeri için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", v -> {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }).show();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Snackbar.make(binding.getRoot(), "Kamera için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", v -> {
                    permissionLauncher.launch(Manifest.permission.CAMERA);
                }).show();
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA);
            }
        } else {
            startCamera();
        }
    }

    private void startCamera() {
        Intent intentToCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(intentToCamera);
    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent intentFromResult = result.getData();
                if (intentFromResult != null) {
                    if (intentFromResult.getData() != null) {
                        imageData = intentFromResult.getData();
                        binding.imageIv.setImageURI(imageData);
                    } else {
                        Bundle extras = intentFromResult.getExtras();
                        if (extras != null && extras.get("data") != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            imageData = getImageUri(imageBitmap);
                            binding.imageIv.setImageURI(imageData);
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                startCamera();
            } else {
                Toast.makeText(MemoryCardsAddActivity.this, "Kamera erişimi gerekiyor!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
}
