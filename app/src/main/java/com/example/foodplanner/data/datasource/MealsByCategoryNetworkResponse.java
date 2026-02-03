package com.example.foodplanner.data.datasource;

import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.random_meals.RandomMeal;

import java.util.List;

public interface MealsByCategoryNetworkResponse {
    public void onSuccess(List<MealsByCategory> mealsByCategoryList);


    public void onFailure(String errorMessage);

    public void onServerError(String errorMessage);
}
