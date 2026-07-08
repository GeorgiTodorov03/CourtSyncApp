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

public class LoginActivity extends AppCompatActivity {
    private AuthViewModel viewModel;
    private EditText etEmail, etPassword;
    private TextView tvError;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);
        ImageView ivHeaderBg = findViewById(R.id.ivHeaderBg);

        // Load header background image
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1546519638405-a9f95a3b74c6?w=800")
                .centerCrop()
                .into(ivHeaderBg);

        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            viewModel.login(email, password);
        });

        findViewById(R.id.tvRegisterLink).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

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
            tvError.setVisibility(loading != null && loading ? View.GONE : tvError.getVisibility());
        });
    }
}
