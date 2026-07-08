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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.courtsync.app.R;
import com.courtsync.app.adapters.ReservationAdapter;
import com.courtsync.app.models.Reservation;
import com.courtsync.app.viewmodels.ReservationsViewModel;

public class ReservationsFragment extends Fragment {
    private ReservationsViewModel viewModel;
    private ReservationAdapter adapter;
    private boolean showingUpcoming = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReservationsViewModel.class);

        RecyclerView rvReservations = view.findViewById(R.id.rvReservations);
        adapter = new ReservationAdapter();
        rvReservations.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvReservations.setAdapter(adapter);

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        LinearLayout emptyState = view.findViewById(R.id.emptyState);
        TextView tabUpcoming = view.findViewById(R.id.tabUpcoming);
        TextView tabPast = view.findViewById(R.id.tabPast);

        adapter.setListener(new ReservationAdapter.OnActionListener() {
            @Override
            public void onCancel(Reservation reservation) {
                showCancelDialog(reservation);
            }

            @Override
            public void onModify(Reservation reservation) {
                Toast.makeText(requireContext(), "Modify coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        tabUpcoming.setOnClickListener(v -> {
            showingUpcoming = true;
            tabUpcoming.setTextColor(requireContext().getColor(R.color.primary));
            tabPast.setTextColor(requireContext().getColor(R.color.muted_foreground));
            viewModel.loadUpcoming();
        });

        tabPast.setOnClickListener(v -> {
            showingUpcoming = false;
            tabPast.setTextColor(requireContext().getColor(R.color.primary));
            tabUpcoming.setTextColor(requireContext().getColor(R.color.muted_foreground));
            viewModel.loadPast();
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getUpcoming().observe(getViewLifecycleOwner(), reservations -> {
            if (showingUpcoming) {
                if (reservations != null && !reservations.isEmpty()) {
                    adapter.submitList(reservations);
                    emptyState.setVisibility(View.GONE);
                    rvReservations.setVisibility(View.VISIBLE);
                } else {
                    adapter.submitList(java.util.Collections.emptyList());
                    emptyState.setVisibility(View.VISIBLE);
                    rvReservations.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getPast().observe(getViewLifecycleOwner(), reservations -> {
            if (!showingUpcoming) {
                if (reservations != null && !reservations.isEmpty()) {
                    adapter.submitList(reservations);
                    emptyState.setVisibility(View.GONE);
                    rvReservations.setVisibility(View.VISIBLE);
                } else {
                    adapter.submitList(java.util.Collections.emptyList());
                    emptyState.setVisibility(View.VISIBLE);
                    rvReservations.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getCancelResult().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Reservation cancelled", Toast.LENGTH_SHORT).show();
                viewModel.loadUpcoming();
                viewModel.clearCancelResult();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });

        viewModel.loadUpcoming();
    }

    private void showCancelDialog(Reservation reservation) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.cancel_confirm_title))
                .setMessage(getString(R.string.cancel_confirm_message))
                .setPositiveButton("Yes, Cancel", (dialog, which) ->
                        viewModel.cancelReservation(reservation.getId()))
                .setNegativeButton("Keep It", null)
                .show();
    }
}
