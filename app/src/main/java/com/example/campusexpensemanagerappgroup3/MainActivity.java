package com.example.campusexpensemanagerappgroup3;





import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView; // QUAN TRỌNG: Phải import CardView
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Khai báo View: CardView thay vì Button
    private TextView totalSpentTextView;
    private TextView remainingTextView;
    private ProgressBar budgetProgressBar; // Thêm ProgressBar

    private CardView cardAddExpense;
    private CardView cardViewExpenses;
    private CardView cardBudget;
    private CardView cardReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Phải là dòng đầu tiên

        // 1. Ánh xạ View (SỬA LỖI KHÔNG KHỚP ID)
        totalSpentTextView = findViewById(R.id.totalSpentText);
        remainingTextView = findViewById(R.id.budgetRemainingText);
        budgetProgressBar = findViewById(R.id.budgetProgressBar); // Ánh xạ ProgressBar

        cardAddExpense = findViewById(R.id.cardAddExpense);
        cardViewExpenses = findViewById(R.id.cardViewExpenses);
        cardBudget = findViewById(R.id.cardBudget);
        cardReports = findViewById(R.id.cardReports);

        // 2. Thiết lập Listener cho các CardView
        setupCardListeners();

        // 3. Tải và hiển thị dữ liệu chi tiêu
        loadExpenseData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenseData();
    }

    // --- Các hàm xử lý sự kiện và logic ---

    private void setupCardListeners() {
        // Xử lý nút "Add Expense"
        cardAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý nút "View Expenses"
        cardViewExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewExpensesActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý nút "Manage Budget"
        cardBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageBudgetActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý nút "Reports"
        cardReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReportsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadExpenseData() {
        // *** LOGIC TẢI DỮ LIỆU TỪ DB SẼ Ở ĐÂY ***

        // Dữ liệu giả lập
        double totalSpent = 1500.50;
        double totalBudget = 2000.00; // Cần có tổng ngân sách để tính Remaining và Progress
        double remainingBudget = totalBudget - totalSpent;

        // Tính Progress Bar (theo %)
        int progress = (int) ((totalSpent / totalBudget) * 100);

        // Cập nhật giao diện
        totalSpentTextView.setText(String.format(Locale.US, "$%.2f", totalSpent));
        remainingTextView.setText(String.format(Locale.US, "$%.2f", remainingBudget));
        budgetProgressBar.setProgress(progress);

        // Tùy chỉnh màu sắc (Đảm bảo R.color.red và R.color.green đã được định nghĩa)
        if (remainingBudget < 200.00) {
            remainingTextView.setTextColor(ContextCompat.getColor(this, R.color.red));
            // Đổi màu progress bar nếu gần hết ngân sách (tùy chọn)
            budgetProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.red));
        } else {
            remainingTextView.setTextColor(ContextCompat.getColor(this, R.color.green));
            budgetProgressBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.green));
        }
    }
}