package com.list.smoothlist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.list.smoothlist.model.ToDo;

import java.util.ArrayList;
import java.util.Collections;

public class DBManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DBLIST.db";

    private static final String TABLE_TODO = "todo";
    private static final String C_TITLE = "title";
    private static final String C_DESC = "desc";
    private static final String C_LEVEL = "level";

    private static DBManager INSTANCE = null;

    private DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBManager getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new DBManager(context);
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TODO + "(id_todo INTEGER PRIMARY KEY," + "title TEXT, desc TEXT, level INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertNote(ToDo todo) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(C_TITLE, todo.getTitle());
        values.put(C_DESC, todo.getDesc());
        values.put(C_LEVEL, todo.getLevelNb());

        return db.insert(TABLE_TODO, null, values) != -1;
    }

    public ArrayList<ToDo> getAllNotes() {
        ArrayList<ToDo> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_TODO, null);

        if (cursor.moveToFirst()) {
            do {
                ToDo todo = new ToDo();
                todo.setFromDB(true);
                todo.setTitle(cursor.getString(1));
                todo.setDesc(cursor.getString(2));
                todo.setLevelNb(cursor.getInt(3));
                list.add(todo);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}
