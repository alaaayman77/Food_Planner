package com.example.foodplanner.data.datasource.remote;

import com.example.foodplanner.data.model.category.MealsByCategory;

import java.util.List;

public interface MealsByCategoryNetworkResponse {
    public void onSuccess(List<MealsByCategory> mealsByCategoryList);


    public void onFailure(String errorMessage);

    public void onServerError(String errorMessage);
}
