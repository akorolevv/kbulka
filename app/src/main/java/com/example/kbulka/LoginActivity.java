package com.example.kbulka;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumberEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity); // Предполагается, что у вас есть файл activity_login.xml

        phoneNumberEditText = findViewById(R.id.loginPhoneNumberEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.loginProgressBar);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bulka-3f4dd-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String phoneNumber = phoneNumberEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Введите номер телефона и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        usersRef.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null && user.getPassword().equals(password)) {
                            // Успешная авторизация
                            Toast.makeText(LoginActivity.this, "Авторизация прошла успешно", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, BodyActivity.class));
                            finish(); // Опционально: закрыть LoginActivity
                            return;
                        }
                    }
                    // Неверный пароль
                    Toast.makeText(LoginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                } else {
                    // Аккаунт не найден
                    Toast.makeText(LoginActivity.this, "Аккаунт с таким номером не существует", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Ошибка при авторизации", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

