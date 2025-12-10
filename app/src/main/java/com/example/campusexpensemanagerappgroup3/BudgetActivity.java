package com.example.campusexpensemanagerappgroup3;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.Locale;

public class BudgetActivity extends AppCompatActivity {

    // Khai báo các biến giao diện
    private EditText etBudgetAmount;
    private Button btnSaveBudget;
    private Button btnBack; // Nút Back ở dưới
    private TextView tvTotalSpent;
    private TextView tvBudgetStatus;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget); // Kết nối với XML

        // 1. Cài đặt nút Back trên thanh tiêu đề (Tùy chọn)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Quản lý Ngân sách");
        }

        // 2. Khởi tạo Database
        db = new DatabaseHelper(this);

        // 3. Ánh xạ View (Khớp với file activity_budget.xml)
        etBudgetAmount = findViewById(R.id.etBudgetAmount);
        btnSaveBudget = findViewById(R.id.btnSaveBudget);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus);
        btnBack = findViewById(R.id.btnBack); // Ánh xạ nút Back

        // 4. Tải dữ liệu ban đầu lên giao diện
        loadBudgetData();

        // 5. Xử lý sự kiện nút LƯU
        btnSaveBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBudget();
            }
        });

        // 6. Xử lý sự kiện nút BACK (Ở dưới cùng màn hình)
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng Activity, quay về trang chủ
            }
        });
    }

    // Hàm tải và hiển thị dữ liệu từ Database
    private void loadBudgetData() {
        // A. Lấy Ngân sách đã lưu từ DB
        double currentBudget = db.getMonthlyBudget();

        // Nếu đã có ngân sách, hiển thị lên ô nhập liệu
        if (currentBudget > 0) {
            etBudgetAmount.setText(String.format(Locale.US, "%.0f", currentBudget));
        }

        // B. Lấy Tổng tiền đã tiêu trong tháng này từ DB
        double totalSpent = db.calculateTotalSpentForMonth();

        // C. Hiển thị và tính toán
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalSpent.setText("Đã chi tiêu tháng này: " + formatter.format(totalSpent));

        if (currentBudget > 0) {
            double remaining = currentBudget - totalSpent;
            tvBudgetStatus.setText("Còn lại: " + formatter.format(remaining));

            // Đổi màu chữ: Đỏ nếu tiêu quá lố, Xanh nếu còn tiền
            if (remaining < 0) {
                tvBudgetStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvBudgetStatus.setText("Vượt ngân sách: " + formatter.format(Math.abs(remaining)));
            } else {
                tvBudgetStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        } else {
            tvBudgetStatus.setText("Chưa thiết lập ngân sách");
        }
    }

    // Hàm lưu ngân sách vào Database
    private void saveBudget() {
        String amountStr = etBudgetAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền ngân sách", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // Gọi hàm lưu trong DatabaseHelper
        boolean isSaved = db.setMonthlyBudget(amount);

        if (isSaved) {
            Toast.makeText(this, "Đã lưu ngân sách!", Toast.LENGTH_SHORT).show();
            loadBudgetData(); // Tải lại giao diện để cập nhật số dư mới
        } else {
            Toast.makeText(this, "Lỗi khi lưu ngân sách.", Toast.LENGTH_SHORT).show();
        }
    }

    // Xử lý sự kiện nút Back trên thanh tiêu đề (Mũi tên nhỏ)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}