package com.example.foodplanner.presentation.mealplanner.view;

import com.example.foodplanner.data.model.meal_plan.MealPlan;

import java.util.List;

public interface MealPlannerView {
    void displayMealPlans(List<MealPlan> mealPlans);

    void hideBreakfastMeal();
    void hideLunchMeal();
    void hideDinnerMeal();
    void showError(String message);
}
