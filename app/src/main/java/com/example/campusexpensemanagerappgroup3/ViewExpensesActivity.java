package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewExpensesActivity extends AppCompatActivity {

    private RecyclerView recyclerExpenses;
    private TextView textViewNoExpenses;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList;

    // KHAI BÁO DatabaseHelper
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);

        // Ánh xạ View
        recyclerExpenses = findViewById(R.id.recyclerExpenses);
        textViewNoExpenses = findViewById(R.id.textViewNoExpenses);

        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));

        // KHỞI TẠO DatabaseHelper THẬT
        db = new DatabaseHelper(this);

        // KHÔNG TẢI DỮ LIỆU Ở ĐÂY. loadExpenses() được gọi trong onResume()
    }

    /**
     * PHƯƠNG PHÁP KHẮC PHỤC LỖI: Luôn tải lại dữ liệu khi quay lại màn hình
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }


    // Tải dữ liệu THẬT từ Database
    private void loadExpenses() {

        // 1. Tải dữ liệu từ DB (SỬ DỤNG PHƯƠNG THỨC getAllExpenses() từ DatabaseHelper)
        expenseList = db.getAllExpenses();

        // ------------------ (Đã loại bỏ DỮ LIỆU GIẢ LẬP) --------------------

        if (expenseList.isEmpty()) {
            recyclerExpenses.setVisibility(View.GONE);
            textViewNoExpenses.setVisibility(View.VISIBLE);
        } else {
            recyclerExpenses.setVisibility(View.VISIBLE);
            textViewNoExpenses.setVisibility(View.GONE);

            // 2. Tái tạo/Cập nhật Adapter
            adapter = new ExpenseAdapter(expenseList);
            recyclerExpenses.setAdapter(adapter);
        }
    }

    // =========================================================================
    // LỚP MÔ HÌNH DỮ LIỆU (Expense Model) - KHÔNG CẦN CHỈNH SỬA
    // =========================================================================
    public static class Expense {
        private int id;
        private String description;
        private double amount;
        private String category;
        private String date;

        public Expense(int id, String description, double amount, String category, String date) {
            this.id = id;
            this.description = description;
            this.amount = amount;
            this.category = category;
            this.date = date;
        }

        public String getDescription() { return description; }
        public double getAmount() { return amount; }
        public String getCategory() { return category; }
        public String getDate() { return date; }
    }

    // =========================================================================
    // LỚP BỘ ĐIỀU HỢP (ExpenseAdapter) - KHÔNG CẦN CHỈNH SỬA
    // =========================================================================
    public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

        private List<Expense> localExpenseList;

        public ExpenseAdapter(List<Expense> expenseList) {
            this.localExpenseList = expenseList;
        }

        @NonNull
        @Override
        public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Dùng layout có sẵn của Android (simple_list_item_2)
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ExpenseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
            Expense expense = localExpenseList.get(position);

            TextView text1 = holder.itemView.findViewById(android.R.id.text1);
            TextView text2 = holder.itemView.findViewById(android.R.id.text2);

            // Dòng 1: Mô tả và Số tiền
            text1.setText(String.format(Locale.US, "%s ($%.2f)", expense.getDescription(), expense.getAmount()));

            // Dòng 2: Category và Ngày
            text2.setText(String.format("Category: %s | Date: %s", expense.getCategory(), expense.getDate()));
            text2.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        @Override
        public int getItemCount() {
            return localExpenseList.size();
        }

        // ViewHolder Class
        public class ExpenseViewHolder extends RecyclerView.ViewHolder {
            public ExpenseViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}