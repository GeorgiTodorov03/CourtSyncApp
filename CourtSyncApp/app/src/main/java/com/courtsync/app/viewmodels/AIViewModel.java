package com.courtsync.app.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.courtsync.app.models.AIMessage;
import com.courtsync.app.repositories.AIRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AIViewModel extends AndroidViewModel {
    private final AIRepository repository;
    private final MutableLiveData<List<AIMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isTyping = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private Long currentConversationId = null;

    public AIViewModel(@NonNull Application application) {
        super(application);
        repository = new AIRepository(application);
        addWelcomeMessage();
    }

    public LiveData<List<AIMessage>> getMessages() { return messages; }
    public LiveData<Boolean> getIsTyping() { return isTyping; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    private void addWelcomeMessage() {
        List<AIMessage> msgs = new ArrayList<>();
        msgs.add(new AIMessage(
                "Hey! 👋 Ready for some action? I can help you find the best courts in the city. What are you looking for today?",
                false,
                getCurrentTime()
        ));
        messages.setValue(msgs);
    }

    public void sendMessage(String text) {
        if (text == null || text.isBlank()) return;

        List<AIMessage> current = new ArrayList<>(messages.getValue() != null ? messages.getValue() : new ArrayList<>());
        current.add(new AIMessage(text, true, getCurrentTime()));
        messages.setValue(current);

        isTyping.setValue(true);

        MutableLiveData<AIMessage> result = new MutableLiveData<>();
        MutableLiveData<String> error = new MutableLiveData<>();

        repository.sendMessage(text, currentConversationId, result, error);

        result.observeForever(response -> {
            if (response != null) {
                currentConversationId = response.getConversationId();
                AIMessage assistantMsg = new AIMessage(response.getContent(), false, getCurrentTime());
                assistantMsg.setConversationId(response.getConversationId());
                if (response.getSuggestedHalls() != null) {
                    assistantMsg.setSuggestedHalls(response.getSuggestedHalls());
                }
                List<AIMessage> updated = new ArrayList<>(messages.getValue() != null ? messages.getValue() : new ArrayList<>());
                updated.add(assistantMsg);
                messages.postValue(updated);
                isTyping.postValue(false);
            }
        });

        error.observeForever(err -> {
            if (err != null) {
                errorMessage.postValue(err);
                isTyping.postValue(false);
            }
        });
    }

    public void sendQuickQuery(String query) {
        sendMessage(query);
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }
}
