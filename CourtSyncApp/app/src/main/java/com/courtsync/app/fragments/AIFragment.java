package com.courtsync.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.courtsync.app.R;
import com.courtsync.app.adapters.AIMessageAdapter;
import com.courtsync.app.viewmodels.AIViewModel;

public class AIFragment extends Fragment {
    private AIViewModel viewModel;
    private AIMessageAdapter adapter;
    private RecyclerView rvMessages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ai, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AIViewModel.class);

        rvMessages = view.findViewById(R.id.rvMessages);
        adapter = new AIMessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);

        EditText etMessage = view.findViewById(R.id.etMessage);

        view.findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage(etMessage));

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage(etMessage);
                return true;
            }
            return false;
        });

        // Quick suggestion chips
        view.findViewById(R.id.chipBestRating).setOnClickListener(v ->
                viewModel.sendQuickQuery("What are the best rated courts available right now?"));
        view.findViewById(R.id.chipNearby).setOnClickListener(v ->
                viewModel.sendQuickQuery("Show me the nearest sports courts to my location"));
        view.findViewById(R.id.chipCheapest).setOnClickListener(v ->
                viewModel.sendQuickQuery("What are the most affordable court options?"));

        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                adapter.submitList(messages);
                rvMessages.scrollToPosition(messages.size() - 1);
            }
        });

        viewModel.getIsTyping().observe(getViewLifecycleOwner(), typing -> {
            // Could show a typing indicator here
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });
    }

    private void sendMessage(EditText etMessage) {
        String text = etMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            viewModel.sendMessage(text);
            etMessage.setText("");
        }
    }
}
