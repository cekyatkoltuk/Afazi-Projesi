package com.aze.afaziapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.aze.afaziapp.NotesActivity;
import com.aze.afaziapp.NotesDetailActivity;
import com.aze.afaziapp.R;
import com.aze.afaziapp.models.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private Context context;
    private List<Note> noteList;
    private NoteAdapterListener listener;

    public interface NoteAdapterListener {
        void onDeleteNoteClicked(String noteId);
    }

    public NotesAdapter(Context context, List<Note> noteList, NoteAdapterListener listener) {
        this.context = context;
        this.noteList = noteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotesDetailActivity.class);
                intent.putExtra("noteId", note.getNoteId());
                intent.putExtra("noteTitle", note.getTitle());
                intent.putExtra("noteContent", note.getContent());
                intent.putExtra("noteUserId", note.getUserId());
                intent.putExtra("noteUserEmail", note.getUserEmail());
                context.startActivity(intent);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(note.getNoteId());
            }
        });
    }

    private void showDeleteConfirmationDialog(String noteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Notu Sil");
        builder.setMessage("Bu notu silmek istediğinizden emin misiniz?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDeleteNoteClicked(noteId);
            }
        });
        builder.setNegativeButton("Hayır", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView contentTextView;
        public ImageButton deleteBtn;

        public NoteViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
