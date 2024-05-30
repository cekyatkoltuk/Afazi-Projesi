package com.aze.afaziapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class FullscreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_ai_card);

        ImageView imageView = findViewById(R.id.fullscreenAiCardImageView);
        String imagePath = getIntent().getStringExtra("imagePath");

        // imagePath null değilse, resmi yükle ve ekranda göster
        if (imagePath != null) {
            // Resmi göstermeden önce oryantasyonu kontrol et ve düzelt
            Bitmap rotatedBitmap = rotateBitmap(imagePath);
            if (rotatedBitmap != null) {
                imageView.setImageBitmap(rotatedBitmap);

            } else {
                Toast.makeText(this, "Resim yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Resim yolu bulunamadı", Toast.LENGTH_SHORT).show();
        }
    }

    // Resmin oryantasyonunu kontrol eden ve gerektiğinde döndüren metot
    private Bitmap rotateBitmap(String imagePath) {
        try {
            // Resmi dosyadan yükle
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;  // Örneklem boyutunu ayarla
            options.inJustDecodeBounds = false;  // Bitmap'i yüklerken gerçek boyutunu almak için false olarak ayarla
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

            // Resmin oryantasyonunu kontrol et ve gerektiğinde döndür
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    // Oryantasyon düzgün, döndürme gerekli değil
                    return bitmap;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
