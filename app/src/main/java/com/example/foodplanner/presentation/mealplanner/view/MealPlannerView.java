package com.example.foodplanner.presentation.mealplanner.view;

import com.example.foodplanner.data.model.meal_plan.MealPlan;

import java.util.List;

public interface MealPlannerView {
    void displayMealPlans(List<MealPlan> mealPlans);

    void hideBreakfastMeal();
    void hideLunchMeal();
    void hideDinnerMeal();
    void showBreakfastMeals(List<MealPlan> breakfastMeals);
    void showLunchMeals(List<MealPlan> lunchMeals);
    void showDinnerMeals(List<MealPlan> dinnerMeals);

    void showError(String message);
    void showLoading();
    void hideLoading();
    void onMealPlanDeletedSuccess();
    void onMealPlanDeletedFailure(String errorMessage);
    void onSyncSuccess();

}
