package com.example.doan_admin1.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doan_admin1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.doan_admin1.R.layout.activity_admin_login);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        txtEmail = findViewById(com.example.doan_admin1.R.id.txtAdminEmail);
        txtPassword = findViewById(com.example.doan_admin1.R.id.txtAdminPassword);
        btnLogin = findViewById(R.id.btnAdminLogin);

        btnLogin.setOnClickListener(v -> loginAdmin());
    }

    private void loginAdmin() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            Log.d("ADMIN", "UID Login = " + firebaseUser.getUid());
                            checkAdmin(firebaseUser.getUid());
                        }
                    } else {
                        Toast.makeText(this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAdmin(String userId) {
        databaseReference.child(userId).child("isAdmin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean isAdmin = snapshot.getValue(Boolean.class);

                        if (Boolean.TRUE.equals(isAdmin)) {
                            startActivity(new Intent(
                                    AdminLoginActivity.this,
                                    AdminMainActivity.class
                            ));
                            finish();
                        } else {
                            firebaseAuth.signOut();
                            Toast.makeText(AdminLoginActivity.this,
                                    "Tài khoản không có quyền Admin!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminLoginActivity.this,
                                "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
