package com.aze.afaziapp.models;

public class Note {
    private String noteId;
    private String title;
    private String content;
    private String userId;
    private String userEmail;

    public Note() {
        // Boş parametresiz yapıcı metod gereklidir (Firebase veri işlemi için).
    }

    public Note(String noteId, String title, String content, String userId, String userEmail) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.userEmail = userEmail;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}

