package com.courtsync.app.repositories;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.User;
import com.courtsync.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final RetrofitClient retrofitClient;

    public UserRepository(Context context) {
        retrofitClient = RetrofitClient.getInstance(context);
    }

    public void getProfile(MutableLiveData<User> result, MutableLiveData<String> error) {
        retrofitClient.getApiService().getMyProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Failed to load profile");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void updateProfile(String fullName, String phone, String city,
                              MutableLiveData<User> result,
                              MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        if (fullName != null) body.put("fullName", fullName);
        if (phone != null) body.put("phone", phone);
        if (city != null) body.put("city", city);

        retrofitClient.getApiService().updateProfile(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Update failed");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }
}
