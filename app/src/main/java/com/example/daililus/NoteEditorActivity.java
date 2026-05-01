package com.example.daililus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NoteEditorActivity extends AppCompatActivity {
    private DataBaseHelper dbHelper;
    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        dbHelper = new DataBaseHelper(this);
        Intent intent = getIntent();
        noteId = intent.getIntExtra("NOTE_ID", -1);
        String title = intent.getStringExtra("NOTE_TITLE");
        String date = intent.getStringExtra("NOTE_DATE");
        String content = intent.getStringExtra("NOTE_CONTENT");

        EditText etTitle = findViewById(R.id.etEditorTitle);
        EditText etContent = findViewById(R.id.etEditorContent);
        TextView tvDate = findViewById(R.id.tvEditorDate);
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnDelete = findViewById(R.id.btnDelete);
        etTitle.setText(title);
        etContent.setText(content);
        tvDate.setText("Дата: " + date);

        btnBack.setOnClickListener(v -> {
            String updatedTitle = etTitle.getText().toString();
            String updatedContent = etContent.getText().toString();

            if (noteId != -1){
                dbHelper.updateNote(noteId, updatedTitle, updatedContent);
            }
            finish();
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(NoteEditorActivity.this)
                    .setTitle("Удаление заметки")
                    .setMessage("Вы уверены, что хотите удалить эту заметку?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        dbHelper.deleteNote(noteId);
                        noteId = -1;
                        finish();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();

        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            View view = getCurrentFocus();
            if (view instanceof EditText){
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null){
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}