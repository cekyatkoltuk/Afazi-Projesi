package com.aze.afaziapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aze.afaziapp.databinding.ActivityWriteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class WriteActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;

    private ActivityWriteBinding binding;
    private int currentIndex = 0;

    private List<String> words;
    private Set<String> correctWords;
    private Set<String> wrongWords;

    private String selectedCategory;
    private String targetText;
    private SharedPreferences sharedPreferences;
    MediaPlayer play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        Intent intent = getIntent();
        if (intent != null) {
            String category = intent.getStringExtra("category");
            selectedCategory = intent.getStringExtra("selectedCategory");
            if (category != null) {
                DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Readwords").child(category);
                categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        words = new ArrayList<>();
                        for (DataSnapshot wordSnapshot : snapshot.getChildren()) {
                            String word = wordSnapshot.getValue(String.class);
                            words.add(word);
                        }
                        if (words.size() > 0) {
                            showCurrentWord();
                            String text = binding.textTv.getText().toString();
                            speakText(text);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(WriteActivity.this, "Veri okunamadı.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        correctWords = new HashSet<>();
        wrongWords = new HashSet<>();

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(WriteActivity.this, "TextToSpeech dil desteği mevcut değil.", Toast.LENGTH_SHORT).show();
                    } else {
                        String text = binding.textTv.getText().toString();
                        targetText = text;
                        speakText(text);
                    }
                } else {
                    Toast.makeText(WriteActivity.this, "TextToSpeech başlatılamadı.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        binding.soundIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = binding.textTv.getText().toString();
                speakText(text);
            }
        });

        binding.controlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public void playCorrectSound() {
        play = MediaPlayer.create(this, R.raw.dogruses);

        play.start();
    }

    public void playWrongSound() {
        play = MediaPlayer.create(this, R.raw.yanlisses);

        play.start();
    }

    private void checkAnswer() {
        if (words != null && currentIndex < words.size()) {
            String currentWord = words.get(currentIndex);
            String writeText = binding.writeText.getText().toString().trim();

            if (writeText.isEmpty()) {
                Toast.makeText(this, "Lütfen duyduğunuzu yazın!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentWord.equalsIgnoreCase(writeText)) {
                playCorrectSound();
                correctWords.add(currentWord);
                wrongWords.remove(currentWord);
                binding.writeText.setText("");
                showNextWord();

            } else if (!writeText.isEmpty()) {
                //Toast.makeText(this, "Yanlış! Tekrar deneyin.", Toast.LENGTH_SHORT).show();
                playWrongSound();

                wrongWords.add(currentWord);
                binding.writeText.setText("");
                correctWords.remove(currentWord);
            }
        }

        if (correctWords.size() == words.size()) {
            showResetDialog();
        }
    }

    private void showCurrentWord() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            int savedIndex = getSavedIndex(uid);

            if (savedIndex != -1 && savedIndex < words.size()) {
                currentIndex = savedIndex;
                binding.textTv.setText(words.get(currentIndex));
            } else {
                binding.textTv.setText(words.get(0));
            }
        }
    }

    private int getSavedIndex(String uid) {
        sharedPreferences = getSharedPreferences("WriteActivity", MODE_PRIVATE);
        return sharedPreferences.getInt(uid, -1);
    }

    private void saveIndex(String uid, int index) {
        sharedPreferences = getSharedPreferences("WriteActivity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(uid, index);
        editor.apply();
    }

    private void showNextWord() {


        currentIndex++;
        if (currentIndex < words.size()) {
            String nextWord = words.get(currentIndex);
            binding.textTv.setText(nextWord);
            speakText(nextWord);

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();
                saveIndex(uid, currentIndex);
            }
        } else {
            showResetDialog();
        }
    }

    private void speakText(String text) {
        if (textToSpeech != null && !text.isEmpty()) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void showResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Kategori Tamamlandı")
                .setMessage("Kategori tamamlandı. Baştan başlamak ister misiniz?")
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetCategory(true);
                    }
                })
                .setNegativeButton("Hayır",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetCategory(false);
                    }
                })
                .show();
    }

    private void resetCategory(boolean isCompleted) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            saveIndex(uid, words.size());
            saveCategoryCompletion(uid, selectedCategory, isCompleted);
        }
        Intent intent = new Intent(WriteActivity.this, CategoryActivity.class);
        intent.putExtra("isWritingMode", true);
        startActivity(intent);
        finish();
    }

    private void saveCategoryCompletion(String uid, String category, boolean isCompleted) {
        SharedPreferences sharedPreferences = getSharedPreferences("CompletedCategories", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(uid + "_" + category, isCompleted);
        editor.apply();
    }
}
