package com.ilkomp.catatanku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.ilkomp.catatanku.model;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "notes_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(model.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + model.TABLE_NAME);

        onCreate(db);
    }

    public long insertNote(String note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(model.COLUMN_NOTE, note);

        long id = db.insert(model.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public model getNote(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(model.TABLE_NAME,
                new String[]{model.COLUMN_ID, model.COLUMN_NOTE, model.COLUMN_TIMESTAMP},
                model.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        model note = new model(
                cursor.getInt(cursor.getColumnIndex(model.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(model.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(model.COLUMN_TIMESTAMP)));

        cursor.close();

        return note;
    }

    public List<model> getAllNotes() {
        List<model> notes = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + model.TABLE_NAME + " ORDER BY " +
                model.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                model note = new model();
                note.setId(cursor.getInt(cursor.getColumnIndex(model.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(model.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(model.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        db.close();

        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + model.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        return count;
    }

    public int updateNote(model note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(model.COLUMN_NOTE, note.getNote());

        return db.update(model.TABLE_NAME, values, model.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(model note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(model.TABLE_NAME, model.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
