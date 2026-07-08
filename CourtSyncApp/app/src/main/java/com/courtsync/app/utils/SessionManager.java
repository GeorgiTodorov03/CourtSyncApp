package com.courtsync.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.courtsync.app.models.User;

public class SessionManager {
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, User user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_TOKEN, token);
        editor.putLong(Constants.KEY_USER_ID, user.getId());
        editor.putString(Constants.KEY_USER_NAME, user.getFullName());
        editor.putString(Constants.KEY_USER_EMAIL, user.getEmail());
        editor.putString(Constants.KEY_USER_AVATAR, user.getProfileImageUrl());
        if (user.getCredits() != null) {
            editor.putString(Constants.KEY_CREDITS, user.getCredits().toString());
        }
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(Constants.KEY_TOKEN, null);
    }

    public String getUserName() {
        return prefs.getString(Constants.KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return prefs.getString(Constants.KEY_USER_EMAIL, "");
    }

    public String getUserAvatar() {
        return prefs.getString(Constants.KEY_USER_AVATAR, null);
    }

    public long getUserId() {
        return prefs.getLong(Constants.KEY_USER_ID, -1);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public void updateUserName(String name) {
        prefs.edit().putString(Constants.KEY_USER_NAME, name).apply();
    }
}
