package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent; // Cần import Intent
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

    DatabaseHelper db;

    // Khai báo biến trạng thái Edit Mode và ID
    private boolean isEditMode = false;
    private int expenseId = -1;

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

        db = new DatabaseHelper(this);

        // --------------------- CATEGORY SPINNER -------------------
        String[] categories = {"Food", "Transport", "Rent", "Shopping", "Bills", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // --------------------- DATE PICKER LOGIC -------------------------
        edtDate.setOnClickListener(v -> showDatePicker());

        // =================================================================
        // LOGIC XỬ LÝ CHẾ ĐỘ CHỈNH SỬA (EDIT MODE)
        // =================================================================
        Intent intent = getIntent();
        if (intent.getBooleanExtra("IS_EDIT_MODE", false)) {
            isEditMode = true;
            expenseId = intent.getIntExtra("EXPENSE_ID", -1);

            // 1. Điền dữ liệu cũ vào các trường
            edtDescription.setText(intent.getStringExtra("DESCRIPTION"));
            edtAmount.setText(String.format(Locale.US, "%.2f", intent.getDoubleExtra("AMOUNT", 0.0)));
            edtDate.setText(intent.getStringExtra("DATE"));

            // 2. Cập nhật Tiêu đề và Nút Save
            setTitle("Edit Expense");
            btnSave.setText("Update Expense");

            // 3. Đặt Spinner đến Category hiện tại
            String category = intent.getStringExtra("CATEGORY");
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(category)) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        } else {
            setTitle("Add New Expense");
            btnSave.setText("Save Expense");
        }

        // --------------------- SAVE/UPDATE BUTTON -------------------------
        btnSave.setOnClickListener(v -> handleSaveExpense());
    }

    // Phương thức xử lý sự kiện lưu (SAVE) hoặc cập nhật (UPDATE)
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

        // 3. XỬ LÝ LƯU HOẶC CẬP NHẬT DỰA TRÊN isEditMode
        if (isEditMode) {
            // CHẾ ĐỘ CHỈNH SỬA (UPDATE)
            int updatedRows = db.updateExpense(expenseId, description, amount, category, date);
            if (updatedRows > 0) {
                Toast.makeText(this, "Expense Updated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update expense.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // CHẾ ĐỘ THÊM MỚI (INSERT)
            long result = db.insertExpense(description, amount, category, date);

            if (result > 0) {
                Toast.makeText(this, "Expense Added!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add expense. Check database.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Phương thức hiển thị Date Picker (Không đổi)
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