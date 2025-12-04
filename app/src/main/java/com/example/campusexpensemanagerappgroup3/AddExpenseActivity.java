package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    EditText edtDescription, edtAmount, edtDate;
    Spinner spinnerCategory;
    Button btnSave;

    // 1. KHAI BÁO DatabaseHelper
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Ánh xạ View
        edtDescription = findViewById(R.id.edtDescription);
        edtAmount = findViewById(R.id.edtAmount);
        edtDate = findViewById(R.id.edtDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSaveExpense);

        // 2. KHỞI TẠO DatabaseHelper THẬT
        db = new DatabaseHelper(this);

        // --------------------- CATEGORY SPINNER (Fixed Color) -------------------
        String[] categories = {"Food", "Transport", "Rent", "Shopping", "Bills", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // --------------------- DATE PICKER LOGIC -------------------------
        edtDate.setOnClickListener(v -> showDatePicker());

        // --------------------- SAVE BUTTON -------------------------
        btnSave.setOnClickListener(v -> handleSaveExpense());
    }

    // Phương thức xử lý sự kiện lưu
    private void handleSaveExpense() {
        String description = edtDescription.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        double amount;

        // 1. Kiểm tra trường bắt buộc
        if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Xử lý lỗi chuyển đổi Amount
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Thực hiện chèn vào Database THẬT
        long result = db.insertExpense(description, amount, category, date);

        // Đã XOÁ dòng giả lập: long result = 1;

        if (result > 0) { // Nếu chèn thành công (ID > 0)
            Toast.makeText(this, "Expense Added!", Toast.LENGTH_SHORT).show();
            finish(); // Quan trọng: Đóng Activity này để gọi onResume() của ViewExpensesActivity
        } else {
            // Bao gồm trường hợp result == -1 (thường là lỗi chèn)
            Toast.makeText(this, "Failed to add expense. Check database.", Toast.LENGTH_SHORT).show();
        }
    }


    // Phương thức hiển thị Date Picker
    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Định dạng ngày: YYYY-MM-DD
                    String dateString = String.format(Locale.US, "%d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    edtDate.setText(dateString);
                },
                year, month, day);

        datePickerDialog.show();
    }
}