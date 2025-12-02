package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddExpenseActivity extends AppCompatActivity {

    EditText edtDescription, edtAmount, edtDate;
    Spinner spinnerCategory;
    Button btnSave;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

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
                android.R.layout.simple_spinner_dropdown_item,
                categories);
        spinnerCategory.setAdapter(adapter);

        // --------------------- SAVE BUTTON -------------------------
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String description = edtDescription.getText().toString().trim();
                String amountStr = edtAmount.getText().toString().trim();
                String date = edtDate.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem().toString();

                if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
                    Toast.makeText(AddExpenseActivity.this,
                            "All fields are mandatory", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountStr);

                // Insert to database
                boolean inserted = db.insertExpense(description, amount, category, date);

                if (inserted) {
                    Toast.makeText(AddExpenseActivity.this,
                            "Expense Added!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to MainActivity
                } else {
                    Toast.makeText(AddExpenseActivity.this,
                            "Failed to add expense", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
