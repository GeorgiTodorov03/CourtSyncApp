package com.courtsync.app.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.courtsync.app.R;
import com.courtsync.app.adapters.SportHallCardAdapter;
import com.courtsync.app.adapters.SportHallListAdapter;
import com.courtsync.app.viewmodels.SearchViewModel;

public class SearchFragment extends Fragment {
    private SearchViewModel viewModel;
    private SportHallListAdapter adapter;
    private String selectedSport = null;
    private String sortField = "rating";
    private boolean sortReverse = false;
    private TextView tvSortLabel;
    private EditText etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        RecyclerView rvResults = view.findViewById(R.id.rvResults);
        adapter = new SportHallListAdapter();
        rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvResults.setAdapter(adapter);

        adapter.setListener(hall -> {
            Bundle args = new Bundle();
            args.putLong("hallId", hall.getId());
            Navigation.findNavController(view)
                    .navigate(R.id.action_search_to_details, args);
        });

        etSearch = view.findViewById(R.id.etSearch);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextView tvEmpty = view.findViewById(R.id.tvEmpty);
        TextView tvResultCount = view.findViewById(R.id.tvResultCount);
        tvSortLabel = view.findViewById(R.id.tvSortLabel);
        updateSortLabel();

        // Search debounce
        etSearch.addTextChangedListener(new TextWatcher() {
            private Runnable debounceRunnable;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (debounceRunnable != null) view.removeCallbacks(debounceRunnable);
                debounceRunnable = () -> viewModel.search(s.toString(), getSportId(selectedSport), buildSortBy());
                view.postDelayed(debounceRunnable, 400);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Sport filter chips
        setChipSelected(view.findViewById(R.id.chipAll), true);
        setupChip(view, R.id.chipAll, null, etSearch);
        setupChip(view, R.id.chipBasketball, "Basketball", etSearch);
        setupChip(view, R.id.chipFootball, "Football", etSearch);
        setupChip(view, R.id.chipTennis, "Tennis", etSearch);

        // Sort & filter bottom sheet
        view.findViewById(R.id.btnFilter).setOnClickListener(v ->
                SortFilterBottomSheet.newInstance(sortField, sortReverse)
                        .show(getParentFragmentManager(), "sort_filter"));

        getParentFragmentManager().setFragmentResultListener(
                SortFilterBottomSheet.REQUEST_KEY, getViewLifecycleOwner(), (requestKey, bundle) -> {
                    sortField = bundle.getString(SortFilterBottomSheet.ARG_FIELD, "rating");
                    sortReverse = bundle.getBoolean(SortFilterBottomSheet.ARG_REVERSE, false);
                    updateSortLabel();
                    viewModel.search(etSearch.getText().toString().trim(), getSportId(selectedSport), buildSortBy());
                });

        // Observe
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getSearchResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.getContent() != null && !result.getContent().isEmpty()) {
                adapter.submitList(result.getContent());
                tvResultCount.setText(String.format("Found %d facilities", result.getTotalElements()));
                tvEmpty.setVisibility(View.GONE);
                rvResults.setVisibility(View.VISIBLE);
            } else {
                adapter.submitList(java.util.Collections.emptyList());
                tvEmpty.setVisibility(View.VISIBLE);
                rvResults.setVisibility(View.GONE);
                tvResultCount.setText("Found 0 facilities");
            }
        });

        viewModel.searchAll();
    }

    private void setupChip(View root, int chipId, String sport, EditText etSearch) {
        root.findViewById(chipId).setOnClickListener(v -> {
            selectedSport = sport;
            updateChipStates(root, chipId);
            viewModel.search(etSearch.getText().toString().trim(), getSportId(sport), buildSortBy());
        });
    }

    private String buildSortBy() {
        if ("name".equals(sortField)) {
            // Alphabetical: default is A-Z (ascending); reversed is Z-A (descending)
            return sortReverse ? "name_desc" : "name_asc";
        }
        // Rating/Price: default is highest-first (descending); reversed is lowest-first (ascending)
        return sortField + (sortReverse ? "_asc" : "_desc");
    }

    private void updateSortLabel() {
        String fieldLabel;
        String dirLabel;
        switch (sortField) {
            case "price":
                fieldLabel = "Price";
                dirLabel = sortReverse ? "Low-High" : "High-Low";
                break;
            case "name":
                fieldLabel = "Name";
                dirLabel = sortReverse ? "Z-A" : "A-Z";
                break;
            default:
                fieldLabel = "Rating";
                dirLabel = sortReverse ? "Low-High" : "High-Low";
                break;
        }
        tvSortLabel.setText(fieldLabel + " (" + dirLabel + ")");
    }

    private void updateChipStates(View root, int selectedChipId) {
        int[] chipIds = {R.id.chipAll, R.id.chipBasketball, R.id.chipFootball, R.id.chipTennis};
        for (int id : chipIds) {
            setChipSelected(root.findViewById(id), id == selectedChipId);
        }
    }

    private void setChipSelected(View chip, boolean selected) {
        chip.setBackground(requireContext().getDrawable(
                selected ? R.drawable.bg_pill : R.drawable.bg_pill_outline));
        if (chip instanceof TextView) {
            ((TextView) chip).setTextColor(requireContext().getColor(
                    selected ? R.color.on_primary : R.color.foreground));
        }
    }

    private Long getSportId(String sport) {
        if (sport == null) return null;
        // Map sport names to IDs — align with your seed data
        switch (sport) {
            case "Basketball": return 1L;
            case "Football": return 2L;
            case "Tennis": return 3L;
            default: return null;
        }
    }
}
