package com.aze.afaziapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;

import com.aze.afaziapp.adapters.AdapterCard;
import com.aze.afaziapp.databinding.ActivityMemoryCardsBinding;
import com.aze.afaziapp.models.ModelCard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MemoryCardsActivity extends AppCompatActivity {

    private ActivityMemoryCardsBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelCard> cardArrayList;
    private AdapterCard adapterCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemoryCardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadCards();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    adapterCard.getFilter().filter(s);


                }
                catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MemoryCardsActivity.this, MemoryCardsAddActivity.class));
            }
        });

        binding.exerciseBtn.setOnClickListener(v -> startActivity(new Intent(MemoryCardsActivity.this,ExerciseActivity.class)));
    }

    private void loadCards() {

        cardArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Cards");

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.orderByChild("uid").equalTo(currentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardArrayList.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelCard model= ds.getValue(ModelCard.class);

                    cardArrayList.add(model);
                }
                adapterCard = new AdapterCard(MemoryCardsActivity.this,cardArrayList);
                binding.cardRv.setAdapter(adapterCard);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}