package com.example.foodplanner.data.datasource.remote;

import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;

import java.util.List;

public interface MealPlanFirestoreNetworkResponse {
    void onSaveSuccess();
    void onFetchSuccess(List<MealPlanFirestore> mealPlans);
    void onDeleteSuccess();
    void onFailure(String error);
}
