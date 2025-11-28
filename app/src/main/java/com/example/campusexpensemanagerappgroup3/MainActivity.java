package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CardView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    CardView cardAddExpense, cardViewExpenses, cardBudget, cardReports;
    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardAddExpense = findViewById(R.id.cardAddExpense);
        cardViewExpenses = findViewById(R.id.cardViewExpenses);
        cardBudget = findViewById(R.id.cardBudget);
        cardReports = findViewById(R.id.cardReports);
        welcomeText = findViewById(R.id.welcomeText);

        // Get logged email from session
        String email = getSharedPreferences("session", MODE_PRIVATE)
                .getString("email", "User");

        welcomeText.setText("Welcome, " + email + "!");

        // OPEN ADD EXPENSE
        cardAddExpense.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddExpenseActivity.class)));

        // OPEN VIEW EXPENSE
        cardViewExpenses.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ViewExpensesActivity.class)));

        // OPEN BUDGET
        cardBudget.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, BudgetActivity.class)));

        // OPEN REPORTS
        cardReports.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ReportActivity.class)));
    }
}
