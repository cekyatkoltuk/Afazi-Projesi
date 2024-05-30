package com.aze.afaziapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
public class FirebaseImage {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    public FirebaseImage() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("VisualComprehension");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("visualC");
    }

    public void addData(Context context) {
        Thread thread = new Thread(() -> {
            DatabaseReference level1Ref = databaseReference.child("level1");

            uploadImageAndSaveData(context, level1Ref, "elma.png", "elma", R.drawable.elma);


            DatabaseReference level2Ref = databaseReference.child("level2");

            uploadImageAndSaveData(context, level2Ref, "araba.png", "araba", R.drawable.araba);



            DatabaseReference level3Ref = databaseReference.child("level3");

            uploadImageAndSaveData(context, level3Ref, "kedi.png", "kedi", R.drawable.kedi);

        });

        thread.start();
    }

    private void uploadImageAndSaveData(Context context, DatabaseReference ref, String imageName, String title, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);

        byte[] imageData = bitmapToByteArray(bitmap);

        StorageReference imageRef = storageReference.child(imageName);
        imageRef.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        ref.child(title).setValue(imageUrl);
                    });
                })
                .addOnFailureListener(e -> {

                });
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
