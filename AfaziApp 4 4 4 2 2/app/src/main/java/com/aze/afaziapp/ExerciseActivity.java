package com.aze.afaziapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.aze.afaziapp.adapters.AdapterCard;
import com.aze.afaziapp.databinding.ActivityExerciseBinding;
import com.aze.afaziapp.models.ModelCard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ExerciseActivity extends AppCompatActivity {

    private ActivityExerciseBinding binding;

    private ArrayList<ModelCard> cardArrayList;
    private ArrayList<ModelCard> wrongCardsList;
    private AdapterCard adapter;
    private int currentPosition = 0;
    private boolean allCardsKnown = false;
    private boolean isControlButtonClicked = false;

    MediaPlayer play;
    public boolean isExercise= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.controlBtn.setOnClickListener(v -> {
            if (isControlButtonClicked) {
                showNextCard();
            } else {
                checkAnswer();
            }
        });

        loadCards();

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

    private void loadCards() {
        cardArrayList = new ArrayList<>();
        wrongCardsList = new ArrayList<>();

        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cards");
        reference.orderByChild("uid").equalTo(currentUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cardArrayList.clear();
                wrongCardsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelCard modelCard = snapshot.getValue(ModelCard.class);
                    cardArrayList.add(modelCard);
                }

                if (cardArrayList.isEmpty()) {
                    binding.writeText.setText("Henüz hafıza kartın yok.");
                    binding.cardImg.setImageDrawable(null);
                    binding.controlBtn.setEnabled(false);
                } else {
                    binding.controlBtn.setEnabled(true);
                    showCard(currentPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ExerciseActivity.this, "Veriler alınamadı: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCard(int position) {
        if (position >= 0 && position < cardArrayList.size()) {
            ModelCard card = cardArrayList.get(position);
            String imageUrl = card.getDownloadurl();
            String title = card.getTitle();
            binding.writeText.setEnabled(true);

            binding.cardRl.setBackgroundResource(R.drawable.imageborder2);
            Picasso.get().load(imageUrl).into(binding.cardImg);
            binding.writeText.setText("");

            binding.controlBtn.setEnabled(true);
            if (isControlButtonClicked) {
                binding.controlBtn.setText("İlerle");
            } else {
                binding.controlBtn.setText("Kontrol Et");
            }
        }
    }

    private void checkAnswer() {
        String userInput = binding.writeText.getText().toString().trim();
        String currentImageTitle = getCurrentImageTitle();

        if (TextUtils.isEmpty(userInput)) {
            Toast.makeText(this, "Metin girin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentImageTitle.equalsIgnoreCase(userInput)) {
            isControlButtonClicked = true;
            binding.controlBtn.setText("İlerle");
            playCorrectSound();
            binding.cardRl.setBackgroundResource(R.drawable.imageborder_true);

            cardArrayList.remove(currentPosition);

            if (!allCardsKnown) {
                if (cardArrayList.isEmpty()) {
                   // Toast.makeText(ExerciseActivity.this, "Tebrikler, hepsini bildiniz!", Toast.LENGTH_SHORT).show();
                    allCardsKnown = true;
                    binding.controlBtn.setEnabled(false);

                    isExercise = true;
                    boolean control=isExercise;
                    Intent intent= new Intent(ExerciseActivity.this,ListenComprehensionResult.class);
                    intent.putExtra("veri",control);
                    startActivity(intent);
                }
            }
        } else {
            isControlButtonClicked = true;
            binding.controlBtn.setText("İlerle");
            playWrongSound();
            binding.cardRl.setBackgroundResource(R.drawable.imageborder_false);
            binding.writeText.setEnabled(false);
            binding.writeText.setText(currentImageTitle);

            wrongCardsList.add(cardArrayList.get(currentPosition));
        }
    }

    private String getCurrentImageTitle() {
        if (currentPosition >= 0 && currentPosition < cardArrayList.size()) {
            ModelCard card = cardArrayList.get(currentPosition);
            return card.getTitle();
        }
        return "";
    }

    private void showNextCard() {
        currentPosition++;

        if (allCardsKnown && currentPosition >= cardArrayList.size()) {
            binding.controlBtn.setEnabled(false);
            return;
        }

        if (currentPosition >= cardArrayList.size()) {
            currentPosition = 0;
        }

        isControlButtonClicked = false;
        showCard(currentPosition);
    }

    @Override
    protected void onDestroy() {
        TextToSpeechHelper.release();
        super.onDestroy();
    }

}
