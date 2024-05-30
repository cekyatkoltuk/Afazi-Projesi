package com.aze.afaziapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.aze.afaziapp.databinding.ActivityListenComprehensionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListenComprehensionActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private ActivityListenComprehensionBinding binding;
    private DatabaseReference databaseReference;
    private List<AppCompatButton> optionButtons;
    private String currentStoryKey;
    private int currentQuestionIndex = 0;
    private int totalQuestions;
    private int userQuestion = 0;
    private int firebaseQuestion = 0;

    private int questionCount = 0;

    private int userCorrectCount = 0;
    private int userWrongCount = 0;

    private boolean isLastQuestion;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;

    private String currentUserUID;

    MediaPlayer play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListenComprehensionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.soundIb.setOnClickListener(v -> {
            String text = binding.textTv.getText().toString();
            speakText(text);
        });

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.getDefault());
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Dil desteklenmiyor.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "TextToSpeech başlatılamadı.", Toast.LENGTH_SHORT).show();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        currentUserUID = firebaseAuth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Stories");

        optionButtons = new ArrayList<>();
        optionButtons.add(binding.option1);
        optionButtons.add(binding.option2);
        optionButtons.add(binding.option3);
        optionButtons.add(binding.option4);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedStoryKey = preferences.getString(currentUserUID + "_currentStoryKey", null);
        if (savedStoryKey != null) {
            loadStory(savedStoryKey);
        } else {
            loadStory("story1");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(currentUserUID + "_currentStoryKey", currentStoryKey);
        editor.apply();
    }

    private void speakText(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void playCorrectSound() {
        play = MediaPlayer.create(this, R.raw.dogruses);
        play.start();
    }

    public void playWrongSound() {
        play = MediaPlayer.create(this, R.raw.yanlisses);
        play.start();
    }

    private void loadStory(String storyKey) {
        binding.newStoryRl.setVisibility(View.GONE);
        binding.allRl.setVisibility(View.VISIBLE);
        binding.nextBtn.setEnabled(true);
        userQuestion = 0;
        binding.scorRltv.setText("");
        userWrongCount = 0;
        userCorrectCount = 0;
        currentStoryKey = storyKey;
        currentQuestionIndex = 1;
        isLastQuestion = false;

        databaseReference.child(storyKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> storyData = (Map<String, Object>) dataSnapshot.getValue();

                if (storyData != null) {
                    String content = (String) storyData.get("content");
                    binding.textTv.setText(content);

                    totalQuestions = (int) dataSnapshot.child("questions").getChildrenCount();
                    firebaseQuestion = totalQuestions;

                    String initialText = binding.textTv.getText().toString();
                    speakText(initialText);

                    loadQuestion(storyData, "question1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListenComprehensionActivity.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNextStory() {
        binding.newStoryRl.setVisibility(View.GONE);
        binding.allRl.setVisibility(View.VISIBLE);
        binding.nextBtn.setEnabled(true);
        userQuestion = 0;
        binding.scorRltv.setText("");
        userWrongCount = 0;
        userCorrectCount = 0;

        String previousStoryKey = currentStoryKey;

        String nextStoryKey;
        if (previousStoryKey.equals("story1")) {
            nextStoryKey = "story2";
        } else if (previousStoryKey.equals("story2")) {
            nextStoryKey = "story3";
        } else if (previousStoryKey.equals("story3")) {
            nextStoryKey = "story4";
        } else if (previousStoryKey.equals("story4")) {
            nextStoryKey = "story5";
        } else if (previousStoryKey.equals("story5")) {
            nextStoryKey = "story6";
        } else {
            nextStoryKey = "";
        }

        loadStory(nextStoryKey);
    }

    private void loadQuestion(Map<String, Object> storyData, String questionKey) {
        String question = (String) storyData.get(questionKey);
        Map<String, String> options = (Map<String, String>) storyData.get(questionKey + "Options");
        String answer = (String) storyData.get(questionKey + "Answer");

        questionCount++;

        binding.scorTv.setText(questionCount + "/3");

        if (questionCount == 3) {
            questionCount = 0;
        }

        List<String> optionValues = new ArrayList<>(options.values());
        Collections.shuffle(optionValues);

        binding.questionTv.setText(question);

        int index = 0;
        for (String option : optionValues) {
            optionButtons.get(index).setText(option);
            index++;
        }

        int greenColor = ContextCompat.getColor(this, R.color.bgcolor01_);
        int redColor = ContextCompat.getColor(this, R.color.redBtn);

        for (AppCompatButton button : optionButtons) {
            button.setOnClickListener(v -> {
                String selectedAnswer = button.getText().toString();
                boolean isAnswerCorrect = selectedAnswer.equals(answer);

                if (isAnswerCorrect) {
                    button.setBackgroundTintList(ColorStateList.valueOf(greenColor));
                    userCorrectCount++;
                    playCorrectSound();
                } else {
                    button.setBackgroundTintList(ColorStateList.valueOf(redColor));
                    playWrongSound();

                    for (AppCompatButton optionButton : optionButtons) {
                        if (optionButton.getText().toString().equals(answer)) {
                            optionButton.setBackgroundTintList(ColorStateList.valueOf(greenColor));
                            break;
                        }
                    }
                    userWrongCount++;
                }

                setOptionButtonsEnabled(false);
                userQuestion++;
                checkQuestionCount();
            });
        }

        binding.nextBtn.setOnClickListener(v -> {
            boolean isAnyOptionSelected = false;
            for (AppCompatButton button : optionButtons) {
                if (button.getBackgroundTintList() != null &&
                        button.getBackgroundTintList().getDefaultColor() == ColorStateList.valueOf(greenColor).getDefaultColor()) {
                    isAnyOptionSelected = true;
                    break;
                }
            }

            if (!isAnyOptionSelected) {
                Toast.makeText(this, "Lütfen soruyu cevaplayınız.", Toast.LENGTH_SHORT).show();
            } else {
                if (isLastQuestion) {
                    boolean isAnswerCorrect = optionButtons.get(0).getText().toString().equals(answer);

                    if (isAnswerCorrect) {
                        optionButtons.get(0).setBackgroundTintList(ColorStateList.valueOf(greenColor));
                    } else {
                        optionButtons.get(0).setBackgroundTintList(ColorStateList.valueOf(redColor));

                        for (AppCompatButton optionButton : optionButtons) {
                            if (optionButton.getText().toString().equals(answer)) {
                                optionButton.setBackgroundTintList(ColorStateList.valueOf(greenColor));
                                break;
                            }
                        }
                    }

                    setOptionButtonsEnabled(false);
                    binding.nextBtn.setEnabled(false);
                    userQuestion++;
                    checkQuestionCount();
                } else {
                    String nextQuestionKey = "question" + (currentQuestionIndex + 1);
                    loadQuestion(storyData, nextQuestionKey);
                    currentQuestionIndex++;
                    isLastQuestion = (currentQuestionIndex == totalQuestions);
                }
            }
        });

        resetOptionButtonColors();
        setOptionButtonsEnabled(true);
    }

    private void resetOptionButtonColors() {
        for (AppCompatButton button : optionButtons) {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C0C0C0")));
        }
    }

    private void setOptionButtonsEnabled(boolean enabled) {
        for (AppCompatButton button : optionButtons) {
            button.setEnabled(enabled);
        }
    }

    private void checkQuestionCount() {
        DatabaseReference questionRef = databaseReference.child(currentStoryKey).child("questions");
        questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int firebaseQuestionCount = (int) dataSnapshot.getChildrenCount() - 1;
                if (userQuestion == 3) {
                    binding.nextBtn.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.allRl.setVisibility(View.GONE);
                            binding.newStoryRl.setVisibility(View.VISIBLE);
                            binding.scorRltv.setText("Tebrikler bu hikayeyi bitirdiniz. Doğru Sayısı:" + userCorrectCount + "\nYanlış Sayısı:" + userWrongCount);
                            String previousStoryKey = currentStoryKey;

                            List<String> storyKeys = Arrays.asList("story1", "story2", "story3", "story4", "story5", "story6");

                            if (storyKeys.contains(previousStoryKey)) {
                                int currentIndex = storyKeys.indexOf(previousStoryKey);
                                if (currentIndex == storyKeys.size() - 1) {
                                    // Last story completed
                                    binding.scorRltv.setText("Tebrikler tüm hikayeleri bitirdiniz!");
                                    binding.nextStoryBtn.setText("Ana Menüye Dön");
                                    binding.nextStoryBtn.setVisibility(View.VISIBLE);
                                    binding.againStoryBtn.setOnClickListener(v -> loadStory(currentStoryKey));
                                    binding.nextStoryBtn.setOnClickListener(v -> {
                                        resetProgress();
                                        startActivity(new Intent(ListenComprehensionActivity.this, HomeActivity.class));
                                        finish();
                                    });
                                } else {
                                    binding.nextStoryBtn.setText("Sonraki Hikaye");
                                    binding.nextStoryBtn.setVisibility(View.VISIBLE);
                                    binding.nextStoryBtn.setOnClickListener(v -> loadNextStory());
                                    binding.againStoryBtn.setOnClickListener(v -> loadStory(currentStoryKey));
                                }
                            } else {
                                startActivity(new Intent(ListenComprehensionActivity.this, AccountFragment.class));
                            }
                        }
                    }, 1000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListenComprehensionActivity.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetProgress() {
        // Reset any variables tracking the current story progress
        currentStoryKey = "story1"; // or whatever the initial story key is
        userQuestion = 0;
        userCorrectCount = 0;
        userWrongCount = 0;
    }

}
