package com.example.foodplanner.presentation.filter_results.presenter;

import android.content.Context;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.filter_results.view.FilterResultsView;

import java.util.ArrayList;
import java.util.List;

public class FilterResultsPresenterImp implements FilteredResultsPresenter {
    private FilterResultsView filterResultsView;
    private MealsRepository mealsRepository;
    private List<RecipeDetails> mealDetailsList;
    private int loadedCount = 0;
    private int totalCount = 0;

    public FilterResultsPresenterImp(FilterResultsView filterResultsView , Context context) {
        this.mealsRepository = new MealsRepository(context);
        this.filterResultsView = filterResultsView;
        this.mealDetailsList = new ArrayList<>();
    }
    @Override
    public void getFilteredRecipes(String[] mealIds) {
        if (mealIds == null || mealIds.length == 0) {
            filterResultsView.showEmptyState();
            return;
        }

        filterResultsView.showLoading();
        totalCount = mealIds.length;
        loadedCount = 0;
        mealDetailsList.clear();

        // Fetch details for each meal ID
        for (String mealId : mealIds) {
            mealsRepository.getRecipeDetails(mealId, new RecipeDetailsNetworkResponse() {


                @Override
                public void onSuccess(List<RecipeDetails> recipeDetailsList) {
                    if (recipeDetailsList != null) {
                        mealDetailsList.add(recipeDetailsList.get(0));
                    }
                    loadedCount++;


                    if (loadedCount == totalCount) {
                        filterResultsView.hideLoading();
                        if (mealDetailsList.isEmpty()) {
                            filterResultsView.showEmptyState();
                        } else {
                            filterResultsView.displayMeals(mealDetailsList);
                        }
                    }
                }

                @Override
                public void onFailure(String errorMsg) {
                    loadedCount++;

                    if (loadedCount == totalCount) {
                        filterResultsView.hideLoading();
                        if (mealDetailsList.isEmpty()) {
                            filterResultsView.showError(errorMsg);
                        } else {
                            filterResultsView.displayMeals(mealDetailsList);
                        }
                    }
                }

                @Override
                public void onServerError(String errorMessage) {
                    filterResultsView.showError(errorMessage);
                }
            });
        }
    }
}
