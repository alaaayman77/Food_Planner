package com.example.foodplanner.presentation.mealplanner.presenter;

import com.example.foodplanner.data.model.meal_plan.MealPlan;

public interface MealPlannerPresenter {
    void loadMealPlansForDay(String dayOfWeek);
    void deleteMealPlanById(MealPlan mealPlan);
    void syncMealPlansFromFirestore();

}
