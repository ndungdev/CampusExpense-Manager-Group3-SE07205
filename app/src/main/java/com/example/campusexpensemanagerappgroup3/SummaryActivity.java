package com.example.campusexpensemanagerappgroup3;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SummaryActivity extends AppCompatActivity {

    private TextView tvTotalExpense;
    private ListView lvCategorySummary;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đảm bảo bạn đã có file layout activity_summary.xml
        setContentView(R.layout.activity_summary);

        // Khởi tạo các thành phần UI
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        lvCategorySummary = findViewById(R.id.lvCategorySummary);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Tải và hiển thị dữ liệu tóm tắt
        loadSummaryData();
    }

    /**
     * Tải tổng chi tiêu và chi tiết theo danh mục từ cơ sở dữ liệu.
     */
    private void loadSummaryData() {

        // 1. Tính và Hiển thị Tổng Chi tiêu
        double total = dbHelper.getTotalExpenses();

        // Định dạng tiền tệ theo Locale mặc định (Ví dụ: 1.000.000,00 VND)
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedTotal = formatter.format(total);

        tvTotalExpense.setText(String.format("Tổng chi tiêu: %s", formattedTotal));

        // 2. Lấy và Xử lý Chi tiết theo Danh mục
        Cursor cursor = dbHelper.getCategorySummary();
        ArrayList<String> summaryList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                // Lấy dữ liệu từ Cursor
                // Cột 0: Category (TEXT)
                // Cột 1: total_amount (REAL, đã được alias trong hàm getCategorySummary)
                String category = cursor.getString(0);
                double amount = cursor.getDouble(1);

                String formattedAmount = formatter.format(amount);

                // Chuẩn bị chuỗi để hiển thị
                String summaryItem = String.format("%s: %s (%.2f%%)",
                        category,
                        formattedAmount,
                        (total > 0 ? (amount / total) * 100 : 0.0));

                summaryList.add(summaryItem);
            } while (cursor.moveToNext());
        }

        // Đóng Cursor sau khi sử dụng
        cursor.close();

        // 3. Hiển thị danh sách tóm tắt trên ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1, // Layout mặc định cho List Item
                summaryList
        );
        lvCategorySummary.setAdapter(adapter);
    }
}