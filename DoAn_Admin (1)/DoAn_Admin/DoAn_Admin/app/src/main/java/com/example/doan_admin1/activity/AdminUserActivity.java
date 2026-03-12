package com.example.doan_admin1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_admin1.R;
import com.example.doan_admin1.adapter.UserAdapter;
import com.example.doan_admin1.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminUserActivity extends AppCompatActivity {

    private TextView txtTotalUsers, txtAdminCount;
    private EditText edtSearch;
    private Spinner spinnerFilter;
    private RecyclerView recyclerUsers;
    private LinearLayout btnHome, btnSOS, btnUsers, btnSettings;

    private DatabaseReference usersRef;
    private ValueEventListener usersListener;

    private final List<User> allUsers = new ArrayList<>();
    private final List<User> displayUsers = new ArrayList<>();
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        LinearLayout layoutStats = findViewById(R.id.layoutStats);
        txtTotalUsers = layoutStats.findViewById(R.id.txtTotalUsers);
        txtAdminCount = layoutStats.findViewById(R.id.txtAdminCount);

        edtSearch = findViewById(R.id.edtSearch);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        recyclerUsers = findViewById(R.id.recyclerUsers);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));

        View footer = findViewById(R.id.footerMenu);
        btnHome = footer.findViewById(R.id.btnHome);
        btnSOS = footer.findViewById(R.id.btnSOS);
        btnUsers = footer.findViewById(R.id.btnUsers);
        btnSettings = footer.findViewById(R.id.btnSettings);

        // Adapter
        userAdapter = new UserAdapter(displayUsers, new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                Toast.makeText(AdminUserActivity.this,
                        "Chọn: " + safe(user.getName()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserLongClick(User user) {
                Toast.makeText(AdminUserActivity.this,
                        "Giữ lâu: " + safe(user.getName()), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerUsers.setAdapter(userAdapter);

        // Firebase URL chính xác
        usersRef = FirebaseDatabase.getInstance("https://sos-alert-cc754-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");
        attachUsersListener();

        // Tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s == null ? "" : s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        setupFooterClick();
    }

    private void attachUsersListener() {
        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUsers.clear();
                displayUsers.clear();

                int adminCount = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if (user == null) continue;

                    Boolean flag = data.child("isAdmin").getValue(Boolean.class);
                    boolean isAdmin = (flag != null && flag);

                    allUsers.add(user);
                    if (isAdmin) adminCount++;
                    else displayUsers.add(user);
                }

                txtTotalUsers.setText(String.valueOf(allUsers.size()));
                txtAdminCount.setText(String.valueOf(adminCount));

                userAdapter.notifyDataSetChanged();

                String q = edtSearch.getText() == null ? "" : edtSearch.getText().toString();
                if (!q.trim().isEmpty()) filterUsers(q);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUserActivity.this,
                        "🔥 Firebase lỗi " + error.getCode() + ": " + error.getMessage(),
                        Toast.LENGTH_LONG).show();

                Log.e("FIREBASE_ERROR", "Code: " + error.getCode() + " - " + error.getMessage());
            }
        };

        usersRef.addValueEventListener(usersListener);
    }

    private void filterUsers(String query) {
        String q = query.trim().toLowerCase();
        if (q.isEmpty()) {
            userAdapter.updateData(new ArrayList<>(displayUsers));
            return;
        }

        List<User> filtered = new ArrayList<>();
        for (User u : displayUsers) {
            String name = safe(u.getName()).toLowerCase();
            String email = safe(u.getEmail()).toLowerCase();
            String phone = safe(u.getPhone()).toLowerCase();
            if (name.contains(q) || email.contains(q) || phone.contains(q)) {
                filtered.add(u);
            }
        }
        userAdapter.updateData(filtered);
    }

    private void setupFooterClick() {
        btnHome.setOnClickListener(v -> startActivity(new Intent(this, AdminMainActivity.class)));
        btnSOS.setOnClickListener(v -> startActivity(new Intent(this, AdminSosActivity.class)));
        btnUsers.setOnClickListener(v -> Toast.makeText(this, "Bạn đang ở trang Người dùng", Toast.LENGTH_SHORT).show());
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, AdminSettingActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (usersListener != null) usersRef.removeEventListener(usersListener);
    }

    private String safe(String s) { return s == null ? "" : s; }
}
