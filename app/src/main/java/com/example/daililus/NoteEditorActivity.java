package com.example.daililus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
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
    private EditText etContent;

    private final int[] colors = {
            0xFF000000,
            0xFFFF0000,
            0xFF00FF00,
            0xFF0000FF,
            0xFFFFFF00,
            0xFFFF00FF,
            0xFF00FFFF,
            0xFFFFFFFF
    };
    private final String[] colorNames = {
            "Черный", "Красный", "Зеленый", "Синий", "Желтый", "Розовый", "Голубой", "Белый"
    };

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
        String contentHtml = intent.getStringExtra("NOTE_CONTENT");

        EditText etTitle = findViewById(R.id.etEditorTitle);
        etContent = findViewById(R.id.etEditorContent);
        TextView tvDate = findViewById(R.id.tvEditorDate);
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnDelete = findViewById(R.id.btnDelete);
        etTitle.setText(title);
        tvDate.setText("Дата: " + date);

        if(contentHtml != null){
            etContent.setText(Html.fromHtml(contentHtml, Html.FROM_HTML_MODE_LEGACY));
        }

        setupFormattingButtons();

        btnBack.setOnClickListener(v -> {
            saveAndExit(etTitle);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Удаление заметки")
                    .setMessage("Вы уверены?")
                    .setPositiveButton("Удалить", (d, w) -> {
                        dbHelper.deleteNote(noteId);
                        finish();
                    })
                    .setNegativeButton("Отмена", null).show();
        });

    }

    private void setupFormattingButtons(){
        findViewById(R.id.btnBold).setOnClickListener(v -> toggleSpan(new StyleSpan(Typeface.BOLD)));
        findViewById(R.id.btnItalic).setOnClickListener(v -> toggleSpan(new StyleSpan(Typeface.ITALIC)));
        findViewById(R.id.btnUnderline).setOnClickListener(v -> toggleSpan(new UnderlineSpan()));
        findViewById(R.id.btnStrikeThrough).setOnClickListener(v -> toggleSpan(new StrikethroughSpan()));

        findViewById(R.id.btnH1).setOnClickListener(v -> toggleSpan(new AbsoluteSizeSpan(24, true))); // Размер 24sp
        findViewById(R.id.btnH2).setOnClickListener(v -> toggleSpan(new AbsoluteSizeSpan(20, true)));

        findViewById(R.id.btnCheckbox).setOnClickListener(v -> {
            int start = etContent.getSelectionStart();
            etContent.getText().insert(start, "\n●  ");
        });

        findViewById(R.id.btnTextColor).setOnClickListener(v -> {
            showColorPicker(false);
        });

        findViewById(R.id.btnTextBackground).setOnClickListener(v -> {
            showColorPicker(true);
        });
    }

    public void toggleSpan(Object span){
        int start = etContent.getSelectionStart();
        int end = etContent.getSelectionEnd();
        if (start == end){
            android.widget.Toast.makeText(this, "Сначала выделите текст!", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        if (start != end){
            Spannable spannable = etContent.getText();
            spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void showColorPicker(boolean isBackground){
        int start = etContent.getSelectionStart();
        int end = etContent.getSelectionEnd();

        if (start == end) {
            android.widget.Toast.makeText(this, "Сначала выделите текст!", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isBackground ? "Цвет выделения (например Желтый)" : "Цвет текста (например Красный)");
        builder.setItems(colorNames, ((dialog, which) -> {
            int selectedColor = colors[which];

                Spannable spannable = etContent.getText();
                if(isBackground){
                    spannable.setSpan(new BackgroundColorSpan(selectedColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else{
                    spannable.setSpan(new ForegroundColorSpan(selectedColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
        }));
        builder.show();

    }

    private void saveAndExit(EditText etTitle){
        String updatedTitle = etTitle.getText().toString();
        String updatedContentHtml = Html.toHtml(etContent.getText(), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);

        if (noteId != -1){
            dbHelper.updateNote(noteId, updatedTitle, updatedContentHtml);
        }
        else{
            TextView tvDate = findViewById(R.id.tvEditorDate);
            String currentDate = tvDate.getText().toString().replace("Дата: ", "");
            if (currentDate.trim().isEmpty()) {
                currentDate = "02.05.2026";
            }

            String userEmail = getIntent().getStringExtra("USER_EMAIL");
            if (userEmail == null) userEmail = "user@example.com";

            dbHelper.addNote(updatedTitle, updatedContentHtml, currentDate, userEmail);
        }
        finish();
    }


//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event){
//        if(event.getAction() == MotionEvent.ACTION_DOWN){
//            View view = getCurrentFocus();
//            if (view instanceof EditText){
//                Rect outRect = new Rect();
//                view.getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
//                    view.clearFocus();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (imm != null){
//                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                    }
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event);
//    }
}