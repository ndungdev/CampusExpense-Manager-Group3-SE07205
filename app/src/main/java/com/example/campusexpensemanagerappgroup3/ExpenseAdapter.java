package com.example.campusexpensemanagerappgroup3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    // Sử dụng đúng model từ DatabaseHelper
    private Context context;
    private List<DatabaseHelper.ExpenseModel> expenseList;
    private OnItemClickListener listener;

    // Interface để Activity xử lý sự kiện
    public interface OnItemClickListener {
        void onItemClick(DatabaseHelper.ExpenseModel expense);
        void onItemLongClick(DatabaseHelper.ExpenseModel expense);
    }

    // Constructor: Yêu cầu 3 tham số
    public ExpenseAdapter(Context context, List<DatabaseHelper.ExpenseModel> expenseList, OnItemClickListener listener) {
        this.context = context;
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đảm bảo bạn đã có file item_expense.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        DatabaseHelper.ExpenseModel expense = expenseList.get(position);

        holder.tvDescription.setText(expense.getDescription());

        // Gộp danh mục và ngày
        String subInfo = expense.getCategory() + " • " + expense.getDate();
        holder.tvSubInfo.setText(subInfo);

        // Icon chữ cái đầu
        if (expense.getCategory() != null && !expense.getCategory().isEmpty()) {
            holder.tvIcon.setText(String.valueOf(expense.getCategory().charAt(0)).toUpperCase());
        }

        // Định dạng tiền tệ
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvAmount.setText(formatter.format(expense.getAmount()));

        // Sự kiện Click
        holder.itemView.setOnClickListener(v -> listener.onItemClick(expense));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(expense);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvAmount, tvSubInfo, tvIcon;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            // Đảm bảo ID trong item_expense.xml khớp với các dòng này
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
            tvAmount = itemView.findViewById(R.id.tvItemAmount);
            tvSubInfo = itemView.findViewById(R.id.tvItemSubInfo);
            tvIcon = itemView.findViewById(R.id.tvIconCategory);
        }
    }
}