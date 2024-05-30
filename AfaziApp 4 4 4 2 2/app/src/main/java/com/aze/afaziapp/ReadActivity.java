package com.aze.afaziapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aze.afaziapp.databinding.ActivityReadBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ReadActivity extends AppCompatActivity {

    private ActivityReadBinding binding;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private List<String> words;
    private int currentIndex = 0;
    private SharedPreferences sharedPreferences;
    private Set<String> correctWords;
    private Set<String> wrongWords;

    MediaPlayer play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            String category = intent.getStringExtra("category");
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
                            binding.textTv.setText(words.get(0));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReadActivity.this, "Veri okunamadı.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        correctWords = new HashSet<>();
        wrongWords = new HashSet<>();

        binding.voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
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

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ekranda yazan kelimeyi söyleyin.");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Konuşma başlatılamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = result.get(0);

            binding.speechText.setText(spokenText);
            //  checkAnswer();
        }
    }

    private void checkAnswer() {
        if (words != null && currentIndex < words.size()) {
            String currentWord = words.get(currentIndex);
            String spokenText = binding.speechText.getText().toString().trim();

            if(spokenText.isEmpty()) {
                Toast.makeText(this, "Lütfen konuşun!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentWord.equalsIgnoreCase(spokenText)) {
              //  Toast.makeText(this, "Doğru!", Toast.LENGTH_SHORT).show();
                playCorrectSound();
                correctWords.add(currentWord);
                wrongWords.remove(currentWord);
                binding.speechText.setText("");
                showNextWord();
            } else if (currentWord != spokenText && !spokenText.isEmpty()){
                //Toast.makeText(this, "Yanlış! Tekrar deneyin.", Toast.LENGTH_SHORT).show();
                playWrongSound();
                wrongWords.add(currentWord);
                binding.speechText.setText("");
                correctWords.remove(currentWord);
            }
        }

        if(correctWords.size() == words.size()){
            Toast.makeText(this, "Tebrikler, kategoriyi bitirdiniz.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNextWord() {
        while (currentIndex < words.size()){
            String currentWord = words.get(currentIndex);
            if (!correctWords.contains(currentWord))
                break;
            currentIndex++;
        }

        if (currentIndex < words.size()){
            String nextWord = words.get(currentIndex);
            binding.textTv.setText(nextWord);
        }
    }
}
