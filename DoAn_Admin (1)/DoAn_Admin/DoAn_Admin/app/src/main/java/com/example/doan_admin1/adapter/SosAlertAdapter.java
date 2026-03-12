package com.example.doan_admin1.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_admin1.R;
import com.example.doan_admin1.model.SOSAlert;

import java.util.ArrayList;
import java.util.List;

public class SosAlertAdapter extends RecyclerView.Adapter<SosAlertAdapter.AlertViewHolder> {

    // ✅ Danh sách dữ liệu
    private final List<SOSAlert> alertList;
    private final OnAlertActionListener listener;

    // Giao diện callback (callback lên AdminSosActivity)
    public interface OnAlertActionListener {
        void onToggleSms(SOSAlert alert, boolean checked);
        void onToggleCall(SOSAlert alert, boolean checked);
        void onDeleteAlert(SOSAlert alert);
    }

    // ✅ Constructor
    public SosAlertAdapter(List<SOSAlert> alertList, OnAlertActionListener listener) {
        // Nếu danh sách null, tạo list trống để tránh NullPointerException
        this.alertList = (alertList != null) ? alertList : new ArrayList<>();
        this.listener = listener;
    }

    // ✅ Hàm cập nhật dữ liệu mới từ Activity
    public void updateData(List<SOSAlert> newList) {
        alertList.clear();
        if (newList != null) {
            alertList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sos_alert, parent, false);
        return new AlertViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        SOSAlert alert = alertList.get(position);

        holder.txtName.setText(alert.getName());
        holder.txtPhone.setText(alert.getPhone());
        holder.txtMessage.setText(alert.getMessage());
        holder.txtDate.setText(alert.getDate());

        // Nếu có link vị trí trong message
        if (alert.getMessage() != null && alert.getMessage().contains("http")) {
            holder.txtLink.setVisibility(View.VISIBLE);
            holder.txtLink.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(alert.getMessage()));
                v.getContext().startActivity(i);
            });
        } else {
            holder.txtLink.setVisibility(View.GONE);
        }

        // Gán trạng thái checkbox
        holder.chkSms.setOnCheckedChangeListener(null);
        holder.chkCall.setOnCheckedChangeListener(null);

        holder.chkSms.setChecked(alert.isSmsStatus());
        holder.chkCall.setChecked(alert.isCallStatus());

        holder.chkSms.setOnCheckedChangeListener((buttonView, isChecked) ->
                listener.onToggleSms(alert, isChecked));

        holder.chkCall.setOnCheckedChangeListener((buttonView, isChecked) ->
                listener.onToggleCall(alert, isChecked));

        holder.btnDelete.setOnClickListener(v ->
                listener.onDeleteAlert(alert));
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    // ✅ ViewHolder chứa các view trong item_sos_alert.xml
    public static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPhone, txtMessage, txtDate, txtLink;
        CheckBox chkSms, chkCall;
        ImageButton btnDelete;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtLink = itemView.findViewById(R.id.txtLink);
            chkSms = itemView.findViewById(R.id.chkSms);
            chkCall = itemView.findViewById(R.id.chkCall);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
