package com.courtsync.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.courtsync.app.R;
import com.courtsync.app.viewmodels.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {
    private AuthViewModel viewModel;
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private TextView tvError;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);
        ImageView ivHeaderBg = findViewById(R.id.ivHeaderBg);

        Glide.with(this)
                .load("https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?w=800")
                .centerCrop()
                .into(ivHeaderBg);

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();
            viewModel.register(fullName, email, password, confirm);
        });

        findViewById(R.id.tvLoginLink).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        viewModel.getAuthResult().observe(this, result -> {
            if (result != null) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(this, loading -> {
            progressBar.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE);
        });
    }
}
