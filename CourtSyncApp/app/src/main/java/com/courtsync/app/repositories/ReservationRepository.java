package com.courtsync.app.repositories;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.Reservation;
import com.courtsync.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationRepository {
    private final RetrofitClient retrofitClient;

    public ReservationRepository(Context context) {
        retrofitClient = RetrofitClient.getInstance(context);
    }

    public void getUpcoming(MutableLiveData<List<Reservation>> result,
                            MutableLiveData<String> error) {
        retrofitClient.getApiService().getUpcomingReservations().enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Failed to load reservations");
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void getPast(MutableLiveData<List<Reservation>> result,
                        MutableLiveData<String> error) {
        retrofitClient.getApiService().getPastReservations().enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Failed to load past reservations");
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void create(long hallId, String date, String startTime, String endTime,
                       MutableLiveData<Reservation> result,
                       MutableLiveData<String> error) {
        Map<String, Object> body = new HashMap<>();
        body.put("hallId", hallId);
        body.put("date", date);
        body.put("startTime", startTime);
        body.put("endTime", endTime);

        retrofitClient.getApiService().createReservation(body).enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Failed to create reservation");
                }
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void cancel(long reservationId,
                       MutableLiveData<Boolean> result,
                       MutableLiveData<String> error) {
        retrofitClient.getApiService().cancelReservation(reservationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.postValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.postValue("Failed to cancel reservation");
            }
        });
    }
}
