package com.example.doan_admin1.model;

public class SOSAlert {
    private String id;
    private String name;
    private String phone;
    private String message;
    private String date;
    private boolean smsStatus;   // true = đã gửi, false = chưa gửi
    private boolean callStatus;  // true = đã gọi, false = chưa gọi
    private String userId;       // phân biệt user

    public SOSAlert() {
        // Firebase yêu cầu constructor rỗng
    }

    public SOSAlert(String id, String name, String phone, String message,
                   String date, boolean smsStatus, boolean callStatus) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.message = message;
        this.date = date;
        this.smsStatus = smsStatus;
        this.callStatus = callStatus;
    }

    // ✅ Getter và Setter cho userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getMessage() { return message; }
    public String getDate() { return date; }

    public boolean isSmsStatus() { return smsStatus; }
    public boolean isCallStatus() { return callStatus; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setMessage(String message) { this.message = message; }
    public void setDate(String date) { this.date = date; }
    public void setSmsStatus(boolean smsStatus) { this.smsStatus = smsStatus; }
    public void setCallStatus(boolean callStatus) { this.callStatus = callStatus; }
}
