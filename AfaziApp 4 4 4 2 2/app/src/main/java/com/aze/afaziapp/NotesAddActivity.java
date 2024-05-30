package com.aze.afaziapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.aze.afaziapp.databinding.ActivityNotesAddBinding;
import com.aze.afaziapp.models.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotesAddActivity extends AppCompatActivity {
    private ActivityNotesAddBinding binding;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotesAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("notes");

        binding.saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesAddActivity.this, NotesActivity.class));

            }
        });

    }

    private void saveNote() {
        String title = binding.noteTitle.getText().toString().trim();
        String content = binding.noteContent.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Lütfen not başlığı ve içeriğini girin.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();
        String email = firebaseAuth.getCurrentUser().getEmail();

        DatabaseReference newNoteRef = databaseReference.push();
        String noteId = newNoteRef.getKey();

        Note note = new Note(noteId, title, content, userId, email);
        newNoteRef.setValue(note);

       // Toast.makeText(this, "Not kaydedildi.", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(NotesAddActivity.this,NotesActivity.class));

    }
}
