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

    public static final String databaseName = "SignLog.db";
    // Tăng phiên bản để kích hoạt onUpgrade, đảm bảo các bảng mới nhất được tạo
    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 3);
    }

    // Tên bảng và cột
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

        // Tạo index cho cột DATE để tăng tốc độ truy vấn theo ngày
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_date ON " + TABLE_EXPENSES + "(" + COL_DATE + ")");

        // Tạo index cho cột CATEGORY để tăng tốc độ truy vấn theo danh mục
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_category ON " + TABLE_EXPENSES + "(" + COL_CATEGORY + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các index cũ
        db.execSQL("DROP INDEX IF EXISTS idx_date");
        db.execSQL("DROP INDEX IF EXISTS idx_category");

        // Xóa các bảng cũ
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);

        // Tạo lại bảng và index
        onCreate(db);
    }

    // ------------------- USER FUNCTIONS (Đăng ký/Đăng nhập) ---------------------

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

    // ------------------- EXPENSE FUNCTIONS (CRUD) ---------------------

    // Thêm chi tiêu (CREATE)
    public long insertExpense(String description, double amount, String category, String date) {
        long result = -1;

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put(COL_DESCRIPTION, description);
            cv.put(COL_AMOUNT, amount);
            cv.put(COL_CATEGORY, category);
            cv.put(COL_DATE, date);

            result = db.insert(TABLE_EXPENSES, null, cv);
            // Không đóng db - SQLiteOpenHelper tự quản lý
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Xem chi tiêu (READ)
    public List<ViewExpensesActivity.Expense> getAllExpenses() {
        List<ViewExpensesActivity.Expense> expenseList = new ArrayList<>();
        // Sắp xếp theo ngày giảm dần
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " ORDER BY " + COL_DATE + " DESC, " + COL_ID + " DESC";

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase(); // Dùng getReadableDatabase cho SELECT
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COL_ID);
                int descIndex = cursor.getColumnIndexOrThrow(COL_DESCRIPTION);
                int amountIndex = cursor.getColumnIndexOrThrow(COL_AMOUNT);
                int categoryIndex = cursor.getColumnIndexOrThrow(COL_CATEGORY);
                int dateIndex = cursor.getColumnIndexOrThrow(COL_DATE);

                do {
                    int id = cursor.getInt(idIndex);
                    String description = cursor.getString(descIndex);
                    double amount = cursor.getDouble(amountIndex);
                    String category = cursor.getString(categoryIndex);
                    String date = cursor.getString(dateIndex);

                    // Khởi tạo đối tượng Expense
                    ViewExpensesActivity.Expense expense = new ViewExpensesActivity.Expense(
                            id, description, amount, category, date);
                    expenseList.add(expense);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Đảm bảo đóng cursor
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            // Không đóng db ở đây vì SQLiteOpenHelper quản lý connection pool
        }

        return expenseList;
    }

    // Chỉnh sửa chi tiêu (UPDATE)
    public int updateExpense(int id, String description, double amount, String category, String date) {
        int result = 0;

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COL_DESCRIPTION, description);
            values.put(COL_AMOUNT, amount);
            values.put(COL_CATEGORY, category);
            values.put(COL_DATE, date);

            result = db.update(
                    TABLE_EXPENSES,
                    values,
                    COL_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
            // Không đóng db - SQLiteOpenHelper tự quản lý
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Xóa chi tiêu (DELETE)
    public int deleteExpense(int id) {
        int result = 0;

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            result = db.delete(
                    TABLE_EXPENSES,
                    COL_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
            // Không đóng db - SQLiteOpenHelper tự quản lý
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // ------------------- SUMMARY & REPORTING FUNCTIONS ---------------------

    /**
     * Tính tổng số tiền của tất cả các chi tiêu.
     * @return Tổng chi tiêu (dưới dạng double).
     */
    public double getTotalExpenses() {
        double total = 0.0;
        Cursor cursor = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            // Sử dụng hàm SUM() trong SQLite
            cursor = db.rawQuery("SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_EXPENSES, null);

            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return total;
    }

    /**
     * Lấy tổng chi tiêu cho mỗi danh mục (để vẽ biểu đồ/báo cáo).
     * @return Cursor chứa hai cột: Category và Total Amount (đã được đổi tên thành 'total_amount').
     */
    public Cursor getCategorySummary() {
        SQLiteDatabase db = this.getReadableDatabase();
        // GROUP BY để nhóm các chi tiêu cùng Category
        String query = "SELECT " + COL_CATEGORY + ", SUM(" + COL_AMOUNT + ") as total_amount " +
                "FROM " + TABLE_EXPENSES + " GROUP BY " + COL_CATEGORY +
                " ORDER BY total_amount DESC";
        return db.rawQuery(query, null);
    }

    /**
     * Lấy tất cả chi tiêu chi tiết theo một danh mục cụ thể.
     * @param category Tên danh mục cần lọc.
     * @return Cursor chứa các chi tiêu của danh mục đó (Description, Amount, Date, etc.).
     */
    public Cursor getExpensesByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Lấy tất cả các cột
        String query = "SELECT * FROM " + TABLE_EXPENSES +
                " WHERE " + COL_CATEGORY + " = ?" +
                " ORDER BY " + COL_DATE + " DESC";

        // Truyền tên danh mục vào mảng String[] để ngăn chặn SQL Injection
        return db.rawQuery(query, new String[]{category});
    }

    // ------------------- BUDGET FUNCTIONS ---------------------

    /**
     * Tính tổng chi tiêu cho tháng hiện tại.
     * Yêu cầu định dạng ngày tháng trong DB là YYYY-MM-DD.
     * @return Tổng chi tiêu (dưới dạng double).
     */
    public double calculateTotalSpentForMonth() {
        double totalSpent = 0.0;
        Cursor cursor = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            // 1. Lấy ngày đầu tiên và ngày cuối cùng của tháng hiện tại
            Calendar calendar = Calendar.getInstance();

            // Đặt về ngày đầu tiên của tháng (YYYY-MM-01)
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String startDate = dateFormat.format(calendar.getTime());

            // Đặt về ngày cuối cùng của tháng
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            String endDate = dateFormat.format(calendar.getTime());

            // 2. Viết truy vấn SQL
            // Sử dụng hàm SUM() và điều kiện WHERE để lọc theo ngày
            String query = "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_EXPENSES +
                    " WHERE " + COL_DATE + " BETWEEN ? AND ?";

            cursor = db.rawQuery(query, new String[]{startDate, endDate});

            if (cursor != null && cursor.moveToFirst()) {
                totalSpent = cursor.getDouble(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return totalSpent;
    }

    /**
     * Đóng database một cách an toàn.
     * Nên gọi phương thức này khi Activity/Fragment bị destroy để tránh memory leak.
     */
    public synchronized void closeDatabase() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}