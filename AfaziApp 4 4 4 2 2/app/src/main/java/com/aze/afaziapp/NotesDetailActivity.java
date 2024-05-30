package com.aze.afaziapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aze.afaziapp.models.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotesDetailActivity extends AppCompatActivity {
    private EditText noteTitleEditText;
    private EditText noteContentEditText;
    private ImageButton saveButton;

    private DatabaseReference databaseReference;

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);

        noteTitleEditText = findViewById(R.id.noteTitle);
        noteContentEditText = findViewById(R.id.noteContent);
        saveButton = findViewById(R.id.backBtn);

        databaseReference = FirebaseDatabase.getInstance().getReference("notes");

        // Not verisini al
        Intent intent = getIntent();
        // Not verisini al
        String noteId = intent.getStringExtra("noteId");
        String noteTitle = intent.getStringExtra("noteTitle");
        String noteContent = intent.getStringExtra("noteContent");
        String noteUserId = intent.getStringExtra("noteUserId");
        String noteUserEmail = intent.getStringExtra("noteUserEmail");

        note = new Note(noteId, noteTitle, noteContent, noteUserId, noteUserEmail);

        if (note != null) {
            noteTitleEditText.setText(note.getTitle());
            noteContentEditText.setText(note.getContent());
        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNoteChanges();
            }
        });
    }

    private void saveNoteChanges() {
        String title = noteTitleEditText.getText().toString().trim();
        String content = noteContentEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Lütfen not başlığı ve içeriğini girin.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Değişiklikleri güncelle
        note.setTitle(title);
        note.setContent(content);

        // Notu veritabanına güncelle
        DatabaseReference noteRef = databaseReference.child(note.getNoteId());
        noteRef.child("title").setValue(title);
        noteRef.child("content").setValue(content);

        //Toast.makeText(this, "Değişiklikler kaydedildi.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NotesDetailActivity.this, NotesActivity.class);
        intent.putExtra("noteId", note.getNoteId());
        intent.putExtra("noteTitle", title);
        intent.putExtra("noteContent", content);
        intent.putExtra("noteUserId", note.getUserId());
        intent.putExtra("noteUserEmail", note.getUserEmail());
        startActivity(intent);
        finish();

    }

}
