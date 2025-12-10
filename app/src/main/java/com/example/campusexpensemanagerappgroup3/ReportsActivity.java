package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem; // Thêm import MenuItem cho nút Back
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    private Spinner spinnerCategoryFilter;
    private TextView tvReportTitle;
    private ListView lvDetailedExpenses;
    private DatabaseHelper dbHelper;

    // --- Các phần code khác bị lược bỏ để tập trung vào lỗi Locale ---
    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Kích hoạt nút Back trên ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Báo cáo Chi tiêu");
        }

        // 1. Ánh xạ Views
        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);
        tvReportTitle = findViewById(R.id.tvReportTitle);
        lvDetailedExpenses = findViewById(R.id.lvDetailedExpenses);

        dbHelper = new DatabaseHelper(this);

        // 2. Tải danh sách các danh mục duy nhất cho Spinner
        loadCategorySpinner();

        // 3. Thiết lập Listener cho Spinner
        setupSpinnerListener();
    }

    // Xử lý nút Back trên ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCategorySpinner() {
        Cursor summaryCursor = null;
        List<String> categories = new ArrayList<>();
        categories.add("— Tất cả Danh mục (Báo cáo Tổng quan) —");

        try {
            summaryCursor = dbHelper.getCategorySummary();
            if (summaryCursor != null && summaryCursor.moveToFirst()) {
                do {
                    String category = summaryCursor.getString(0);
                    categories.add(category);
                } while (summaryCursor.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tải danh mục: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (summaryCursor != null) {
                summaryCursor.close();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryFilter.setAdapter(adapter);
    }

    private void setupSpinnerListener() {
        spinnerCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = (String) parent.getItemAtPosition(position);

                if (position == 0) {
                    displayCategorySummary();
                } else {
                    displayDetailedReport(selectedCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });
    }

    private void displayCategorySummary() {
        double total = dbHelper.getTotalExpenses();
        // SỬA LỖI CÚ PHÁP: Dùng Locale chuẩn
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        tvReportTitle.setText(String.format("Báo cáo Tổng quan (Tổng: %s)", formatter.format(total)));

        Cursor cursor = null;
        ArrayList<String> summaryList = new ArrayList<>();

        try {
            cursor = dbHelper.getCategorySummary();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String category = cursor.getString(0);
                    double amount = cursor.getDouble(1);

                    String formattedAmount = formatter.format(amount);
                    double percentage = (total > 0 ? (amount / total) * 100 : 0.0);

                    String summaryItem = String.format("%s: %s (%.2f%%)",
                            category,
                            formattedAmount,
                            percentage);

                    summaryList.add(summaryItem);
                } while (cursor.moveToNext());
            } else if (total == 0) {
                summaryList.add("Chưa có chi tiêu nào được ghi nhận.");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tải tóm tắt chi tiêu.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_activated_1,
                summaryList
        );
        lvDetailedExpenses.setAdapter(adapter);
    }

    private void displayDetailedReport(String category) {
        Cursor cursor = null;
        ArrayList<String> detailedList = new ArrayList<>();
        // SỬA LỖI CÚ PHÁP: Dùng Locale chuẩn
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        double categoryTotal = 0.0;

        try {
            cursor = dbHelper.getExpensesByCategory(category);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String description = cursor.getString(1);
                    double amount = cursor.getDouble(2);
                    String date = cursor.getString(4);

                    categoryTotal += amount;

                    String detailItem = String.format("%s (%s) - %s",
                            description,
                            formatter.format(amount),
                            date);

                    detailedList.add(detailItem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tải chi tiết danh mục.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        tvReportTitle.setText(String.format("Chi tiết: %s (Tổng: %s)", category, formatter.format(categoryTotal)));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_activated_1,
                detailedList
        );
        lvDetailedExpenses.setAdapter(adapter);

        if (detailedList.isEmpty()) {
            Toast.makeText(this, "Không có chi tiêu nào cho danh mục này.", Toast.LENGTH_SHORT).show();
        }
    }
}