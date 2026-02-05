package com.example.foodplanner.presentation.search.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.search.area.Area;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class CountryChipAdapter {
    private Context context;
    private ChipGroup chipGroup;
    private List<Area> area;
    private List<Area> selectedAreas;
    private CountryChipAdapter.OnCountryChipClickListener listener;

    public interface OnCountryChipClickListener {
        void onCountryChipClicked(Area area, boolean isSelected);

    }

    public CountryChipAdapter(Context context, ChipGroup chipGroup, CountryChipAdapter.OnCountryChipClickListener listener) {
        this.context = context;
        this.chipGroup = chipGroup;
        this.listener = listener;
        this.area = new ArrayList<>();
        this.selectedAreas = new ArrayList<>();
    }

    public void setCountries(List<Area> areaList) {
        this.area = areaList;
        populateChips();
    }

    private void populateChips() {
        chipGroup.removeAllViews();

        for (Area area : area) {
            Chip chip = (Chip) LayoutInflater.from(context)
                    .inflate(R.layout.item_category_chip, chipGroup, false);

            chip.setText(area.getStrArea());
            chip.setCheckable(true);
            chip.setChecked(false);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedAreas.contains(area)) {
                        selectedAreas.add(area);
                    }
                } else {
                    selectedAreas.remove(area);
                }

                if (listener != null) {
                    listener.onCountryChipClicked(area, isChecked);

                }
            });

            chipGroup.addView(chip);
        }
    }

    public List<Area> getSelectedAreas() {
        return selectedAreas;
    }

    public void clearSelection() {
        selectedAreas.clear();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                ((Chip) child).setChecked(false);
            }
        }
    }
}
