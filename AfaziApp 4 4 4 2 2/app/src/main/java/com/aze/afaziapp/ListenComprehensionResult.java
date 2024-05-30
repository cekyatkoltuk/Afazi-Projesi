    package com.aze.afaziapp;

    import android.content.Intent;
    import android.os.Bundle;

    import androidx.appcompat.app.AppCompatActivity;

    import com.aze.afaziapp.databinding.ActivityListenComprehensionResultBinding;

    public class ListenComprehensionResult extends AppCompatActivity {

        private ActivityListenComprehensionResultBinding binding;
        public static boolean isNewStory=false;
        private int currentLevel;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityListenComprehensionResultBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            binding.newStoryBtn.setOnClickListener(v -> {
                Intent intent = new Intent(ListenComprehensionResult.this, ListenComprehensionActivity.class);
                startActivity(intent);//
            });

            binding.againBtn.setOnClickListener(v -> {

                Intent intent = new Intent(ListenComprehensionResult.this, ListenComprehensionActivity.class);
                startActivity(intent);
                finish();
            });

            Intent intent = getIntent();
            boolean control = intent.getBooleanExtra("veri",false);
            if (control){
                binding.textTv.setText("Tebrikler! Tüm kartları tamamladınız.");
                binding.newStoryBtn.setText("ana sayfa");
                binding.newStoryBtn.setOnClickListener(v -> startActivity(new Intent(ListenComprehensionResult.this,HomeActivity.class)));
                binding.againBtn.setText("tekrar et");
                binding.againBtn.setOnClickListener(v -> startActivity(new Intent(ListenComprehensionResult.this,ExerciseActivity.class)));
            }


            currentLevel = getIntent().getIntExtra("currentLevel", 0);

            if (currentLevel == 1) {
                binding.newStoryBtn.setText("Bölüm 2'ye Geç");
            } else if (currentLevel == 2) {
                binding.newStoryBtn.setText("Bölüm 3'e Geç");
            }
            else if (currentLevel == 3) {
                binding.newStoryBtn.setText("Bölüm 4'e Geç");
            }
            else if (currentLevel == 4) {
                binding.newStoryBtn.setText("Bölüm 5'e Geç");
            }
            else if (currentLevel == 5) {
                binding.newStoryBtn.setText("Bölüm 6'e Geç");
            }

        }
    }