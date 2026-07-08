package com.courtsync.app.repositories;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.AuthResponse;
import com.courtsync.app.network.RetrofitClient;
import com.courtsync.app.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private final RetrofitClient retrofitClient;
    private final SessionManager sessionManager;

    public AuthRepository(Context context) {
        retrofitClient = RetrofitClient.getInstance(context);
        sessionManager = new SessionManager(context);
    }

    public void login(String email, String password,
                      MutableLiveData<AuthResponse> result,
                      MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        retrofitClient.getApiService().login(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionManager.saveSession(response.body().getToken(), response.body().getUser());
                    result.postValue(response.body());
                } else {
                    error.postValue("Invalid email or password");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void register(String fullName, String email, String password,
                         MutableLiveData<AuthResponse> result,
                         MutableLiveData<String> error) {
        Map<String, String> body = new HashMap<>();
        body.put("fullName", fullName);
        body.put("email", email);
        body.put("password", password);

        retrofitClient.getApiService().register(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionManager.saveSession(response.body().getToken(), response.body().getUser());
                    result.postValue(response.body());
                } else {
                    error.postValue("Registration failed. Email may already be in use.");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }
}
