package com.example.foodplanner.presentation.mealplanner.presenter;

public interface MealPlannerPresenter {
    void loadMealPlansForDay(String dayOfWeek);
    void deleteMealPlanById(int mealPlanId);
}
