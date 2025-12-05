package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ProgressBar;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Khai báo Ngân sách Cố định (Giống BudgetActivity)
    private static final double MONTHLY_BUDGET = 5000000.0; // 5,000,000 VND

    // Menu Cards
    CardView cardAddExpense, cardViewExpenses, cardBudget, cardReports;
    TextView welcomeText;

    // Elements cho Stats Card (Từ activity_main.xml đã cập nhật)
    private TextView totalSpentText;        // ID: totalSpentText
    private TextView budgetRemainingText;   // ID: budgetRemainingText
    private ProgressBar budgetProgressBar;  // ID: budgetProgressBar

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- 1. Ánh xạ Menu Cards ---
        cardAddExpense = findViewById(R.id.cardAddExpense);
        cardViewExpenses = findViewById(R.id.cardViewExpenses);
        cardBudget = findViewById(R.id.cardBudget);
        cardReports = findViewById(R.id.cardReports);
        welcomeText = findViewById(R.id.welcomeText);

        // --- 2. Ánh xạ Stats Card (Dữ liệu mới) ---
        totalSpentText = findViewById(R.id.totalSpentText);
        budgetRemainingText = findViewById(R.id.budgetRemainingText);
        budgetProgressBar = findViewById(R.id.budgetProgressBar);

        // --- 3. Khởi tạo Database ---
        db = new DatabaseHelper(this);

        // Lấy Email và hiển thị lời chào
        String email = getSharedPreferences("session", MODE_PRIVATE)
                .getString("email", "User");
        welcomeText.setText("Welcome, " + email + "!");

        // --- 4. Thiết lập Listeners ---
        // OPEN ADD EXPENSE
        cardAddExpense.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddExpenseActivity.class)));

        // OPEN VIEW EXPENSES
        cardViewExpenses.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ViewExpensesActivity.class)));

        // OPEN BUDGET ACTIVITY
        cardBudget.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, BudgetActivity.class)));

        cardReports.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ReportsActivity.class))); // Sửa ReportActivity -> SummaryActivity
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật tóm tắt ngân sách mỗi khi Activity trở lại foreground
        updateBudgetSummary();
    }

    /**
     * Lấy dữ liệu chi tiêu trong tháng, tính toán ngân sách và cập nhật UI.
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
        totalSpentText.setText(formatter.format(totalSpent));

        // Cập nhật Remaining Budget
        budgetRemainingText.setText(formatter.format(remainingBudget));

        // Thay đổi màu Remaining Budget nếu chi tiêu vượt quá
        if (remainingBudget < 0) {
            budgetRemainingText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            budgetProgressBar.setProgressTintList(getResources().getColorStateList(android.R.color.holo_red_light)); // Đổi màu thanh progress thành đỏ
        } else {
            budgetRemainingText.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            budgetProgressBar.setProgressTintList(getResources().getColorStateList(android.R.color.holo_green_light)); // Đổi màu thanh progress thành xanh
        }

        // Cập nhật Progress Bar
        budgetProgressBar.setProgress(progressPercent);
    }
}