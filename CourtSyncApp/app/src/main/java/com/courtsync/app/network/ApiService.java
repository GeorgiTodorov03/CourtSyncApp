package com.courtsync.app.network;

import com.courtsync.app.models.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

public interface ApiService {

    // Auth
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body Map<String, String> body);

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body Map<String, String> body);

    // Sport Halls
    @GET("api/halls/recommended")
    Call<List<SportHall>> getRecommendedHalls();

    @GET("api/halls")
    Call<PagedResponse<SportHall>> searchHalls(
            @Query("query") String query,
            @Query("sportId") Long sportId,
            @Query("sortBy") String sortBy,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/halls/{id}")
    Call<SportHall> getHallById(@Path("id") long id);

    @POST("api/halls/{id}/favorite")
    Call<Void> toggleFavorite(@Path("id") long id);

    // Reservations
    @GET("api/reservations/upcoming")
    Call<List<Reservation>> getUpcomingReservations();

    @GET("api/reservations/past")
    Call<List<Reservation>> getPastReservations();

    @POST("api/reservations")
    Call<Reservation> createReservation(@Body Map<String, Object> body);

    @DELETE("api/reservations/{id}")
    Call<Void> cancelReservation(@Path("id") long id);

    // User
    @GET("api/users/me")
    Call<User> getMyProfile();

    @PUT("api/users/me")
    Call<User> updateProfile(@Body Map<String, String> body);

    // AI
    @POST("api/ai/chat")
    Call<AIMessage> sendAIMessage(@Body Map<String, Object> body);
}
