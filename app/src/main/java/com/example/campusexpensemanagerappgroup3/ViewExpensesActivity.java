package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button; // Import Button
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class ViewExpensesActivity extends AppCompatActivity {

    private RecyclerView recyclerExpenses;
    private TextView textViewNoExpenses;
    private Button btnBack; // Khai báo nút BACK
    private ExpenseAdapter adapter;
    private List<DatabaseHelper.ExpenseModel> expenseList;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);

        // --- CÀI ĐẶT THANH TIÊU ĐỀ & NÚT BACK (Trên cùng) ---
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Expenses");
        }

        // --- ÁNH XẠ VIEW ---
        recyclerExpenses = findViewById(R.id.recyclerExpenses);
        textViewNoExpenses = findViewById(R.id.textViewNoExpenses);
        btnBack = findViewById(R.id.btnBack); // Ánh xạ nút BACK

        // --- CẤU HÌNH RECYCLERVIEW & DB ---
        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseHelper(this);

        // --- XỬ LÝ SỰ KIỆN NÚT BACK DƯỚI CÙNG ---
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadExpenses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses(); // Tải lại dữ liệu khi Activity quay lại foreground
    }

    private void loadExpenses() {
        expenseList = db.getAllExpenses();

        // LOGIC HIỂN THỊ TRẠNG THÁI TRỐNG
        if (expenseList.isEmpty()) {
            recyclerExpenses.setVisibility(View.GONE);
            textViewNoExpenses.setVisibility(View.VISIBLE); // Hiện thông báo trống
        } else {
            recyclerExpenses.setVisibility(View.VISIBLE);
            textViewNoExpenses.setVisibility(View.GONE);    // Ẩn thông báo trống

            // Cài đặt Adapter (cần Listener để xử lý Edit/Delete)
            adapter = new ExpenseAdapter(this, expenseList, new ExpenseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DatabaseHelper.ExpenseModel expense) {
                    // Mở màn hình Sửa
                    startEditActivity(expense);
                }

                @Override
                public void onItemLongClick(DatabaseHelper.ExpenseModel expense) {
                    // Hiện hộp thoại Xóa
                    showDeleteDialog(expense);
                }
            });
            recyclerExpenses.setAdapter(adapter);
        }
    }

    private void startEditActivity(DatabaseHelper.ExpenseModel expense) {
        Intent intent = new Intent(this, AddExpenseActivity.class);
        intent.putExtra("IS_EDIT_MODE", true);
        intent.putExtra("EXPENSE_ID", expense.getId());
        intent.putExtra("DESCRIPTION", expense.getDescription());
        intent.putExtra("AMOUNT", expense.getAmount());
        intent.putExtra("CATEGORY", expense.getCategory());
        intent.putExtra("DATE", expense.getDate());
        startActivity(intent);
    }

    private void showDeleteDialog(DatabaseHelper.ExpenseModel expense) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete: " + expense.getDescription() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = db.deleteExpense(expense.getId());
                    if (result > 0) {
                        Toast.makeText(this, "Expense deleted successfully!", Toast.LENGTH_SHORT).show();
                        loadExpenses(); // Tải lại danh sách sau khi xóa
                    } else {
                        Toast.makeText(this, "Deletion failed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // XỬ LÝ SỰ KIỆN NÚT BACK TRÊN THANH TIÊU ĐỀ
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity, quay về màn hình chính
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}