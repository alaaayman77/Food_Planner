package com.example.foodplanner.data.model.recipe_details;

import com.example.foodplanner.data.model.random_meals.RandomMeal;

import java.util.List;

public class RecipeDetailsResponse {
    private List<RecipeDetails> meals;

    public RecipeDetailsResponse() {
    }

    public List<RecipeDetails> getRecipeDetails() {
        return meals;
    }
}
