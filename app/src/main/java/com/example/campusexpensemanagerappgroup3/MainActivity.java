package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    CardView cardAddExpense, cardViewExpenses, cardBudget, cardReports;
    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        cardAddExpense = findViewById(R.id.cardAddExpense);
        cardViewExpenses = findViewById(R.id.cardViewExpenses);
        cardBudget = findViewById(R.id.cardBudget);
        cardReports = findViewById(R.id.cardReports);
        welcomeText = findViewById(R.id.welcomeText);

        // GET USER EMAIL FROM SESSION
        String email = getSharedPreferences("session", MODE_PRIVATE)
                .getString("email", "User");

        welcomeText.setText("Welcome, " + email + "!");

        // --- OPEN ADD EXPENSE ---
        cardAddExpense.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddExpenseActivity.class)));

        // --- OPEN VIEW EXPENSES ---
        cardViewExpenses.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ViewExpensesActivity.class)));

//        // --- OPEN BUDGET ACTIVITY ---
//        cardBudget.setOnClickListener(v ->
//                startActivity(new Intent(MainActivity.this, BudgetActivity.class)));
//
//        // --- OPEN REPORTS ACTIVITY ---
//        cardReports.setOnClickListener(v ->
//                startActivity(new Intent(MainActivity.this, ReportActivity.class)));
    }
}
