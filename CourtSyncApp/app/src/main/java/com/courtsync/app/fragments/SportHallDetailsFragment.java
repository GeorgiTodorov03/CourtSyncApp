package com.courtsync.app.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.bumptech.glide.Glide;
import com.courtsync.app.R;
import com.courtsync.app.models.SportHall;
import com.courtsync.app.repositories.ReservationRepository;
import com.courtsync.app.viewmodels.SportHallDetailsViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import java.util.Calendar;

public class SportHallDetailsFragment extends Fragment implements OnMapReadyCallback {
    private SportHallDetailsViewModel viewModel;
    private SportHall currentHall;
    private boolean isFavorite = false;
    private MapView mapView;
    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sport_hall_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SportHallDetailsViewModel.class);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        long hallId = getArguments() != null ? getArguments().getLong("hallId", -1) : -1;
        if (hallId == -1) {
            Navigation.findNavController(view).navigateUp();
            return;
        }

        // Views
        ImageView ivHall = view.findViewById(R.id.ivHall);
        TextView tvHallName = view.findViewById(R.id.tvHallName);
        TextView tvSport = view.findViewById(R.id.tvSport);
        TextView tvRating = view.findViewById(R.id.tvRating);
        TextView tvOpenHours = view.findViewById(R.id.tvOpenHours);
        TextView tvRate = view.findViewById(R.id.tvRate);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        ImageView ivFavoriteIcon = view.findViewById(R.id.ivFavoriteIcon);

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        view.findViewById(R.id.btnFavorite).setOnClickListener(v -> {
            viewModel.toggleFavorite(hallId);
        });

        view.findViewById(R.id.btnShare).setOnClickListener(v -> {
            if (currentHall != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Check out " + currentHall.getName() + " on CourtSync!");
                startActivity(Intent.createChooser(shareIntent, "Share Hall"));
            }
        });

        view.findViewById(R.id.btnOpenMaps).setOnClickListener(v -> {
            if (currentHall != null && currentHall.getLatitude() != null) {
                Uri uri = Uri.parse(String.format("geo:%f,%f?q=%f,%f(%s)",
                        currentHall.getLatitude(), currentHall.getLongitude(),
                        currentHall.getLatitude(), currentHall.getLongitude(),
                        currentHall.getName()));
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        view.findViewById(R.id.btnRentNow).setOnClickListener(v -> {
            if (currentHall != null) {
                showBookingDialog(currentHall, view);
            }
        });

        // Observe
        viewModel.getHall().observe(getViewLifecycleOwner(), hall -> {
            if (hall == null) return;
            currentHall = hall;
            isFavorite = hall.isFavorite();

            tvHallName.setText(hall.getName());
            if (hall.getSportName() != null) tvSport.setText(hall.getSportName());
            tvRating.setText(String.format("★ %.1f (%d reviews)", hall.getRating(), hall.getReviewCount()));
            if (hall.getOpenTime() != null && hall.getCloseTime() != null) {
                tvOpenHours.setText(hall.getOpenTime() + " - " + hall.getCloseTime());
            }
            if (hall.getPricePerHour() != null) {
                tvRate.setText(String.format("$%.2f / hr", hall.getPricePerHour().doubleValue()));
                tvTotalPrice.setText(String.format("$%.2f", hall.getPricePerHour().doubleValue()));
            }
            tvDescription.setText(hall.getDescription());
            tvAddress.setText(hall.getAddress());

            updateFavoriteIcon(ivFavoriteIcon);
            updateMapMarker();

            Glide.with(this)
                    .load(hall.getImageUrl())
                    .centerCrop()
                    .into(ivHall);
        });

        viewModel.getFavoriteToggled().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                isFavorite = !isFavorite;
                updateFavoriteIcon(ivFavoriteIcon);
                Snackbar.make(view,
                        isFavorite ? "Added to favorites" : "Removed from favorites",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });

        viewModel.loadHall(hallId);
    }

    private void updateFavoriteIcon(ImageView iv) {
        iv.setImageTintList(android.content.res.ColorStateList.valueOf(
                requireContext().getColor(isFavorite ? R.color.error : R.color.foreground)));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        updateMapMarker();
    }

    private void updateMapMarker() {
        if (googleMap == null || currentHall == null
                || currentHall.getLatitude() == null || currentHall.getLongitude() == null) {
            return;
        }
        LatLng position = new LatLng(currentHall.getLatitude(), currentHall.getLongitude());
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(position).title(currentHall.getName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        if (mapView != null) mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mapView != null) mapView.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    private void showBookingDialog(SportHall hall, View view) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (datePicker, year, month, day) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            new TimePickerDialog(requireContext(), (timePicker, hour, min) -> {
                String startTime = String.format("%02d:%02d", hour, min);
                new TimePickerDialog(requireContext(), (tp, h2, m2) -> {
                    String endTime = String.format("%02d:%02d", h2, m2);
                    ReservationRepository repo = new ReservationRepository(requireContext());
                    androidx.lifecycle.MutableLiveData<com.courtsync.app.models.Reservation> result = new androidx.lifecycle.MutableLiveData<>();
                    androidx.lifecycle.MutableLiveData<String> error = new androidx.lifecycle.MutableLiveData<>();
                    repo.create(hall.getId(), date, startTime, endTime, result, error);
                    result.observe(getViewLifecycleOwner(), r -> {
                        if (r != null) {
                            Toast.makeText(requireContext(), "Reservation confirmed!", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(view).navigate(R.id.action_details_to_reservation);
                        }
                    });
                    error.observe(getViewLifecycleOwner(), e -> {
                        if (e != null) Toast.makeText(requireContext(), e, Toast.LENGTH_SHORT).show();
                    });
                }, hour + 1, 0, true).show();
            }, calendar.get(Calendar.HOUR_OF_DAY), 0, true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
