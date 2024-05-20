package com.example.kbulka;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
public class RegistrationActivity extends AppCompatActivity {

    private EditText phoneNumberEditText, passwordEditText, verificationCodeEditText;
    private Button sendVerificationCodeButton, registerButton, verifyButton;
    private TextView phoneNumberExistsTextView;
    private ProgressBar progressBar;
    private String verificationId;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean isPhoneNumberRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity); // Предполагается, что у вас есть файл activity_registration.xml

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        verificationCodeEditText = findViewById(R.id.verificationCodeEditText);
        sendVerificationCodeButton = findViewById(R.id.sendVerificationCodeButton);
        verifyButton = findViewById(R.id.verifyButton);
        progressBar = findViewById(R.id.progressBar);
        phoneNumberExistsTextView = findViewById(R.id.phoneNumberExistsTextView);

        mAuth = FirebaseAuth.getInstance();

        // Инициализация ссылки на базу данных с указанным URL-адресом
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bulka-3f4dd-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");

        phoneNumberEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                String phoneNumber = s.toString();
                checkPhoneNumberExists(phoneNumber);
            }
        });


        sendVerificationCodeButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (isPhoneNumberRegistered) {
                Toast.makeText(RegistrationActivity.this, "Пользователь с таким номером телефона уже зарегистрирован", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegistrationActivity.this, "Введите номер телефона и пароль", Toast.LENGTH_LONG).show();
            } else {
                sendVerificationCodeButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                verificationCodeEditText.setVisibility(View.VISIBLE);
                verifyButton.setVisibility(View.VISIBLE);

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phoneNumber)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(RegistrationActivity.this)
                                .setCallbacks(mCallbacks)
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        verifyButton.setOnClickListener(v -> {
            String code = verificationCodeEditText.getText().toString();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(RegistrationActivity.this, "Введите код подтверждения", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyPhoneNumberWithCode(mVerificationId, code);
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBar.setVisibility(View.GONE); // Скрываем индикатор загрузки
                Toast.makeText(RegistrationActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                progressBar.setVisibility(View.GONE); // Скрываем индикатор загрузки
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(RegistrationActivity.this, "Код подтверждения отправлен", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        progressBar.setVisibility(View.VISIBLE); // Показываем индикатор загрузки
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void checkPhoneNumberExists(String phoneNumber) {
        usersRef.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isPhoneNumberRegistered = dataSnapshot.exists();
                if (isPhoneNumberRegistered)
                {
                    phoneNumberExistsTextView.setText("Пользователь с таким номером телефона уже зарегистрирован");
                    phoneNumberExistsTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    phoneNumberExistsTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при запросе к базе данных
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Получаем UID пользователя после успешной аутентификации
                        FirebaseUser user = mAuth.getCurrentUser();
                        String phoneNumber = phoneNumberEditText.getText().toString();
                        String password = passwordEditText.getText().toString();

                        // Сохранение пользователя в базе данных
                        usersRef.child(user.getUid()).setValue(new User(phoneNumber, password))
                                .addOnSuccessListener(aVoid -> {
                                    // Запись данных прошла успешно
                                    Toast.makeText(RegistrationActivity.this, "Регистрация успешна, данные сохранены в БД", Toast.LENGTH_SHORT).show();
                                    // Переход к MainActivity // ...
                                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); // Опционально: закрыть RegistrationActivity, чтобы пользователь не мог вернуться к ней кнопкой "Назад"
                                })
                                .addOnFailureListener(e -> {
                                    // Произошла ошибка при записи данных
                                    Toast.makeText(RegistrationActivity.this, "Ошибка записи в БД: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("RegistrationActivity", "Ошибка записи в БД", e);
                                });
                    } else {

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(RegistrationActivity.this, "Неверный код подтверждения", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
