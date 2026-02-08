package com.example.foodplanner.presentation.meals_by_category.view;

import com.example.foodplanner.data.model.category.MealsByCategory;

public interface OnMealByCategoryClick {
    public void setOnMealClick(MealsByCategory meal);
    void onAddToPlanClick(MealsByCategory meal);
    void onFavoriteClick(MealsByCategory meal);
}
