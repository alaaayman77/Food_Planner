package com.example.foodplanner.presentation.home.presenter;

import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.meal_plan.MealPlan;

public interface HomePresenter {
    public void getRandomMeal();
    public void getCategory();
    public void onCategoryClick(Category category);

    public void addMealToPlan(MealPlan mealPlan);
}
