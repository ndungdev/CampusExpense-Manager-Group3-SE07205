package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.Locale;

public class BudgetActivity extends AppCompatActivity {

    // KHAI BÁO NGÂN SÁCH CỐ ĐỊNH (PHẢI KHỚP VỚI YÊU CẦU CỦA BẠN)
    private static final double MONTHLY_BUDGET = 5000000.0; // Ví dụ: 5,000,000 VND

    private TextView tvTotalSpent;
    private TextView tvRemainingBudget;
    private TextView tvFixedBudget; // Để hiển thị ngân sách cố định
    private ProgressBar progressBarBudget;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // 1. Ánh xạ View
        tvFixedBudget = findViewById(R.id.tvFixedBudget);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget);
        progressBarBudget = findViewById(R.id.progressBarBudget);

        // 2. Khởi tạo Database
        db = new DatabaseHelper(this);

        // 3. Hiển thị ngân sách cố định ngay lập tức
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvFixedBudget.setText(String.format("Budget: %s", formatter.format(MONTHLY_BUDGET)));

        // 4. Tải và cập nhật tóm tắt ngân sách
        updateBudgetSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại summary khi người dùng quay lại màn hình
        updateBudgetSummary();
    }

    /**
     * Lấy dữ liệu chi tiêu, tính toán ngân sách và cập nhật UI.
     */
    private void updateBudgetSummary() {
        // 1. Lấy Total Spent từ Database cho tháng hiện tại
        double totalSpent = db.calculateTotalSpentForMonth();

        // 2. Tính toán Remaining Budget
        double remainingBudget = MONTHLY_BUDGET - totalSpent;

        // 3. Tính tỷ lệ Progress
        double spendingRatio = totalSpent / MONTHLY_BUDGET;
        int progressPercent = (int) (spendingRatio * 100);

        // Đảm bảo progress không vượt quá 100%
        if (progressPercent > 100) {
            progressPercent = 100;
        }

        // 4. Định dạng tiền tệ
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // 5. Cập nhật UI

        // Cập nhật Total Spent
        tvTotalSpent.setText(formatter.format(totalSpent));

        // Cập nhật Remaining Budget
        tvRemainingBudget.setText(formatter.format(remainingBudget));

        // Tùy chọn: Thay đổi màu Remaining Budget nếu chi tiêu vượt quá ngân sách
        if (remainingBudget < 0) {
            tvRemainingBudget.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            tvRemainingBudget.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        }

        // Cập nhật Progress Bar
        progressBarBudget.setProgress(progressPercent);
    }
}