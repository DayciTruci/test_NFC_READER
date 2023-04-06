package com.example.test1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "activity_db";
    private static final String TABLE_NAME = "activity";
    private static final String ID_COLUMN = "id";
    private static final String ACTIVITY_COLUMN = "activity";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + ID_COLUMN + " TEXT PRIMARY KEY,"
            + ACTIVITY_COLUMN + " TEXT" + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addData(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, data.getId());
        values.put(ACTIVITY_COLUMN, data.getActivity());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateData(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, data.getId());
        values.put(ACTIVITY_COLUMN, data.getActivity());
        db.update(TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{data.getId()});
        db.close();
    }

    public void deleteData(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID_COLUMN + " = ?", new String[]{data.getId()});
        db.close();
    }

    public Data getData(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID_COLUMN, ACTIVITY_COLUMN}, ID_COLUMN + "=?",
                new String[]{id}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Data data = new Data(cursor.getString(0), cursor.getString(1));
        cursor.close();
        db.close();
        return data;
    }

    public boolean insert_check(String id) {
        boolean check = false;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID_COLUMN, ACTIVITY_COLUMN}, ID_COLUMN + "=?",
                new String[]{id}, null, null, null, null);

        if (cursor.moveToFirst())
            check = true;

        cursor.close();
        db.close();
        return check;
    }
}
