package com.example.daililus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import org.jspecify.annotations.NonNull;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private AppCompatButton btnLogin;
    private TextView tvRegisterLink, tvForgotPassword;
    private LoginViewModel viewModel;
    private FirebaseAuth mAuth;

    private static final String KEY_EMAIL = "saved_email";
    private static final String KEY_PASSWORD = "saved_password";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        initViews();
        setupObserves();

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Введите данные для входа", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.loginUser(email, password);
            }
        });

        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void setupObserves(){
        viewModel.getLoginState().observe(this, state -> {
            if (state == null) return;

            if(state.isLoading()){
                btnLogin.setEnabled(false);
                btnLogin.setText("Входим...");
            }
            else if (state.isSuccess()){
                Toast.makeText(this, "Вход выполнен!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }
            else if (state.getError() != null){
                btnLogin.setEnabled(true);
                btnLogin.setText("Войти");
                Toast.makeText(this, "Ошибка: " + state.getError(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initViews(){
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(KEY_EMAIL, etEmail.getText().toString());
        outState.putString(KEY_PASSWORD, etPassword.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        String savedEmail = savedInstanceState.getString(KEY_EMAIL);
        String savedPassword = savedInstanceState.getString(KEY_PASSWORD);
        if (savedEmail != null) etEmail.setText(savedEmail);
        if (savedPassword != null) etPassword.setText(savedPassword);
    }
}