package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> register());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void register() {
        String name = getText(etName);
        String email = getText(etEmail);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);

        if (TextUtils.isEmpty(name)) { etName.setError("Vui lòng nhập họ tên"); return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Vui lòng nhập email"); return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Vui lòng nhập mật khẩu"); return; }
        if (password.length() < 6) { etPassword.setError("Mật khẩu tối thiểu 6 ký tự"); return; }
        if (!password.equals(confirmPassword)) { etConfirmPassword.setError("Mật khẩu không khớp"); return; }

        setLoading(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("createdAt", System.currentTimeMillis());

                        db.collection("users").document(uid).set(user)
                                .addOnCompleteListener(t -> {
                                    setLoading(false);
                                    if (t.isSuccessful()) {
                                        Toast.makeText(this, "Đăng ký thành công! Chào mừng " + name, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(this, "Lỗi lưu thông tin người dùng", Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        setLoading(false);
                        String msg = task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại";
                        Toast.makeText(this, "Lỗi: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
    }
}
