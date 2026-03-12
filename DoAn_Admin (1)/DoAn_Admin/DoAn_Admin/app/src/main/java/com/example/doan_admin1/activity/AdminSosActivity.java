package com.example.doan_admin1.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_admin1.R;
import com.example.doan_admin1.adapter.SosAlertAdapter;
import com.example.doan_admin1.model.SOSAlert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminSosActivity extends AppCompatActivity implements SosAlertAdapter.OnAlertActionListener {

    private TextView txtTotalHistory, txtSmsSent, txtCallDone;
    private EditText edtSearch;
    private Spinner spinnerFilter;
    private RecyclerView recyclerHistory;
    private SosAlertAdapter adapter;
    private final List<SOSAlert> allHistory = new ArrayList<>();
    private final List<SOSAlert> displayHistory = new ArrayList<>();

    private LinearLayout btnHome, btnSOS, btnUsers, btnSettings;
    private DatabaseReference historyRef;
    private ValueEventListener historyListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_admin); // dùng lại layout SOS Admin

        txtTotalHistory = findViewById(R.id.txtTotalAlerts);
        txtSmsSent = findViewById(R.id.txtSmsSent);
        txtCallDone = findViewById(R.id.txtCallDone);
        edtSearch = findViewById(R.id.edtSearch);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        recyclerHistory = findViewById(R.id.recyclerAlerts);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SosAlertAdapter(displayHistory, this);
        recyclerHistory.setAdapter(adapter);

        View footer = findViewById(R.id.footerMenu);
        btnHome = footer.findViewById(R.id.btnHome);
        btnSOS = footer.findViewById(R.id.btnSOS);
        btnUsers = footer.findViewById(R.id.btnUsers);
        btnSettings = footer.findViewById(R.id.btnSettings);
        setupFooter();

        // 🔹 Kết nối Firebase đúng project
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://sos-alert-cc754-default-rtdb.asia-southeast1.firebasedatabase.app/"
        );
        historyRef = database.getReference("history");

        attachHistoryListener();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s == null ? "" : s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void attachHistoryListener() {
        historyListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allHistory.clear();
                displayHistory.clear();

                int smsCount = 0, callCount = 0;

                if (!snapshot.exists()) {
                    Toast.makeText(AdminSosActivity.this, "⚠️ Chưa có dữ liệu lịch sử nào", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot child : snapshot.getChildren()) {
                    SOSAlert alert = child.getValue(SOSAlert.class);
                    if (alert == null) continue;

                    // Gán ID từ key nếu thiếu
                    if (alert.getId() == null || alert.getId().trim().isEmpty()) {
                        alert.setId(child.getKey());
                    }

                    allHistory.add(alert);
                    if (alert.isSmsStatus()) smsCount++;
                    if (alert.isCallStatus()) callCount++;
                }

                // Sắp xếp mới nhất lên đầu
                Collections.sort(allHistory, new Comparator<SOSAlert>() {
                    @Override
                    public int compare(SOSAlert a, SOSAlert b) {
                        String da = a.getDate() == null ? "" : a.getDate();
                        String db = b.getDate() == null ? "" : b.getDate();
                        return db.compareTo(da);
                    }
                });

                displayHistory.addAll(allHistory);

                txtTotalHistory.setText(String.valueOf(allHistory.size()));
                txtSmsSent.setText(String.valueOf(smsCount));
                txtCallDone.setText(String.valueOf(callCount));

                adapter.notifyDataSetChanged();

                String q = edtSearch.getText() == null ? "" : edtSearch.getText().toString();
                if (!q.trim().isEmpty()) filterList(q);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminSosActivity.this,
                        "❌ Lỗi tải dữ liệu lịch sử: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        };

        historyRef.addValueEventListener(historyListener);
    }

    private void filterList(String query) {
        String q = query.trim().toLowerCase();
        displayHistory.clear();

        if (q.isEmpty()) {
            displayHistory.addAll(allHistory);
        } else {
            for (SOSAlert a : allHistory) {
                String name = safe(a.getName()).toLowerCase();
                String phone = safe(a.getPhone()).toLowerCase();
                String msg = safe(a.getMessage()).toLowerCase();
                if (name.contains(q) || phone.contains(q) || msg.contains(q)) {
                    displayHistory.add(a);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupFooter() {
        btnHome.setOnClickListener(v -> startActivity(new Intent(this, AdminMainActivity.class)));
        btnSOS.setOnClickListener(v -> startActivity(new Intent(this, AdminSosActivity.class)));
        btnUsers.setOnClickListener(v -> startActivity(new Intent(this, AdminUserActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, AdminSettingActivity.class)));
    }

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
        new AlertDialog.Builder(this)
                .setTitle("Xoá lịch sử")
                .setMessage("Bạn muốn xoá lịch sử của \"" + safe(alert.getName()) + "\"?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    historyRef.child(alert.getId()).removeValue()
                            .addOnSuccessListener(unused -> Toast.makeText(AdminSosActivity.this, "Đã xoá", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(AdminSosActivity.this, "Lỗi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private String safe(String s) { return s == null ? "" : s; }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (historyListener != null) historyRef.removeEventListener(historyListener);
    }
}
