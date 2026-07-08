package com.courtsync.app.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class User {
    @SerializedName("id")
    private long id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("city")
    private String city;

    @SerializedName("profileImageUrl")
    private String profileImageUrl;

    @SerializedName("role")
    private String role;

    @SerializedName("credits")
    private BigDecimal credits;

    @SerializedName("totalBookings")
    private int totalBookings;

    @SerializedName("totalHours")
    private double totalHours;

    public long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getRole() { return role; }
    public BigDecimal getCredits() { return credits; }
    public int getTotalBookings() { return totalBookings; }
    public double getTotalHours() { return totalHours; }
    public void setFullName(String n) { fullName = n; }
    public void setPhone(String p) { phone = p; }
    public void setCity(String c) { city = c; }
    public void setProfileImageUrl(String u) { profileImageUrl = u; }
}
