package com.courtsync.app.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.AuthResponse;
import com.courtsync.app.repositories.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repository;
    private final MutableLiveData<AuthResponse> authResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
    }

    public LiveData<AuthResponse> getAuthResult() { return authResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void login(String email, String password) {
        if (!validateEmail(email) || !validatePassword(password)) return;
        isLoading.setValue(true);
        repository.login(email, password, authResult, errorMessage);
        authResult.observeForever(r -> isLoading.setValue(false));
        errorMessage.observeForever(e -> isLoading.setValue(false));
    }

    public void register(String fullName, String email, String password, String confirmPassword) {
        if (fullName.isEmpty()) {
            errorMessage.setValue("Full name is required");
            return;
        }
        if (!validateEmail(email)) return;
        if (!validatePassword(password)) return;
        if (!password.equals(confirmPassword)) {
            errorMessage.setValue("Passwords do not match");
            return;
        }
        isLoading.setValue(true);
        repository.register(fullName, email, password, authResult, errorMessage);
    }

    private boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            errorMessage.setValue("Email is required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.setValue("Enter a valid email address");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password == null || password.length() < 6) {
            errorMessage.setValue("Password must be at least 6 characters");
            return false;
        }
        return true;
    }
}
