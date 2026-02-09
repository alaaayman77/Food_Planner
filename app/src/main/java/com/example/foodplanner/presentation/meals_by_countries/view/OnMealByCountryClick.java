package com.example.foodplanner.presentation.meals_by_countries.view;

import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMeals;

public interface OnMealByCountryClick {
    public void setOnMealClick(AreaFilteredMeals meal);
    void onAddToPlanClick(AreaFilteredMeals meal);
    void onFavoriteClick(AreaFilteredMeals meal);
}
