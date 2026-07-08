package com.courtsync.app.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.SportHall;
import com.courtsync.app.repositories.SportHallRepository;

public class SportHallDetailsViewModel extends AndroidViewModel {
    private final SportHallRepository repository;
    private final MutableLiveData<SportHall> hall = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> favoriteToggled = new MutableLiveData<>();

    public SportHallDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = new SportHallRepository(application);
    }

    public LiveData<SportHall> getHall() { return hall; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getFavoriteToggled() { return favoriteToggled; }

    public void loadHall(long id) {
        isLoading.setValue(true);
        repository.getById(id, hall, errorMessage);
        hall.observeForever(h -> isLoading.setValue(false));
        errorMessage.observeForever(e -> isLoading.setValue(false));
    }

    public void toggleFavorite(long hallId) {
        repository.toggleFavorite(hallId, favoriteToggled, errorMessage);
    }
}
