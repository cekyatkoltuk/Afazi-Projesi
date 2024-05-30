package com.aze.afaziapp;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRead {

    private DatabaseReference databaseReference;

    public FirebaseRead() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Readwords");
    }

    public void addData() {
        DatabaseReference dailyRef = databaseReference.child("daily");
        dailyRef.child("word1").setValue("merhaba");
        dailyRef.child("word2").setValue("nasılsın");
        dailyRef.child("word3").setValue("iyiyim");
        dailyRef.child("word4").setValue("kimsin");
        dailyRef.child("word5").setValue("nerede");
        dailyRef.child("word6").setValue("tamam");
        dailyRef.child("word7").setValue("hayır");
        dailyRef.child("word8").setValue("evet");

        DatabaseReference objectsRef = databaseReference.child("objects");
        objectsRef.child("word1").setValue("ağaç");
        objectsRef.child("word2").setValue("masa");
        objectsRef.child("word3").setValue("televizyon");
        objectsRef.child("word4").setValue("yemek");
        objectsRef.child("word5").setValue("koltuk");
        objectsRef.child("word6").setValue("otobüs");

        DatabaseReference animalsRef = databaseReference.child("animals");
        animalsRef.child("word1").setValue("kedi");
        animalsRef.child("word2").setValue("köpek");
        animalsRef.child("word3").setValue("kuş");
        animalsRef.child("word4").setValue("fare");
        animalsRef.child("word5").setValue("örümcek");
    }
}


