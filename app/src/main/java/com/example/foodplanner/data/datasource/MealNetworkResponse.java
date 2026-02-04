package com.example.foodplanner.data.datasource;

import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.random_meals.RandomMeal;

import java.util.List;

public interface MealNetworkResponse {
    public void onSuccess(List<RandomMeal> randomMealList);


    public void onFailure(String errorMessage);

    public void onServerError(String errorMessage);
}
