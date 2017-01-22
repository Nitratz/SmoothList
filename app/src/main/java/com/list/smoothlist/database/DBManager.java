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
    private static final String C_ID = "id_todo";
    private static final String C_TITLE = "title";
    private static final String C_IMAGE = "image";
    private static final String C_DATE = "date";
    private static final String C_DESC = "desc";
    private static final String C_DONE = "is_done";
    private static final String C_LEVEL = "level";

    private enum COLUMNS {
        ID,
        TITLE,
        DESC,
        DATE,
        IMAGE,
        DONE,
        LEVEL,
    }

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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TODO + "("+ C_ID + " INTEGER PRIMARY KEY,"
                + C_TITLE + " TEXT," + C_DESC + " TEXT,"
                + C_DATE + " TEXT, " + C_IMAGE + " BLOB, "
                + C_DONE + " SHORT, " + C_LEVEL + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    public boolean insertNote(ToDo todo) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(C_TITLE, todo.getTitle());
        values.put(C_DESC, todo.getDesc());
        values.put(C_DONE, todo.isDone());
        values.put(C_DATE, todo.getDate());
        values.put(C_IMAGE, todo.getBannerArray());
        values.put(C_LEVEL, todo.getLevelNb());

        long id = db.insert(TABLE_TODO, null, values);
        todo.setId((int) id);
        return id != -1;
    }

    public boolean deleteNote(ToDo todo) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TABLE_TODO, C_ID + " = " + todo.getId(), null) > 0;
    }

    public boolean updateNote(ToDo todo) {
        ContentValues args = new ContentValues();
        SQLiteDatabase db = getReadableDatabase();

        args.put(C_TITLE, todo.getTitle());
        args.put(C_DESC, todo.getDesc());
        args.put(C_DONE, todo.isDone());
        args.put(C_IMAGE, todo.getBannerArray());
        args.put(C_DATE, todo.getDate());
        args.put(C_LEVEL, todo.getLevelNb());

        return db.update(TABLE_TODO, args, C_ID + " = " + todo.getId(), null) > 0;
    }

    public ArrayList<ToDo> getAllNotes() {
        ArrayList<ToDo> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_TODO, null);

        if (cursor.moveToFirst()) {
            do {
                ToDo todo = new ToDo();
                todo.setFromDB(true);
                todo.setId(cursor.getInt(COLUMNS.ID.ordinal()))
                        .setTitle(cursor.getString(COLUMNS.TITLE.ordinal()))
                        .setDesc(cursor.getString(COLUMNS.DESC.ordinal()))
                        .setDate(cursor.getString(COLUMNS.DATE.ordinal()))
                        .setBannerFromArray(cursor.getBlob(COLUMNS.IMAGE.ordinal()))
                        .setDone(cursor.getShort(COLUMNS.DONE.ordinal()))
                        .setLevelNb(cursor.getInt(COLUMNS.LEVEL.ordinal()));
                list.add(todo);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}
