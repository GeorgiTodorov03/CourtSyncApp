package com.courtsync.app.repositories;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.*;
import com.courtsync.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class SportHallRepository {
    private final RetrofitClient retrofitClient;

    public SportHallRepository(Context context) {
        retrofitClient = RetrofitClient.getInstance(context);
    }

    public void getRecommended(MutableLiveData<List<SportHall>> result,
                               MutableLiveData<String> error) {
        retrofitClient.getApiService().getRecommendedHalls().enqueue(new Callback<List<SportHall>>() {
            @Override
            public void onResponse(Call<List<SportHall>> call, Response<List<SportHall>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Failed to load halls");
                }
            }

            @Override
            public void onFailure(Call<List<SportHall>> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void search(String query, Long sportId, String sortBy, int page,
                       MutableLiveData<PagedResponse<SportHall>> result,
                       MutableLiveData<String> error) {
        retrofitClient.getApiService().searchHalls(query, sportId, sortBy, page, 20)
                .enqueue(new Callback<PagedResponse<SportHall>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<SportHall>> call,
                                           Response<PagedResponse<SportHall>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.postValue(response.body());
                        } else {
                            error.postValue("Search failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<SportHall>> call, Throwable t) {
                        error.postValue("Network error: " + t.getMessage());
                    }
                });
    }

    public void getById(long id,
                        MutableLiveData<SportHall> result,
                        MutableLiveData<String> error) {
        retrofitClient.getApiService().getHallById(id).enqueue(new Callback<SportHall>() {
            @Override
            public void onResponse(Call<SportHall> call, Response<SportHall> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Hall not found");
                }
            }

            @Override
            public void onFailure(Call<SportHall> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void toggleFavorite(long hallId,
                               MutableLiveData<Boolean> result,
                               MutableLiveData<String> error) {
        retrofitClient.getApiService().toggleFavorite(hallId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.postValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.postValue("Failed to update favorite");
            }
        });
    }
}
