package com.courtsync.app.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.*;
import com.courtsync.app.repositories.SportHallRepository;

public class SearchViewModel extends AndroidViewModel {
    private final SportHallRepository repository;
    private final MutableLiveData<PagedResponse<SportHall>> searchResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private String currentQuery = null;
    private Long currentSportId = null;
    private String currentSort = "rating_desc";

    public SearchViewModel(@NonNull Application application) {
        super(application);
        repository = new SportHallRepository(application);
        searchResult.observeForever(r -> isLoading.setValue(false));
        errorMessage.observeForever(e -> isLoading.setValue(false));
    }

    public LiveData<PagedResponse<SportHall>> getSearchResult() { return searchResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void search(String query, Long sportId, String sortBy) {
        this.currentQuery = (query != null && !query.isBlank()) ? query : null;
        this.currentSportId = sportId;
        this.currentSort = sortBy != null ? sortBy : "rating_desc";
        isLoading.setValue(true);
        repository.search(currentQuery, currentSportId, currentSort, 0, searchResult, errorMessage);
    }

    public void searchAll() {
        search(null, null, "rating_desc");
    }

    public void filterBySport(Long sportId) {
        search(currentQuery, sportId, currentSort);
    }

    public String getCurrentSort() {
        return currentSort;
    }
}
