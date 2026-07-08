package com.courtsync.app.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.courtsync.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SortFilterBottomSheet extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "sort_filter_request";
    public static final String ARG_FIELD = "field";
    public static final String ARG_REVERSE = "reverse";

    private String selectedField;
    private boolean reverse;

    public static SortFilterBottomSheet newInstance(String currentField, boolean currentReverse) {
        SortFilterBottomSheet sheet = new SortFilterBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_FIELD, currentField);
        args.putBoolean(ARG_REVERSE, currentReverse);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_sort, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedField = getArguments() != null ? getArguments().getString(ARG_FIELD, "rating") : "rating";
        reverse = getArguments() != null && getArguments().getBoolean(ARG_REVERSE, false);

        RadioGroup sortFieldGroup = view.findViewById(R.id.sortFieldGroup);
        RadioButton rbRating = view.findViewById(R.id.rbRating);
        RadioButton rbPrice = view.findViewById(R.id.rbPrice);
        RadioButton rbName = view.findViewById(R.id.rbName);
        SwitchMaterial switchReverse = view.findViewById(R.id.switchReverse);

        switch (selectedField) {
            case "price":
                rbPrice.setChecked(true);
                break;
            case "name":
                rbName.setChecked(true);
                break;
            default:
                rbRating.setChecked(true);
                break;
        }
        switchReverse.setChecked(reverse);

        sortFieldGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPrice) {
                selectedField = "price";
            } else if (checkedId == R.id.rbName) {
                selectedField = "name";
            } else {
                selectedField = "rating";
            }
        });

        switchReverse.setOnCheckedChangeListener((buttonView, isChecked) -> reverse = isChecked);

        view.findViewById(R.id.btnApplySort).setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(ARG_FIELD, selectedField);
            result.putBoolean(ARG_REVERSE, reverse);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });
    }
}
