package com.courtsync.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.courtsync.app.R;
import com.courtsync.app.activities.LoginActivity;
import com.courtsync.app.utils.SessionManager;
import com.courtsync.app.viewmodels.ProfileViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {
    private ProfileViewModel viewModel;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        sessionManager = new SessionManager(requireContext());

        ImageView ivAvatar = view.findViewById(R.id.ivAvatar);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        TextView tvBookings = view.findViewById(R.id.tvBookings);
        TextView tvHours = view.findViewById(R.id.tvHours);
        TextView tvCredits = view.findViewById(R.id.tvCredits);

        // Sign out
        view.findViewById(R.id.btnSignOut).setOnClickListener(v ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Sign Out", (dialog, which) -> {
                            sessionManager.clearSession();
                            startActivity(new Intent(requireActivity(), LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        })
                        .setNegativeButton("Cancel", null)
                        .show());

        view.findViewById(R.id.rowPersonalInfo).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Personal info editor coming soon", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.rowPayment).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Payment methods coming soon", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btnStartHosting).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Hall owner registration coming soon", Toast.LENGTH_SHORT).show());

        // Observe profile
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvUserName.setText(user.getFullName() != null ? user.getFullName().toUpperCase() : "");
                tvBookings.setText(String.valueOf(user.getTotalBookings()));
                tvHours.setText(String.format("%.1f", user.getTotalHours()));
                if (user.getCredits() != null) {
                    tvCredits.setText("$" + user.getCredits().toPlainString());
                }
                if (user.getProfileImageUrl() != null) {
                    Glide.with(this)
                            .load(user.getProfileImageUrl())
                            .circleCrop()
                            .into(ivAvatar);
                }
                sessionManager.updateUserName(user.getFullName());
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });

        viewModel.loadProfile();
    }
}
