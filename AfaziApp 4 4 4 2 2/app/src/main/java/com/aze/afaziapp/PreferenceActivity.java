package com.aze.afaziapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aze.afaziapp.databinding.ActivityPreferenceBinding;

public class PreferenceActivity extends AppCompatActivity {

    private ActivityPreferenceBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreferenceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}