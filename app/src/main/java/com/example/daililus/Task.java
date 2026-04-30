package com.example.daililus;

public class Task {
    private int id;
    private String text;
    private boolean isDone;

    public Task(int id, String text, boolean isDone){
        this.id = id;
        this.text = text;
        this.isDone = isDone;
    }

    public int getId() { return id; }
    public String getText() { return text;}
    public boolean isDone() { return isDone;}
    public void setDone(boolean done) {isDone = done;}
}
