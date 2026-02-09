package com.example.foodplanner.presentation.recipe_details.view;

import com.example.foodplanner.data.model.recipe_details.RecipeDetails;

public interface RecipeDetailsView {
    public void setRecipeDetails(RecipeDetails recipeDetails);
    public void showError(String errorMessage);
    void  onMealPlanAddedSuccess();
    void onMealPlanAddedFailure(String error);
    void showSignInPrompt(String featureName, String message);
}
