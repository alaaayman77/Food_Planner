package com.example.foodplanner.data.datasource.remote;

import com.example.foodplanner.data.model.recipe_details.RecipeDetails;

import java.util.List;

public interface RecipeDetailsNetworkResponse {

    public void onSuccess(List<RecipeDetails> recipeDetailsList);

    public void onFailure(String errorMessage);

    public void onServerError(String errorMessage);
}
