package com.aze.afaziapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import com.aze.afaziapp.databinding.ActivityCategoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class CategoryActivity extends AppCompatActivity {

    private ActivityCategoryBinding binding;
    private boolean isWritingMode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this,HomeActivity.class));
            }
        });

        TextToSpeechHelper.initialize(this);



        binding.soundIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = binding.textTv.getText().toString();
                TextToSpeechHelper.speak(text);
            }
        });



        Intent intent = getIntent();
        isWritingMode = intent.getBooleanExtra("isWritingMode", false);

        binding.dailyLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity("daily");
            }
        });

        binding.objectsLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity("objects");
            }
        });

        binding.animalsLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity("animals");
            }
        });

        updateCategoryText();
    }

    private void openActivity(String category) {
        if (isWritingMode) {
            Intent intent = new Intent(CategoryActivity.this, WriteActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("selectedCategory", category);
            startActivity(intent);
        } else {
            Intent intent = new Intent(CategoryActivity.this, ReadActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        }
    }


    private void updateCategoryText() {
        boolean isDailyCompleted = isCategoryCompleted("daily");
        boolean isObjectsCompleted = isCategoryCompleted("objects");
        boolean isAnimalsCompleted = isCategoryCompleted("animals");

        StringBuilder dailyText = new StringBuilder("Günlük");
        StringBuilder objectsText = new StringBuilder("Objeler");
        StringBuilder animalsText = new StringBuilder("Hayvanlar");

        if (isDailyCompleted) {
            dailyText.append(" - Tamamlandı");
        }
        if (isObjectsCompleted) {
            objectsText.append(" - Tamamlandı");
        }
        if (isAnimalsCompleted) {
            animalsText.append(" - Tamamlandı");
        }

    }





    private boolean isCategoryCompleted(String category) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            SharedPreferences sharedPreferences = getSharedPreferences("CompletedCategories", MODE_PRIVATE);
            return sharedPreferences.getBoolean(uid + "_" + category, false);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        TextToSpeechHelper.release();
        super.onDestroy();
    }

}
