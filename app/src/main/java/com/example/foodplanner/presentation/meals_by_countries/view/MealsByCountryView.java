package com.example.foodplanner.presentation.meals_by_countries.view;

import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMeals;

import java.util.List;

public interface MealsByCountryView {
    public void setMealByAreaList(List<AreaFilteredMeals> mealByAreaList);
    public void showError(String errorMessage);
    void showLoading();
    void hideLoading();
    public void onMealPlanAddedSuccess();

    public void onMealPlanAddedFailure(String error);
    public void onFavAddedSuccess();
    public void onFavAddedFailure(String error);
    void showSignInPrompt(String featureName, String message);
}
