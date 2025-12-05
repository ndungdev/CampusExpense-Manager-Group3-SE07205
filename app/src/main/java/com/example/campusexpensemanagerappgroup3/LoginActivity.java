package com.example.campusexpensemanagerappgroup3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences; // Thêm import này
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.campusexpensemanagerappgroup3.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.loginEmail.getText().toString();
                String password = binding.loginPassword.getText().toString();

                if(email.equals("")||password.equals(""))
                    Toast.makeText(LoginActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                else{
                    // Sửa tên phương thức cho khớp với DatabaseHelper của bạn
                    Boolean checkCredentials = databaseHelper.checkEmailPassword(email, password);

                    if(checkCredentials == true){
                        Toast.makeText(LoginActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();

                        // --- BẮT ĐẦU THAY ĐỔI ---

                        // 1. Lưu lại phiên làm việc (session)
                        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
                        SharedPreferences.Editor editor = session.edit();
                        editor.putString("email", email); // Lưu email của người dùng
                        editor.apply(); // Áp dụng thay đổi

                        // 2. Chuyển hướng đến MainActivity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        // Cờ này sẽ xóa các activity trước đó, ngăn người dùng quay lại LoginActivity bằng nút back
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // Đóng LoginActivity

                        // --- KẾT THÚC THAY ĐỔI ---

                    }else{
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
