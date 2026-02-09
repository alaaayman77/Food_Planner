package com.example.foodplanner.presentation.search.view;

import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.category.MealsByCategory;

import java.util.List;

public interface SearchView {

    void showError(String errorMessage);
    void showSearchLoading();
    void hideSearchLoading();
    void showSearchResults(List<MealsByCategory> meals);
    void appendSearchResults(List<MealsByCategory> meals);
    void updateSearchInfo(int loaded, int total, boolean hasMore);

}
