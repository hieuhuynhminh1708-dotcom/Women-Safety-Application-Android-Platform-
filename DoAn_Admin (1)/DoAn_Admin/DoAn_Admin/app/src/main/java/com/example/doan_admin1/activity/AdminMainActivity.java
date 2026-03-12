package com.example.doan_admin1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_admin1.R;
import com.example.doan_admin1.adapter.SosAlertAdapter;
import com.example.doan_admin1.model.SOSAlert;
import com.example.doan_admin1.model.User;
import com.example.doan_admin1.adapter.UserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity implements SosAlertAdapter.OnAlertActionListener {

    private TextView txtAlertCount, txtUserCount, txtEmpty;
    private RecyclerView recyclerAlerts;
    private CardView cardUser;
    private LinearLayout btnHome, btnSOS, btnUsers, btnSettings;

    private DatabaseReference usersRef, historyRef;
    private ValueEventListener usersListener, historyListener;

    private final List<User> userList = new ArrayList<>();
    private final List<SOSAlert> sosList = new ArrayList<>();

    private UserAdapter userAdapter;
    private SosAlertAdapter sosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ View
        txtAlertCount = findViewById(R.id.txtAlertCount);
        txtUserCount = findViewById(R.id.txtUserCount);
        txtEmpty = findViewById(R.id.txtEmpty);
        recyclerAlerts = findViewById(R.id.recyclerAlerts);
        cardUser = findViewById(R.id.cardUser);

        // Footer
        LinearLayout footer = findViewById(R.id.footerMenu);
        btnHome = footer.findViewById(R.id.btnHome);
        btnSOS = footer.findViewById(R.id.btnSOS);
        btnUsers = footer.findViewById(R.id.btnUsers);
        btnSettings = footer.findViewById(R.id.btnSettings);

        // RecyclerView setup
        recyclerAlerts.setLayoutManager(new LinearLayoutManager(this));
        sosAdapter = new SosAlertAdapter(sosList, this);
        recyclerAlerts.setAdapter(sosAdapter);

        // Firebase đúng region
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://sos-alert-cc754-default-rtdb.asia-southeast1.firebasedatabase.app/"
        );
        usersRef = database.getReference("Users");
        historyRef = database.getReference("history");

        attachUsersListener();
        attachHistoryListener();

        // Card mở danh sách người dùng
        cardUser.setOnClickListener(v ->
                startActivity(new Intent(AdminMainActivity.this, AdminUserActivity.class)));

        setupFooterClick();
    }

    /** ----------------- 🔹 Người dùng ----------------- */
    private void attachUsersListener() {
        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                int userCount = 0;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    if (user == null) continue;

                    Boolean isAdminFlag = snap.child("isAdmin").getValue(Boolean.class);
                    boolean isAdmin = (isAdminFlag != null && isAdminFlag);

                    if (!isAdmin) {
                        userList.add(user);
                        userCount++;
                    }
                }

                txtUserCount.setText(String.valueOf(userCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminMainActivity.this,
                        "Firebase lỗi Users: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        };
        usersRef.addValueEventListener(usersListener);
    }

    /** ----------------- 🔹 Lịch sử SOS ----------------- */
    private void attachHistoryListener() {
        historyListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sosList.clear();

                if (!snapshot.exists()) {
                    txtEmpty.setVisibility(View.VISIBLE);
                    txtAlertCount.setText("0");
                    sosAdapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot child : snapshot.getChildren()) {
                    SOSAlert alert = child.getValue(SOSAlert.class);
                    if (alert == null) continue;

                    if (alert.getId() == null || alert.getId().trim().isEmpty()) {
                        alert.setId(child.getKey());
                    }

                    sosList.add(alert);
                }

                // Sắp xếp mới nhất lên đầu
                Collections.sort(sosList, new Comparator<SOSAlert>() {
                    @Override
                    public int compare(SOSAlert a, SOSAlert b) {
                        String da = a.getDate() == null ? "" : a.getDate();
                        String db = b.getDate() == null ? "" : b.getDate();
                        return db.compareTo(da);
                    }
                });

                txtAlertCount.setText(String.valueOf(sosList.size()));
                sosAdapter.notifyDataSetChanged();

                txtEmpty.setVisibility(sosList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminMainActivity.this,
                        "Firebase lỗi SOS: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        };
        historyRef.addValueEventListener(historyListener);
    }

    /** ----------------- 🔹 Footer ----------------- */
    private void setupFooterClick() {
        btnHome.setOnClickListener(v ->
                Toast.makeText(this, "Bạn đang ở trang chính", Toast.LENGTH_SHORT).show());

        btnSOS.setOnClickListener(v ->
                startActivity(new Intent(this, AdminSosActivity.class)));

        btnUsers.setOnClickListener(v ->
                startActivity(new Intent(this, AdminUserActivity.class)));

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(this, AdminSettingActivity.class)));
    }

    /** ----------------- 🔹 Callback Adapter ----------------- */
    @Override
    public void onToggleSms(SOSAlert alert, boolean checked) {
        if (alert.getId() == null) return;
        historyRef.child(alert.getId()).child("smsStatus").setValue(checked)
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi cập nhật SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onToggleCall(SOSAlert alert, boolean checked) {
        if (alert.getId() == null) return;
        historyRef.child(alert.getId()).child("callStatus").setValue(checked)
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi cập nhật gọi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDeleteAlert(SOSAlert alert) {
        if (alert.getId() == null) return;
        historyRef.child(alert.getId()).removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(this, "Đã xoá cảnh báo", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /** ----------------- 🔹 Dọn listener ----------------- */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (usersListener != null) usersRef.removeEventListener(usersListener);
        if (historyListener != null) historyRef.removeEventListener(historyListener);
    }
}
