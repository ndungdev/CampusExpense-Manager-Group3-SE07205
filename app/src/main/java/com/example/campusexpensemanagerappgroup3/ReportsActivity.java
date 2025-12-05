package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // 1. Ánh xạ Views (Không có PieChart)
        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);
        tvReportTitle = findViewById(R.id.tvReportTitle);
        lvDetailedExpenses = findViewById(R.id.lvDetailedExpenses);

        dbHelper = new DatabaseHelper(this);

        // 2. Tải danh sách các danh mục duy nhất cho Spinner
        loadCategorySpinner();

        // 3. Thiết lập Listener cho Spinner
        setupSpinnerListener();

        // Mặc định hiển thị Báo cáo tổng quan (Tóm tắt theo danh mục)
        displayCategorySummary();
    }

    private void loadCategorySpinner() {
        Cursor summaryCursor = dbHelper.getCategorySummary();
        List<String> categories = new ArrayList<>();

        categories.add("— Tất cả Danh mục (Báo cáo Tổng quan) —");

        if (summaryCursor.moveToFirst()) {
            do {
                String category = summaryCursor.getString(0);
                categories.add(category);
            } while (summaryCursor.moveToNext());
        }
        summaryCursor.close();

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
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        tvReportTitle.setText(String.format("Báo cáo Tổng quan (Tổng: %s)", formatter.format(total)));

        Cursor cursor = dbHelper.getCategorySummary();
        ArrayList<String> summaryList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                double amount = cursor.getDouble(1);

                String formattedAmount = formatter.format(amount);
                double percentage = (total > 0 ? (amount / total) * 100 : 0.0);

                // Chuỗi hiển thị tỷ lệ
                String summaryItem = String.format("%s: %s (%.2f%%)",
                        category,
                        formattedAmount,
                        percentage);

                summaryList.add(summaryItem);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Cập nhật ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_activated_1,
                summaryList
        );
        lvDetailedExpenses.setAdapter(adapter);
    }

    private void displayDetailedReport(String category) {
        Cursor cursor = dbHelper.getExpensesByCategory(category);
        ArrayList<String> detailedList = new ArrayList<>();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        double categoryTotal = 0.0;

        if (cursor.moveToFirst()) {
            do {
                String description = cursor.getString(1); // DESC
                double amount = cursor.getDouble(2);     // AMOUNT
                String date = cursor.getString(4);         // DATE

                categoryTotal += amount;

                String detailItem = String.format("%s (%s) - %s",
                        description,
                        formatter.format(amount),
                        date);

                detailedList.add(detailItem);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Cập nhật tiêu đề và ListView
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