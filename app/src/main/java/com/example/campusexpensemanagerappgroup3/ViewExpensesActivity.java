package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Context;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton; // <<< CẦN THÊM IMPORT NÀY
import java.util.List;
import java.util.Locale;

public class ViewExpensesActivity extends AppCompatActivity {

    private RecyclerView recyclerExpenses;
    private TextView textViewNoExpenses;
    private FloatingActionButton fabShowSummary; // <<< KHAI BÁO BIẾN FAB
    private ExpenseAdapter adapter;
    private List<Expense> expenseList;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);

        recyclerExpenses = findViewById(R.id.recyclerExpenses);
        textViewNoExpenses = findViewById(R.id.textViewNoExpenses);
        fabShowSummary = findViewById(R.id.fabShowSummary); // <<< ÁNH XẠ FAB MỚI

        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseHelper(this);

        // =================================================================
        // XỬ LÝ SỰ KIỆN CLICK CHO FAB: MỞ SUMMARY ACTIVITY
        // =================================================================
        fabShowSummary.setOnClickListener(v -> {
            Intent intent = new Intent(ViewExpensesActivity.this, SummaryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }


    private void loadExpenses() {

        expenseList = db.getAllExpenses();

        if (expenseList.isEmpty()) {
            recyclerExpenses.setVisibility(View.GONE);
            textViewNoExpenses.setVisibility(View.VISIBLE);
        } else {
            recyclerExpenses.setVisibility(View.VISIBLE);
            textViewNoExpenses.setVisibility(View.GONE);

            adapter = new ExpenseAdapter(expenseList, ViewExpensesActivity.this);
            recyclerExpenses.setAdapter(adapter);
        }
    }

    // -------------------------------------------------------------------------
    // PHƯƠNG THỨC XỬ LÝ SỰ KIỆN XÓA (DELETE)
    // -------------------------------------------------------------------------
    public void deleteAndReload(int expenseId) {
        int result = db.deleteExpense(expenseId);

        if (result > 0) {
            loadExpenses();
            Toast.makeText(this, "Expense deleted successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete expense.", Toast.LENGTH_SHORT).show();
        }
    }

    // -------------------------------------------------------------------------
    // PHƯƠNG THỨC XỬ LÝ SỰ KIỆN CHỈNH SỬA (EDIT)
    // -------------------------------------------------------------------------
    public void startEditExpenseActivity(Expense expense) {
        Intent intent = new Intent(this, AddExpenseActivity.class);

        intent.putExtra("IS_EDIT_MODE", true);
        intent.putExtra("EXPENSE_ID", expense.getId());
        intent.putExtra("DESCRIPTION", expense.getDescription());
        intent.putExtra("AMOUNT", expense.getAmount());
        intent.putExtra("CATEGORY", expense.getCategory());
        intent.putExtra("DATE", expense.getDate());

        startActivity(intent);
    }

    // =========================================================================
    // LỚP MÔ HÌNH DỮ LIỆU (Expense Model)
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
        public int getId() { return id; }
    }

    // =========================================================================
    // LỚP BỘ ĐIỀU HỢP (ExpenseAdapter)
    // =========================================================================
    public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

        private List<Expense> localExpenseList;
        private Context context;

        public ExpenseAdapter(List<Expense> expenseList, Context context) {
            this.localExpenseList = expenseList;
            this.context = context;
        }

        @NonNull
        @Override
        public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ExpenseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
            Expense expense = localExpenseList.get(position);

            TextView text1 = holder.itemView.findViewById(android.R.id.text1);
            TextView text2 = holder.itemView.findViewById(android.R.id.text2);

            text1.setText(String.format(Locale.US, "%s ($%.2f)", expense.getDescription(), expense.getAmount()));
            text2.setText(String.format("Category: %s | Date: %s", expense.getCategory(), expense.getDate()));
            text2.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        @Override
        public int getItemCount() {
            return localExpenseList.size();
        }

        public class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            public ExpenseViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Expense expenseToEdit = localExpenseList.get(position);
                    if (context instanceof ViewExpensesActivity) {
                        ((ViewExpensesActivity) context).startEditExpenseActivity(expenseToEdit);
                    }
                }
            }

            @Override
            public boolean onLongClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    final int expenseIdToDelete = localExpenseList.get(position).getId();
                    showDeleteConfirmationDialog(expenseIdToDelete);
                    return true;
                }
                return false;
            }

            private void showDeleteConfirmationDialog(int id) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this expense?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (context instanceof ViewExpensesActivity) {
                                ((ViewExpensesActivity) context).deleteAndReload(id);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        }
    }
}