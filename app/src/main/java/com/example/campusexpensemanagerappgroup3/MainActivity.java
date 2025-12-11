package com.example.campusexpensemanagerappgroup3;

import androidx.annotation.NonNull; // Thêm import này
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;         // Thêm import này
import android.view.MenuItem;   // Thêm import này
import android.widget.Button;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;       // Thêm import này
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // (Các biến của bạn vẫn giữ nguyên)
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
    private Button logoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- KIỂM TRA PHIÊN ĐĂNG NHẬP ---
        if (!isUserLoggedIn()) {
            // Nếu chưa đăng nhập, chuyển đến LoginActivity và kết thúc MainActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Ngăn người dùng quay lại MainActivity bằng nút back
            return; // Dừng việc thực thi thêm mã trong onCreate
        }

        setContentView(R.layout.activity_main);

        // (Ánh xạ và các listener của bạn vẫn giữ nguyên)
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
        logoutButton = findViewById(R.id.logoutButton);
        // --- 3. Khởi tạo Database ---
        db = new DatabaseHelper(this);

        // Lấy Email và hiển thị lời chào
        String email = getSharedPreferences("session", MODE_PRIVATE)
                .getString("email", "User");
        welcomeText.setText("Welcome, " + email + "!");

        // --- 4. Thiết lập Listeners ---
        cardAddExpense.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddExpenseActivity.class)));
        cardViewExpenses.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ViewExpensesActivity.class)));
        cardBudget.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, BudgetActivity.class)));
        cardReports.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ReportsActivity.class)));
        logoutButton.setOnClickListener(v -> {
            logoutUser(); // Gọi hàm đăng xuất đã có sẵn
        });
    }

    /**
     * Kiểm tra xem người dùng đã đăng nhập hay chưa bằng cách kiểm tra SharedPreferences.
     * @return true nếu đã đăng nhập, false nếu chưa.
     */
    private boolean isUserLoggedIn() {
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        // Kiểm tra xem có email nào được lưu không. Nếu có giá trị khác null thì coi như đã đăng nhập.
        return session.getString("email", null) != null;
    }


    /**
     * Xóa phiên đăng nhập và chuyển hướng người dùng về trang Login.
     */
    private void logoutUser() {
        // 1. Xóa dữ liệu phiên làm việc
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        editor.clear(); // Xóa tất cả dữ liệu trong "session"
        editor.apply();

        // 2. Chuyển hướng về LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        finish(); // Đóng MainActivity
    }

    // --- KẾT THÚC THAY ĐỔI ---


    @Override
    protected void onResume() {
        super.onResume();
        // Chỉ cập nhật tóm tắt ngân sách nếu người dùng đã đăng nhập (tránh lỗi khi đang chuyển hướng)
        if (isUserLoggedIn()) {
            updateBudgetSummary();
        }
    }

    /**
     * Lấy dữ liệu chi tiêu trong tháng, tính toán ngân sách và cập nhật UI.
     * Chạy database operation trên background thread để tránh blocking UI.
     */
    private void updateBudgetSummary() {
        // Chạy database operation trên background thread
        new Thread(() -> {
            try {
                // 1. Lấy Total Spent từ Database cho tháng hiện tại
                final double totalSpent = db.calculateTotalSpentForMonth();
                final double remainingBudget = MONTHLY_BUDGET - totalSpent;
                final double spendingRatio = totalSpent / MONTHLY_BUDGET;
                int progressPercent = (int) (spendingRatio * 100);
                if (progressPercent > 100) {
                    progressPercent = 100;
                }

                final int finalProgressPercent = progressPercent;
                final NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

                // Cập nhật UI trên main thread
                runOnUiThread(() -> {
                    totalSpentText.setText(formatter.format(totalSpent));
                    budgetRemainingText.setText(formatter.format(remainingBudget));

                    if (remainingBudget < 0) {
                        budgetRemainingText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        budgetProgressBar.setProgressTintList(getResources().getColorStateList(android.R.color.holo_red_light));
                    } else {
                        budgetRemainingText.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                        budgetProgressBar.setProgressTintList(getResources().getColorStateList(android.R.color.holo_green_light));
                    }
                    budgetProgressBar.setProgress(finalProgressPercent);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                    Toast.makeText(MainActivity.this, "Error loading budget summary", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng database khi activity bị destroy để tránh memory leak
        if (db != null) {
            db.closeDatabase();
        }
    }
}
