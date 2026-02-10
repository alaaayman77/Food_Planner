package com.example.foodplanner.presentation.search.view.area;

import android.content.Context;
import android.view.LayoutInflater;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.search.area.Area;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryChipAdapter {
    private Context context;
    private ChipGroup chipGroup;
    private List<Area> areas;
    private List<Area> selectedAreas;
    private OnCountryChipClickListener listener;
    private Map<String, String> countryFlags;


    public interface OnCountryChipClickListener {
        void onCountryChipClicked(Area area, boolean isSelected);
    }

    public CountryChipAdapter(Context context, ChipGroup chipGroup, OnCountryChipClickListener listener) {
        this.context = context;
        this.chipGroup = chipGroup;
        this.listener = listener;
        this.areas = new ArrayList<>();
        this.selectedAreas = new ArrayList<>();
        initCountryFlags();
    }

    private void initCountryFlags() {
        countryFlags = new HashMap<>();
        countryFlags.put("Algerian", "🇩🇿");
        countryFlags.put("American", "🇺🇸");
        countryFlags.put("British", "🇬🇧");
        countryFlags.put("Canadian", "🇨🇦");
        countryFlags.put("Chinese", "🇨🇳");
        countryFlags.put("Croatian", "🇭🇷");
        countryFlags.put("Dutch", "🇳🇱");
        countryFlags.put("Egyptian", "🇪🇬");
        countryFlags.put("Filipino", "🇵🇭");
        countryFlags.put("French", "🇫🇷");
        countryFlags.put("Greek", "🇬🇷");
        countryFlags.put("Indian", "🇮🇳");
        countryFlags.put("Irish", "🇮🇪");
        countryFlags.put("Italian", "🇮🇹");
        countryFlags.put("Jamaican", "🇯🇲");
        countryFlags.put("Japanese", "🇯🇵");
        countryFlags.put("Kenyan", "🇰🇪");
        countryFlags.put("Malaysian", "🇲🇾");
        countryFlags.put("Mexican", "🇲🇽");
        countryFlags.put("Moroccan", "🇲🇦");
        countryFlags.put("Polish", "🇵🇱");
        countryFlags.put("Portuguese", "🇵🇹");
        countryFlags.put("Russian", "🇷🇺");
        countryFlags.put("Spanish", "🇪🇸");
        countryFlags.put("Thai", "🇹🇭");
        countryFlags.put("Tunisian", "🇹🇳");
        countryFlags.put("Turkish", "🇹🇷");
        countryFlags.put("Vietnamese", "🇻🇳");
    }

    public void setCountries(List<Area> areaList) {
        this.areas = areaList;
        populateChips();
    }

    private void populateChips() {
        chipGroup.removeAllViews();

        for (Area area : areas) {
            Chip chip = (Chip) LayoutInflater.from(context)
                    .inflate(R.layout.item_category_chip, chipGroup, false);

            String flag = countryFlags.getOrDefault(area.getStrArea(), "🌍");
            chip.setText(flag + " " + area.getStrArea());
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

    public void clearSelections() {  // Changed from clearSelection to clearSelections
        selectedAreas.clear();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            if (chipGroup.getChildAt(i) instanceof Chip) {
                ((Chip) chipGroup.getChildAt(i)).setChecked(false);
            }
        }
    }
}