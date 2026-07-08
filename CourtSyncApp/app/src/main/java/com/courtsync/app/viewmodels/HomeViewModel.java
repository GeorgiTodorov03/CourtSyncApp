package com.courtsync.app.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.SportHall;
import com.courtsync.app.repositories.SportHallRepository;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final SportHallRepository repository;
    private final MutableLiveData<List<SportHall>> recommendedHalls = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new SportHallRepository(application);
    }

    public LiveData<List<SportHall>> getRecommendedHalls() { return recommendedHalls; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadRecommendedHalls() {
        isLoading.setValue(true);
        repository.getRecommended(recommendedHalls, errorMessage);
        recommendedHalls.observeForever(h -> isLoading.setValue(false));
        errorMessage.observeForever(e -> isLoading.setValue(false));
    }
}
