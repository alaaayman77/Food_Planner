package com.example.foodplanner.presentation.meals_by_category.presenter;

import com.example.foodplanner.data.model.meal_plan.MealPlan;

public interface MealsByCategoryPresenter {
    public void getMealsByCategory(String category);
    public void addMealToPlan(MealPlan mealPlan);

}
