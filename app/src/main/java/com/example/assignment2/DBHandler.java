package com.example.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "addresses_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ADDRESSES = "addresses";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ADDRESSES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ADDRESS + " TEXT, "
                + COLUMN_LATITUDE + " TEXT, "
                + COLUMN_LONGITUDE + " TEXT) ";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESSES);
        onCreate(db);
    }

    // Retrieves all entries from database
    public Cursor getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADDRESSES + " ORDER BY " + COLUMN_ID + " DESC";
        return db.rawQuery(query, null);
    }

    // Searches for address in database and returns if found
    public Cursor getAddress (String addressVal) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT * FROM " + TABLE_ADDRESSES + " WHERE " + COLUMN_ADDRESS +
                " LIKE '%" + addressVal + "%'";
        return db.rawQuery(query, null);
    }

    // Adds entry to database
    public void addAddress(String address, String latitude, String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        db.insert(TABLE_ADDRESSES, null, values);
    }

    // Modifies address entry in database
    public void updateAddress(int id, String address, String latitude, String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        db.update(TABLE_ADDRESSES, values, "id=?",
                new String[]{String.valueOf(id)});
    }

    // Removes address entry from database
    public void deleteAddress(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADDRESSES, "id = ?", new String[]{String.valueOf(id)});
    }

    // Checks if database is empty and returns true if empty
    // Used in main activity to determine if database needs propagation
    public boolean isDatabaseEmpty() {
        SQLiteDatabase db = this.getReadableDatabase(); // Open database in read mode
        Cursor cursor = null;
        boolean isEmpty = true;

        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM addresses", null);
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0); // Get the row count
                isEmpty = (count == 0); // Check if row count is zero
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Close cursor to avoid memory leaks
            }
            db.close(); // Close the database connection
        }
        return isEmpty;
    }

}