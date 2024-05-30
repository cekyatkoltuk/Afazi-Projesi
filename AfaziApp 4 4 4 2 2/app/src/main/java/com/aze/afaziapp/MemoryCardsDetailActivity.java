package com.aze.afaziapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aze.afaziapp.databinding.ActivityMemoryCardsBinding;
import com.aze.afaziapp.databinding.ActivityMemoryCardsDetailBinding;
import com.squareup.picasso.Picasso;

public class MemoryCardsDetailActivity extends AppCompatActivity {

    private ActivityMemoryCardsDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemoryCardsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String cardImage = intent.getStringExtra("cardImage");
            String cardTitle = intent.getStringExtra("cardTitle");

            // Display the card image and title
                Picasso.get().load(cardImage).into(binding.imageIv);
                binding.nameTv.setText(cardTitle);
        }

    }
}