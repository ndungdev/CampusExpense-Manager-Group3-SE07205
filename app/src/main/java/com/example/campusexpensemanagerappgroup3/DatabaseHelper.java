package com.example.campusexpensemanagerappgroup3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "SignLog.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 2);   // IMPORTANT: increase version to update DB
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ----- TABLE USERS -----
        db.execSQL("CREATE TABLE users(" +
                "email TEXT PRIMARY KEY, " +
                "password TEXT)");

        // ----- TABLE EXPENSES -----
        db.execSQL("CREATE TABLE expenses(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "description TEXT, " +
                "amount REAL, " +
                "category TEXT, " +
                "date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS expenses");

        onCreate(db);
    }

    // ------------------- USER FUNCTIONS ---------------------

    public Boolean insertData(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("password", password);

        long result = db.insert("users", null, cv);
        return result != -1;
    }

    public Boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        return cursor.getCount() > 0;
    }

    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        return cursor.getCount() > 0;
    }

    // ------------------- EXPENSE FUNCTIONS ---------------------

    public boolean insertExpense(String description, double amount, String category, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("description", description);
        cv.put("amount", amount);
        cv.put("category", category);
        cv.put("date", date);

        long result = db.insert("expenses", null, cv);
        return result != -1;
    }


}
