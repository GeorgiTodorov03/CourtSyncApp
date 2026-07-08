package com.courtsync.app.repositories;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.AIMessage;
import com.courtsync.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class AIRepository {
    private final RetrofitClient retrofitClient;

    public AIRepository(Context context) {
        retrofitClient = RetrofitClient.getInstance(context);
    }

    public void sendMessage(String message, Long conversationId,
                            MutableLiveData<AIMessage> result,
                            MutableLiveData<String> error) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        if (conversationId != null) {
            body.put("conversationId", conversationId);
        }

        retrofitClient.getApiService().sendAIMessage(body).enqueue(new Callback<AIMessage>() {
            @Override
            public void onResponse(Call<AIMessage> call, Response<AIMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    error.postValue("AI response failed");
                }
            }

            @Override
            public void onFailure(Call<AIMessage> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }
}
