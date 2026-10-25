package com.mobile2app.eventtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private TextInputEditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);

        findViewById(R.id.btn_login).setOnClickListener(v -> login());
        findViewById(R.id.btn_create_account).setOnClickListener(v -> register());

        // Observe authentication results from the ViewModel
        viewModel.getAuthResult().observe(this, result -> {
            if (result == null) return;

            switch (result.type) {
                case LOGIN_SUCCESS: {
                    SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    prefs.edit().putInt("logged_in_user_id", result.user.getId()).apply();

                    startActivity(new Intent(this, EventGridActivity.class));
                    finish();
                    break;
                }
                case LOGIN_FAILURE:
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    break;
                case REGISTER_SUCCESS:
                    Toast.makeText(this, "Account created! Please log in.", Toast.LENGTH_SHORT).show();
                    break;
                case REGISTER_FAILURE:
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.login(username, password);
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.register(username, password, "");
    }
}