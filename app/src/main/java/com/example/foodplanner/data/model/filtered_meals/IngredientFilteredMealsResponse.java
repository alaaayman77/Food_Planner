package com.example.foodplanner.data.model.filtered_meals;

import java.util.List;

public class IngredientFilteredMealsResponse {
    private List<IngredientFilteredMeals> meals;

    public IngredientFilteredMealsResponse() {
    }

    public List<IngredientFilteredMeals> getMealsFilteredByIngredient() {
        return meals;
    }
}
