package com.example.campusexpensemanagerappgroup3;



import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText editTextAmount;
    private EditText editTextDescription;
    private Spinner spinnerCategory;
    private TextView textViewDate;
    private Button buttonSaveExpense;

    private Calendar selectedDate; // Biến để lưu trữ ngày được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // 1. Ánh xạ View
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        textViewDate = findViewById(R.id.textViewDate);
        buttonSaveExpense = findViewById(R.id.buttonSaveExpense);

        // Khởi tạo ngày mặc định là hôm nay
        selectedDate = Calendar.getInstance();
        updateDateDisplay();

        // 2. Thiết lập Spinner (Danh mục)
        setupCategorySpinner();

        // 3. Thiết lập Listener cho chọn ngày
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // 4. Thiết lập Listener cho nút Lưu
        buttonSaveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });
    }

    // --- Hàm xử lý DatePicker ---

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, monthOfYear);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateDisplay();
            }
        };

        new DatePickerDialog(this,
                dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void updateDateDisplay() {
        // Định dạng ngày hiển thị (ví dụ: dd/MM/yyyy)
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        textViewDate.setText("Ngày: " + sdf.format(selectedDate.getTime()));
    }

    // --- Hàm xử lý Spinner ---

    private void setupCategorySpinner() {
        // Đây là danh sách các danh mục mẫu.
        // Trong dự án thực tế, bạn nên lấy danh sách này từ database.
        String[] categories = {"Ăn uống", "Đi lại", "Hóa đơn", "Giải trí", "Khác"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories);

        spinnerCategory.setAdapter(adapter);
    }

    // --- Hàm xử lý Lưu Chi Tiêu ---

    private void saveExpense() {
        String amountStr = editTextAmount.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        Date date = selectedDate.getTime();

        // 1. Kiểm tra dữ liệu đầu vào
        if (amountStr.isEmpty()) {
            editTextAmount.setError("Vui lòng nhập số tiền!");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            editTextAmount.setError("Số tiền không hợp lệ!");
            return;
        }

        // 2. Thu thập dữ liệu chi tiêu (để lưu vào database)
        // Hiện tại chỉ log và hiển thị Toast

        // In ra console để kiểm tra
        System.out.println("Expense Data:");
        System.out.println("Amount: " + amount);
        System.out.println("Category: " + category);
        System.out.println("Description: " + description);
        System.out.println("Date: " + date.toString());

        // 3. **Thực hiện lưu vào Database tại đây** // (Sử dụng Room hoặc SQLite Helper của bạn)

        // 4. Thông báo và kết thúc Activity
        Toast.makeText(this, "Đã lưu chi tiêu: " + category + " - " + amountStr + " VND", Toast.LENGTH_LONG).show();

        // Sau khi lưu thành công, bạn có thể đóng Activity này và quay về MainActivity
        // Cần thêm setResult(RESULT_OK) nếu muốn gửi thông báo cập nhật cho MainActivity
        finish();
    }
}