package com.example.foodplanner.presentation.multi_filter;

import java.util.List;

public interface MultiFilterView {
    void showLoading();
    void hideLoading();
    void showFilteredResults(List<String> mealIds);
    void showError(String errorMessage);
}
