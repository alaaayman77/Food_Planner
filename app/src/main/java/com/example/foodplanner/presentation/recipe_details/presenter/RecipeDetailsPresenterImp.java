package com.example.foodplanner.presentation.recipe_details.presenter;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.recipe_details.view.RecipeDetailsView;

import java.util.List;

public class RecipeDetailsPresenterImp implements RecipeDetailsPresenter {
    RecipeDetailsView recipeDetailsView;

    private MealsRepository mealsRepository;

    public RecipeDetailsPresenterImp(RecipeDetailsView  recipeDetailsView ) {
        this.mealsRepository = new MealsRepository();
        this.recipeDetailsView = recipeDetailsView;
    }



    @Override
    public void getRecipeDetails(String id) {
        mealsRepository.getRecipeDetails(id, new RecipeDetailsNetworkResponse() {
            @Override
            public void onSuccess(List<RecipeDetails> recipeDetailsList) {
                RecipeDetails recipeDetails = recipeDetailsList.get(0);
                if (recipeDetails != null) {
                    recipeDetailsView.setRecipeDetails(recipeDetails);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                recipeDetailsView.showError(errorMessage);
            }

            @Override
            public void onServerError(String errorMessage) {
                recipeDetailsView.showError(errorMessage);
            }
        });
    }
}
