package com.example.foodplanner.data.datasource.remote;

import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMeals;

import java.util.List;

public interface AreaFilteredMealsNetworkResponse {
    public void onSuccess(List<AreaFilteredMeals> areaFilteredMealsList);

    public void onFailure(String errorMessage);

    public void onServerError(String errorMessage);
}
