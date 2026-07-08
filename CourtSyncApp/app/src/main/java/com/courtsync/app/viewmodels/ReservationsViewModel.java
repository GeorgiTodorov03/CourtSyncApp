package com.courtsync.app.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.Reservation;
import com.courtsync.app.repositories.ReservationRepository;
import java.util.List;

public class ReservationsViewModel extends AndroidViewModel {
    private final ReservationRepository repository;
    private final MutableLiveData<List<Reservation>> upcomingReservations = new MutableLiveData<>();
    private final MutableLiveData<List<Reservation>> pastReservations = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cancelResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ReservationsViewModel(@NonNull Application application) {
        super(application);
        repository = new ReservationRepository(application);
        upcomingReservations.observeForever(r -> isLoading.setValue(false));
        pastReservations.observeForever(r -> isLoading.setValue(false));
    }

    public LiveData<List<Reservation>> getUpcoming() { return upcomingReservations; }
    public LiveData<List<Reservation>> getPast() { return pastReservations; }
    public LiveData<Boolean> getCancelResult() { return cancelResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadUpcoming() {
        isLoading.setValue(true);
        repository.getUpcoming(upcomingReservations, errorMessage);
    }

    public void loadPast() {
        isLoading.setValue(true);
        repository.getPast(pastReservations, errorMessage);
    }

    public void cancelReservation(long reservationId) {
        repository.cancel(reservationId, cancelResult, errorMessage);
    }

    public void clearCancelResult() {
        cancelResult.setValue(null);
    }
}
