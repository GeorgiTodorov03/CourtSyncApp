package com.courtsync.app.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.User;
import com.courtsync.app.repositories.UserRepository;

public class ProfileViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public LiveData<User> getUser() { return user; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadProfile() {
        isLoading.setValue(true);
        repository.getProfile(user, errorMessage);
        user.observeForever(u -> isLoading.setValue(false));
        errorMessage.observeForever(e -> isLoading.setValue(false));
    }
}
