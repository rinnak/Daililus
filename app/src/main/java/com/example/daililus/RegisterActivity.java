package com.example.daililus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import org.jspecify.annotations.NonNull;

public class RegisterActivity extends AppCompatActivity {
    private DataBaseHelper dbHelper;
    private EditText etEmail, etPassword, etName;
    private AppCompatButton btnRegister;
    private TextView tvLoginLink;
    private RegisterViewModel viewModel;
    private static final String KEY_EMAIL = "saved_email";
    private static final String KEY_PASSWORD = "saved_password";
    private static final String KEY_NAME = "saved_name";

    @Override
    protected void onCreate(Bundle savesInstanceState){
        super.onCreate(savesInstanceState);
        setContentView(R.layout.activity_register);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        dbHelper = new DataBaseHelper(this);
        initViews();
        setupObservers();
        tvLoginLink.setOnClickListener(v -> {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            performRegistration();
        });
    }

    private void initViews(){
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
    }

    private void performRegistration(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 6){
            Toast.makeText(this, "Пароль должен быть от 6 символов", Toast.LENGTH_SHORT).show();
        }
        else{
            viewModel.registerUser(email, password);
        }
    }

    private void setupObservers(){
        viewModel.getAuthState().observe(this, state -> {
            if (state == null) return;
            if (state.isLoading()){
                btnRegister.setEnabled(false);
            }
            else if (state.isSuccess()){
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                new Thread(() -> {
                    dbHelper.saveUser(email , name);
                }).start();
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Аккаунт создан", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else if (state.getError() != null){
                btnRegister.setEnabled(true);
                Toast.makeText(this, "Ошибка "+ state.getError(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(KEY_NAME, etName.getText().toString());
        outState.putString(KEY_EMAIL, etEmail.getText().toString());
        outState.putString(KEY_PASSWORD, etPassword.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        String savedEmail = savedInstanceState.getString(KEY_EMAIL);
        String savedPassword = savedInstanceState.getString(KEY_PASSWORD);
        String savedName = savedInstanceState.getString(KEY_NAME);
        if (savedName != null) etName.setText(savedName);
        if (savedEmail != null) etEmail.setText(savedEmail);
        if (savedPassword != null) etPassword.setText(savedPassword);
    }


}
