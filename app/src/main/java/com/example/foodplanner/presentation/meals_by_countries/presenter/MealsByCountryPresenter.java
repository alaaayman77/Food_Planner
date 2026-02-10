package com.example.foodplanner.presentation.meals_by_countries.presenter;

import com.example.foodplanner.data.model.favoriteMeal.FavoriteMeal;
import com.example.foodplanner.data.model.meal_plan.MealPlan;

public interface MealsByCountryPresenter {
    public void getMealsByArea(String category);
    public void addMealToPlan(MealPlan mealPlan);
    public void addToFav(FavoriteMeal favoriteMeal);
    void removeFromFav(String mealId);
    void fetchRecipeDetailsAndAddToFavorites(String mealId);

}
