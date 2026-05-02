package com.example.daililus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DaililusDB";
    private static final int DATABASE_VERSION = 7;
    private static final String COLUMN_USER_EMAIL = "user_email";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_NAME = "name";

    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TASK_NAME = "task_name";
    private static final String COLUMN_IS_DONE = "is_done";
    private static final String COLUMN_DATE = "date";

    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_NOTE_ID = "id";
    private static final String COLUMN_NOTE_TITLE = "title";
    private static final String COLUMN_NOTE_DATE = "date";

    private static final String COLUMN_NOTE_CONTENT = "content";

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                COLUMN_NAME + " TEXT)";
        db.execSQL(createTable);

        db.execSQL("CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK_NAME + " TEXT, " +
                COLUMN_IS_DONE + " INTEGER, "+
                COLUMN_DATE + " TEXT, " +
                COLUMN_USER_EMAIL + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTE_TITLE + " TEXT, " +
                COLUMN_NOTE_DATE + " TEXT, " +
                COLUMN_NOTE_CONTENT + " TEXT, " +
                COLUMN_USER_EMAIL + " TEXT)");
    }

    public String getUserName(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String name = null;

        Cursor cursor = db.query("users",
                new String[]{"name"},
                "email = ?",
                new String[]{email},
                null, null, null);

        if (cursor != null){
           if(cursor.moveToFirst()){
               name = cursor.getString(0);
           }
           cursor.close();
        }
        return name;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES );
        onCreate(db);
    }

    public void saveUser(String email, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_NAME, name);

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public void updateUserName(String email, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);

        db.update(TABLE_USERS, cv, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();
    }
    public void addTask(Task task, String date, String userEmail){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TASK_NAME, task.getText());
        cv.put(COLUMN_IS_DONE, task.isDone() ? 1 : 0);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_USER_EMAIL, userEmail);
        db.insert(TABLE_TASKS, null, cv);
        db.close();
    }

    public List<Task> getTaskByDate(String date, String userEmail){
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null,
                COLUMN_DATE + "=? AND " + COLUMN_USER_EMAIL + "=?",
                new String[]{date, userEmail}, null, null, null);
        if (cursor != null && cursor.moveToFirst()){
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_NAME));
                boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DONE)) == 1;
                tasks.add(new Task(id, title, isDone));
            } while(cursor.moveToNext());
            cursor.close();
        }
        return tasks;
    }

    public void updateTaskStatus(int id, boolean isDone){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IS_DONE, isDone ? 1 : 0);

        db.update(TABLE_TASKS, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void addNote(String title,String content, String date, String userEmail){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOTE_TITLE, title);
        cv.put(COLUMN_NOTE_CONTENT, content);
        cv.put(COLUMN_NOTE_DATE, date);
        cv.put(COLUMN_USER_EMAIL, userEmail);
        db.insert(TABLE_NOTES, null, cv);
        db.close();
    }

    public List<Note> getAllNotes(String userEmail  ){
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null,
                COLUMN_USER_EMAIL + "=?",
                new String[]{userEmail}, null, null, COLUMN_NOTE_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()){
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_DATE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT));
                notes.add(new Note(id, title, date, content));
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return notes;
    }

    public void updateNote(int id, String title, String content){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NOTE_TITLE, title);
        cv.put(COLUMN_NOTE_CONTENT, content);
        db.update(TABLE_NOTES, cv, COLUMN_NOTE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteNote(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_NOTE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

}
