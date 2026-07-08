package com.courtsync.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.courtsync.app.R;
import com.courtsync.app.adapters.SportHallCardAdapter;
import com.courtsync.app.utils.SessionManager;
import com.courtsync.app.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment {
    private HomeViewModel viewModel;
    private SportHallCardAdapter adapter;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        sessionManager = new SessionManager(requireContext());

        // Set user name
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        tvUserName.setText(sessionManager.getUserName());

        // Set avatar
        ImageView ivAvatar = view.findViewById(R.id.ivAvatar);
        String avatarUrl = sessionManager.getUserAvatar();
        if (avatarUrl != null) {
            Glide.with(this).load(avatarUrl).circleCrop().into(ivAvatar);
        }

        // Setup RecyclerView
        RecyclerView rvHalls = view.findViewById(R.id.rvRecommendedHalls);
        adapter = new SportHallCardAdapter();
        rvHalls.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHalls.setAdapter(adapter);

        adapter.setListener(hall -> {
            Bundle args = new Bundle();
            args.putLong("hallId", hall.getId());
            Navigation.findNavController(view)
                    .navigate(R.id.action_home_to_details, args);
        });

        // AI Coach button
        view.findViewById(R.id.btnAskAI).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_home_to_ai));

        // View All
        view.findViewById(R.id.tvViewAll).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.searchFragment));

        // Observe
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextView tvEmpty = view.findViewById(R.id.tvEmpty);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getRecommendedHalls().observe(getViewLifecycleOwner(), halls -> {
            if (halls != null && !halls.isEmpty()) {
                adapter.submitList(halls);
                tvEmpty.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), err -> {
            if (err != null) {
                Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loadRecommendedHalls();
    }
}
