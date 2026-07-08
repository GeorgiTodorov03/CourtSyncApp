package com.courtsync.app.models;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("tokenType")
    private String tokenType;

    @SerializedName("user")
    private User user;

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public User getUser() { return user; }
}
