package com.example.campusexpensemanagerappgroup3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SignLog.db";
    public static final int DATABASE_VERSION = 4;

    // --- TÊN BẢNG VÀ CỘT ---
    private static final String TABLE_USERS = "users";
    private static final String TABLE_EXPENSES = "expenses";
    private static final String TABLE_BUDGET = "budget"; // Bảng Ngân sách

    // Cột chung
    private static final String COL_ID = "id";
    // Cột EXPENSES
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_AMOUNT = "amount";
    private static final String COL_CATEGORY = "category";
    private static final String COL_DATE = "date";
    // Cột USERS
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    // Cột BUDGET (Quan trọng cho lỗi của bạn)
    private static final String COL_BUDGET_AMOUNT = "budget_amount";
    private static final String COL_BUDGET_MONTH = "budget_month";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ====================================================================
    // PHẦN 1: CÁC HÀM NGÂN SÁCH (SỬA LỖI BẠN ĐANG GẶP)
    // ====================================================================

    // Lấy chuỗi tháng hiện tại (ví dụ "2025-12")
    private String getCurrentMonthKey() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
        return monthFormat.format(Calendar.getInstance().getTime());
    }

    // 1. Hàm lưu ngân sách (setMonthlyBudget) - Hàm bạn đang thiếu!
    public boolean setMonthlyBudget(double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentMonth = getCurrentMonthKey();
        ContentValues values = new ContentValues();
        values.put(COL_BUDGET_AMOUNT, amount);
        values.put(COL_BUDGET_MONTH, currentMonth);

        // Thử update trước (nếu tháng đó đã có ngân sách)
        int rowsAffected = db.update(TABLE_BUDGET, values, COL_BUDGET_MONTH + " = ?", new String[]{currentMonth});

        // Nếu không update được (chưa có), thì insert mới
        if (rowsAffected == 0) {
            long result = db.insert(TABLE_BUDGET, null, values);
            db.close();
            return result != -1;
        }
        db.close();
        return true;
    }

    // 2. Hàm lấy ngân sách (getMonthlyBudget)
    public double getMonthlyBudget() {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentMonth = getCurrentMonthKey();
        Cursor cursor = db.rawQuery("SELECT " + COL_BUDGET_AMOUNT + " FROM " + TABLE_BUDGET + " WHERE " + COL_BUDGET_MONTH + " = ?", new String[]{currentMonth});

        double budget = 0.0;
        if (cursor.moveToFirst()) {
            budget = cursor.getDouble(0);
        }
        cursor.close();
        // Không đóng db ở đây để tránh lỗi nếu dùng lại
        return budget;
    }

    // 3. Hàm tính tổng tiền đã tiêu trong tháng (calculateTotalSpentForMonth)
    public double calculateTotalSpentForMonth() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Xác định ngày đầu và cuối tháng
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDate = dateFormat.format(calendar.getTime());

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = dateFormat.format(calendar.getTime());

        // Truy vấn tổng tiền
        String query = "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_EXPENSES +
                " WHERE " + COL_DATE + " BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate});

        double totalSpent = 0.0;
        if (cursor.moveToFirst()) {
            totalSpent = cursor.getDouble(0);
        }
        cursor.close();
        return totalSpent;
    }

    // ====================================================================
    // PHẦN 2: CÁC HÀM CHI TIÊU (CRUD)
    // ====================================================================

    public long insertExpense(String description, double amount, String category, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DESCRIPTION, description);
        cv.put(COL_AMOUNT, amount);
        cv.put(COL_CATEGORY, category);
        cv.put(COL_DATE, date);
        long result = db.insert(TABLE_EXPENSES, null, cv);
        db.close();
        return result;
    }

    public List<ExpenseModel> getAllExpenses() {
        List<ExpenseModel> expenseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " ORDER BY " + COL_DATE + " DESC, " + COL_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String description = cursor.getString(1);
                double amount = cursor.getDouble(2);
                String category = cursor.getString(3);
                String date = cursor.getString(4);
                expenseList.add(new ExpenseModel(id, description, amount, category, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return expenseList;
    }

    public int updateExpense(int expenseId, String description, double amount, String category, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DESCRIPTION, description);
        values.put(COL_AMOUNT, amount);
        values.put(COL_CATEGORY, category);
        values.put(COL_DATE, date);
        int result = db.update(TABLE_EXPENSES, values, COL_ID + " = ?", new String[]{String.valueOf(expenseId)});
        db.close();
        return result;
    }

    public int deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_EXPENSES, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    // ====================================================================
    // PHẦN 3: CÁC HÀM BÁO CÁO (Reports)
    // ====================================================================

    public double getTotalExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_EXPENSES, null);
        double total = 0.0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public Cursor getCategorySummary() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_CATEGORY + ", SUM(" + COL_AMOUNT + ") as total_amount " +
                "FROM " + TABLE_EXPENSES + " GROUP BY " + COL_CATEGORY +
                " ORDER BY total_amount DESC";
        return db.rawQuery(query, null);
    }

    public Cursor getExpensesByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSES +
                " WHERE " + COL_CATEGORY + " = ?" +
                " ORDER BY " + COL_DATE + " DESC";
        return db.rawQuery(query, new String[]{category});
    }

    // ====================================================================
    // PHẦN 4: USER & MODEL
    // ====================================================================

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

    public static class ExpenseModel {
        private int id;
        private String description;
        private double amount;
        private String category;
        private String date;

        public ExpenseModel(int id, String description, double amount, String category, String date) {
            this.id = id;
            this.description = description;
            this.amount = amount;
            this.category = category;
            this.date = date;
        }
        public int getId() { return id; }
        public String getDescription() { return description; }
        public double getAmount() { return amount; }
        public String getCategory() { return category; }
        public String getDate() { return date; }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + "(" + COL_EMAIL + " TEXT PRIMARY KEY, " + COL_PASSWORD + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_EXPENSES + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_DESCRIPTION + " TEXT, " + COL_AMOUNT + " REAL, " + COL_CATEGORY + " TEXT, " + COL_DATE + " TEXT)");
        // Tạo bảng Budget
        db.execSQL("CREATE TABLE " + TABLE_BUDGET + "(" + COL_BUDGET_MONTH + " TEXT PRIMARY KEY, " + COL_BUDGET_AMOUNT + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        onCreate(db);
    }
}