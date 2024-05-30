package com.aze.afaziapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VisualResultActivity extends AppCompatActivity {

    private Button nextLevelButton;

    private TextView scortv;
    private int currentLevel;

    private Button againLevelBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_result);

        nextLevelButton = findViewById(R.id.newLevel);
        currentLevel = getIntent().getIntExtra("currentLevel", 0);


        scortv = findViewById(R.id.scorRltv);
        againLevelBtn= findViewById(R.id.againLevelBtn);

        if (currentLevel == 1) {
            nextLevelButton.setText("Bölüm 2'ye Geç");
            scortv.setText("Tebrikler 1.Bölümü Bitirdiniz.");

        } else if (currentLevel == 2) {
            nextLevelButton.setText("Bölüm 3'e Geç");
            scortv.setText("Tebrikler 2.Bölümü Bitirdiniz.");

        }else if(currentLevel==3){
            nextLevelButton.setText("Ana Sayfa");
            scortv.setText("Tebrikler Tüm Bölümleri Bitirdiniz.");

        }

        nextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == 1) {
                    Intent intent = new Intent(VisualResultActivity.this, VisualComprehensionActivity.class);
                    intent.putExtra("currentLevel", 2);
                    startActivity(intent);
                    finish();
                } else if (currentLevel == 2) {
                    Intent intent = new Intent(VisualResultActivity.this, VisualComprehensionActivity.class);
                    intent.putExtra("currentLevel", 3);
                    startActivity(intent);
                    finish();
                } else if (currentLevel ==3) {
                    startActivity(new Intent(VisualResultActivity.this, HomeActivity.class));
                }
            }
        });

        againLevelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == 1) {
                    Intent intent = new Intent(VisualResultActivity.this, VisualComprehensionActivity.class);
                    intent.putExtra("currentLevel", 1);
                    startActivity(intent);
                    finish();
                } else if (currentLevel == 2) {
                    Intent intent = new Intent(VisualResultActivity.this, VisualComprehensionActivity.class);
                    intent.putExtra("currentLevel", 2);
                    startActivity(intent);
                    finish();
                } else if (currentLevel==3) {

                    Intent intent = new Intent(VisualResultActivity.this, VisualComprehensionActivity.class);
                    intent.putExtra("currentLevel", 3);
                    startActivity(intent);
                    finish();

                }

            }
        });

    }
}
