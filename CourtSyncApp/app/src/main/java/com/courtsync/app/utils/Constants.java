package com.courtsync.app.utils;

public class Constants {
    // Change this to your machine's IP when testing on a physical device
    // For emulator, use 10.0.2.2 (maps to host machine localhost)
    public static final String BASE_URL = "http://10.0.2.2:8080/";

    public static final String PREF_NAME = "CourtsyncPrefs";
    public static final String KEY_TOKEN = "jwt_token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_AVATAR = "user_avatar";
    public static final String KEY_CREDITS = "user_credits";

    public static final int PAGE_SIZE = 20;
}
