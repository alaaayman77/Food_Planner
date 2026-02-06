package com.example.foodplanner.data.datasource;

import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMeals;
import com.example.foodplanner.data.model.filtered_meals.IngredientFilteredMeals;

import java.util.List;

public interface IngredientFilteredMealsNetworkResponse {
    public void onSuccess(List<IngredientFilteredMeals> ingredientFilteredMealsList);

    public void onFailure(String errorMessage);

    public void onServerError(String errorMessage);
}
