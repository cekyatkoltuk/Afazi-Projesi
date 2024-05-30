package com.aze.afaziapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aze.afaziapp.databinding.ActivityVisualComprehensionBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class VisualComprehensionActivity extends AppCompatActivity {

    private ActivityVisualComprehensionBinding binding;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private int currentImageIndex;
    private List<String> imageTitles;
    private List<String> imageUrls;
    private List<String> wrongAnswerList;
    private int currentLevel;


    public boolean isVisual = false;

    MediaPlayer play;
    private boolean isControlButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVisualComprehensionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference("VisualComprehension");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("visualC");

        imageTitles = new ArrayList<>();
        imageUrls = new ArrayList<>();
        wrongAnswerList = new ArrayList<>();

        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(VisualComprehensionActivity.this,HomeActivity.class)));

        currentLevel = getIntent().getIntExtra("currentLevel", 1);

        retrieveDataFromFirebase();
        setupUI();

        TextToSpeechHelper.initialize(this);
        binding.soundIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextToSpeechHelper.speak(binding.textSp.getText().toString());
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

    private void retrieveDataFromFirebase() {
        DatabaseReference levelRef = databaseReference.child("level" + currentLevel);
        levelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> randomImageUrls = new ArrayList<>();
                ArrayList<String> randomImageTitles = new ArrayList<>();

                // Veritabanından tüm resim ve başlık verilerini al
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String imageUrl = childSnapshot.getValue(String.class);
                    String imageTitle = childSnapshot.getKey();
                    randomImageUrls.add(imageUrl);
                    randomImageTitles.add(imageTitle);
                }

                // Veritabanından alınan verilerin sayısı
                int dataSize = randomImageUrls.size();

                // Eğer veritabanında 5'ten az resim varsa, hepsini al
                int numImagesToFetch = Math.min(5, dataSize);

                // Rastgele 5 resim ve başlık seç
                Random random = new Random();
                HashSet<Integer> selectedIndexes = new HashSet<>();
                while (selectedIndexes.size() < numImagesToFetch) {
                    int randomIndex = random.nextInt(dataSize);
                    selectedIndexes.add(randomIndex);
                }

                // Seçilen rastgele resim ve başlıkları al
                for (int index : selectedIndexes) {
                    imageUrls.add(randomImageUrls.get(index));
                    imageTitles.add(randomImageTitles.get(index));
                }

                // Eğer resimler varsa ilk resmi yükle
                if (!imageUrls.isEmpty()) {
                    currentImageIndex = 0;
                    loadImageFromUrl(imageUrls.get(currentImageIndex));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hata durumunda yapılacak işlemler
            }
        });
    }


    private void setupUI() {
        binding.controlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isControlButtonClicked) {
                    nextQuestion();
                } else {
                    String userText = binding.editText.getText().toString().trim();
                    String currentImageTitle = imageTitles.get(currentImageIndex);

                    if (!userText.equalsIgnoreCase(currentImageTitle)) {
                        playWrongSound();
                        binding.editText.setText(currentImageTitle);
                        binding.editText.setEnabled(false);
                        wrongAnswerList.add(currentImageTitle);
                        binding.editText.setBackgroundResource(R.drawable.imageborder_false);

                    } else {
                       // Toast.makeText(VisualComprehensionActivity.this, "Doğru", Toast.LENGTH_SHORT).show();
                        playCorrectSound();
                        binding.editText.setBackgroundResource(R.drawable.imageborder_true);

                    }

                    isControlButtonClicked = true;
                    binding.controlBtn.setText("İlerle");
                }
            }
        });
    }

    private void loadImageFromUrl(String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .fit()
                .centerInside()
                .into(binding.imageIv);
    }

    private void nextQuestion() {
        binding.editText.setText("");
        binding.editText.setEnabled(true);
        binding.editText.setBackgroundResource(R.drawable.imageborder2);


        if (!wrongAnswerList.isEmpty()) {
            String nextWrongAnswer = wrongAnswerList.remove(0);
            int nextWrongAnswerIndex = imageTitles.indexOf(nextWrongAnswer);
            loadImageFromUrl(imageUrls.get(nextWrongAnswerIndex));
            binding.controlBtn.setText("Kontrol et");
            isControlButtonClicked = false;
        } else {
            if (currentImageIndex < imageUrls.size() - 1) {
                currentImageIndex++;
                loadImageFromUrl(imageUrls.get(currentImageIndex));
                isControlButtonClicked = false;
                binding.controlBtn.setText("Kontrol et");
            } else {
                if (wrongAnswerList.isEmpty()) {
                    //Toast.makeText(this, "Tebrikler! Level " + currentLevel + " tamamlandı.", Toast.LENGTH_SHORT).show();

                    if (currentLevel == 1) {
                        Intent intent = new Intent(VisualComprehensionActivity.this, VisualResultActivity.class);
                        intent.putExtra("currentLevel", currentLevel);
                        startActivityForResult(intent, 1);
                    } else if (currentLevel == 2) {
                        Intent intent = new Intent(VisualComprehensionActivity.this, VisualResultActivity.class);
                        intent.putExtra("currentLevel", currentLevel);
                        startActivityForResult(intent, 2);
                    } else if (currentLevel < 3) {
                        currentLevel++;
                        retrieveDataFromFirebase();
                    } else {
                        Intent intent = new Intent(VisualComprehensionActivity.this, VisualResultActivity.class);
                        intent.putExtra("currentLevel", currentLevel);
                        startActivityForResult(intent, 3);
                    }
                } else {
                    displayWrongAnswers();
                }
                binding.controlBtn.setVisibility(View.GONE);
            }
        }
    }

    private void displayWrongAnswers() {
        binding.imageIv.setVisibility(View.GONE);
        binding.editText.setVisibility(View.VISIBLE);
        binding.controlBtn.setVisibility(View.VISIBLE);
        binding.controlBtn.setText("İlerle");

        Collections.shuffle(wrongAnswerList);
        showWrongAnswers();
    }

    private void showWrongAnswers() {
        if (wrongAnswerList.isEmpty()) {
            binding.controlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextQuestion();
                }
            });
            return;
        }

        final String wrongAnswer = wrongAnswerList.get(0);
        binding.editText.setText(wrongAnswer);

        binding.controlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.editText.setText("");
                int wrongAnswerIndex = imageTitles.indexOf(wrongAnswer);
                loadImageFromUrl(imageUrls.get(wrongAnswerIndex));
                isControlButtonClicked = false;
                binding.controlBtn.setText("Kontrol et");
                wrongAnswerList.remove(0);
                showWrongAnswers();
            }
        });
    }
}
