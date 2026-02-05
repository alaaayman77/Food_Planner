package com.example.foodplanner.presentation.search.view.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.category.Category;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
public class ChipCategoryAdapter {
    private Context context;
    private ChipGroup chipGroup;
    private List<Category> categories;
    private List<Category> selectedCategories;
    private OnCategoryChipClickListener listener;

    public interface OnCategoryChipClickListener {
        void onChipClicked(Category category, boolean isSelected);

    }

    public ChipCategoryAdapter(Context context, ChipGroup chipGroup, OnCategoryChipClickListener listener) {
        this.context = context;
        this.chipGroup = chipGroup;
        this.listener = listener;
        this.categories = new ArrayList<>();
        this.selectedCategories = new ArrayList<>();
    }

    public void setCategories(List<Category> categoryList) {
        this.categories = categoryList;
        populateChips();
    }

    private void populateChips() {
        chipGroup.removeAllViews();

        for (Category category : categories) {
            Chip chip = (Chip) LayoutInflater.from(context)
                    .inflate(R.layout.item_category_chip, chipGroup, false);

            chip.setText(category.getCategoryName());
            chip.setCheckable(true);
            chip.setChecked(false);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedCategories.contains(category)) {
                        selectedCategories.add(category);
                    }
                } else {
                    selectedCategories.remove(category);
                }

                if (listener != null) {
                    listener.onChipClicked(category, isChecked);

                }
            });

            chipGroup.addView(chip);
        }
    }

    public List<Category> getSelectedCategories() {
        return selectedCategories;
    }

    public void clearSelection() {
        selectedCategories.clear();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                ((Chip) child).setChecked(false);
            }
        }
    }
}
