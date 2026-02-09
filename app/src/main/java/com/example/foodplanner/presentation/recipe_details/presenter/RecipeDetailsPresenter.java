package com.example.foodplanner.presentation.recipe_details.presenter;

import com.example.foodplanner.data.model.meal_plan.MealPlan;

public interface RecipeDetailsPresenter {
    public void getRecipeDetails(String id);
    void addMealToPlan(MealPlan mealPlan);
}
