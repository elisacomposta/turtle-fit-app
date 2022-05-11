package com.example.turtlefit;

public class Sport {
    private String activity;
    private String date;
    private int difficulty;
    private String notes;
    private boolean saved = false;

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Sport(String activity, String date, int difficulty, String notes, boolean saved) {
        this.activity = activity;
        this.date = date;
        this.difficulty = difficulty;
        this.notes = notes;
        this.saved = saved;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Sport(String activity) {
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
