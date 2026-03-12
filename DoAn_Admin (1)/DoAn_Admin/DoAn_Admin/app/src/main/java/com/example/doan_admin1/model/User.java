package com.example.doan_admin1.model;

public class User {
    private String uid;        // UID Firebase Authentication
    private String name;       // Họ và tên người dùng
    private String email;      // Địa chỉ email
    private String phone;      // Số điện thoại
    private boolean isAdmin;   // true nếu là quản trị viên, false nếu là user thường===
    public User() {
    }

    public User(String uid, String name, String email, String phone, boolean isAdmin) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.isAdmin = isAdmin;
    }

    // ====== 🔧 GETTER & SETTER ======
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    // ====== 🧠 HÀM HỖ TRỢ (tuỳ chọn) ======
    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}