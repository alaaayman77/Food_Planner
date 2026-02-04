package com.example.foodplanner.presentation.meals_by_category.view;

import com.example.foodplanner.data.model.category.MealsByCategory;

import java.util.List;

public interface MealsByCategoryView {
    public void setMealByCategoryList(List<MealsByCategory> mealsByCategoryList);
    public void showError(String errorMessage);
}
