package com.example.campusexpensemanagerappgroup3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "SignLog.db";
    // Tăng phiên bản sau khi thêm/sửa bảng (ví dụ: từ 2 lên 3)
    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 3);
    }

    // Tên bảng và cột (Sử dụng hằng số để tránh lỗi chính tả)
    private static final String TABLE_USERS = "users";
    private static final String TABLE_EXPENSES = "expenses";
    private static final String COL_ID = "id";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_AMOUNT = "amount";
    private static final String COL_CATEGORY = "category";
    private static final String COL_DATE = "date";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";


    @Override
    public void onCreate(SQLiteDatabase db) {

        // ----- TABLE USERS -----
        db.execSQL("CREATE TABLE " + TABLE_USERS + "(" +
                COL_EMAIL + " TEXT PRIMARY KEY, " +
                COL_PASSWORD + " TEXT)");

        // ----- TABLE EXPENSES -----
        db.execSQL("CREATE TABLE " + TABLE_EXPENSES + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_AMOUNT + " REAL, " +
                COL_CATEGORY + " TEXT, " +
                COL_DATE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);

        onCreate(db);
    }

    // ------------------- USER FUNCTIONS ---------------------

    public Boolean insertData(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EMAIL, email);
        cv.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public Boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // ------------------- EXPENSE FUNCTIONS ---------------------

    // Sửa kiểu trả về thành 'long' để tương thích với AddExpenseActivity
    public long insertExpense(String description, double amount, String category, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_DESCRIPTION, description);
        cv.put(COL_AMOUNT, amount);
        cv.put(COL_CATEGORY, category);
        cv.put(COL_DATE, date);

        long result = db.insert(TABLE_EXPENSES, null, cv);
        db.close();
        return result; // Trả về ID của hàng mới, hoặc -1 nếu lỗi
    }

    /**
     * PHƯƠNG THỨC CẦN THIẾT ĐỂ HIỂN THỊ DỮ LIỆU
     * Lấy tất cả chi tiêu từ database
     */
    public List<ViewExpensesActivity.Expense> getAllExpenses() {
        // Sử dụng List<ViewExpensesActivity.Expense> vì lớp Expense được định nghĩa bên trong ViewExpensesActivity
        List<ViewExpensesActivity.Expense> expenseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " ORDER BY " + COL_DATE + " DESC, " + COL_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Đảm bảo thứ tự cột khớp với truy vấn SELECT (theo thứ tự khai báo trong onCreate)
                int id = cursor.getInt(0);
                String description = cursor.getString(1);
                double amount = cursor.getDouble(2);
                String category = cursor.getString(3);
                String date = cursor.getString(4);

                // Khởi tạo đối tượng Expense
                ViewExpensesActivity.Expense expense = new ViewExpensesActivity.Expense(
                        id, description, amount, category, date);
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenseList;
    }
}