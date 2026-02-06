package com.example.foodplanner.presentation.filter_results.view;

import com.example.foodplanner.data.model.recipe_details.RecipeDetails;

import java.util.List;

public interface FilterResultsView {
    void showLoading();
    void hideLoading();
    void displayMeals(List<RecipeDetails> meals);
    void showError(String error);
    void showEmptyState();
}
