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
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_NAME = "name";

    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TASK_NAME = "task_name";
    private static final String COLUMN_IS_DONE = "is_done";
    private static final String COLUMN_DATE = "date";
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
                COLUMN_DATE + " TEXT)");
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

    public void addTask(Task task, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TASK_NAME, task.getText());
        cv.put(COLUMN_IS_DONE, task.isDone() ? 1 : 0);
        cv.put(COLUMN_DATE, date);
        db.insert(TABLE_TASKS, null, cv);
        db.close();
    }

    public List<Task> getTaskByDate(String date){
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.query(TABLE_TASKS, null, COLUMN_DATE + "=?", new String[]{date}, null, null, null);
        if (cursor != null && cursor.moveToFirst()){
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_NAME));
                boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DONE)) == 1;
                tasks.add(new Task(id, title, isDone));
            }
            while(cursor.moveToNext());
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
}
