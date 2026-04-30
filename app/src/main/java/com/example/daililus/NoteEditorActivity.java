package com.example.daililus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NoteEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        Intent intent = getIntent();
        int noteId = intent.getIntExtra("NOTE_ID", -1);
        String title = intent.getStringExtra("NOTE_TITLE");
        String date = intent.getStringExtra("NOTE_DATE");
        String content = intent.getStringExtra("NOTE_CONTENT");

        EditText etTitle = findViewById(R.id.etEditorTitle);
        EditText etContent = findViewById(R.id.etEditorContent);
        TextView tvDate = findViewById(R.id.tvEditorDate);
        etTitle.setText(title);
        etContent.setText(content);
        tvDate.setText("Дата: " + date);
    }
}