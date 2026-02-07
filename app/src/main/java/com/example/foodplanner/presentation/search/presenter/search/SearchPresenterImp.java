package com.example.foodplanner.presentation.search.presenter.search;

import android.content.Context;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.CategoryNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealsByCategoryNetworkResponse;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.presentation.search.view.SearchView;


import java.util.ArrayList;
import java.util.List;

public class SearchPresenterImp implements SearchPresenter {
    private SearchView searchView;
    private MealsRepository mealsRepository;

    private List<MealsByCategory> allMeals = new ArrayList<>();
    private int completedCategoryRequests = 0;
    private int totalCategories = 0;

    public SearchPresenterImp(SearchView searchView , Context context) {
        this.searchView = searchView;
        this.mealsRepository = new MealsRepository(context);
    }

    @Override
    public void searchMealsByName(String mealName) {
        if (mealName == null || mealName.trim().isEmpty()) {
            searchView.showError("Please enter a meal name");
            return;
        }

        searchView.showSearchLoading();
        allMeals.clear();
        completedCategoryRequests = 0;

        // First, fetch all categories
        mealsRepository.getCategory(new CategoryNetworkResponse() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                if (categoryList.isEmpty()) {
                    searchView.hideSearchLoading();
                    searchView.showError("No categories available");
                    return;
                }

                totalCategories = categoryList.size();

                // Fetch meals from each category
                for (Category category : categoryList) {
                    fetchMealsFromCategory(category.getCategoryName(), mealName.trim().toLowerCase());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                searchView.hideSearchLoading();
                searchView.showError("Error loading categories: " + errorMessage);
            }

            @Override
            public void onServerError(String errorMessage) {
                searchView.hideSearchLoading();
                searchView.showError("Server error: " + errorMessage);
            }
        });
    }

    private void fetchMealsFromCategory(String categoryName, String searchQuery) {
        mealsRepository.getMealsByCategory(categoryName, new MealsByCategoryNetworkResponse() {
            @Override
            public void onSuccess(List<MealsByCategory> mealsByCategoryList) {
                // Filter meals by name
                for (MealsByCategory meal : mealsByCategoryList) {
                    if (meal.getMealName().toLowerCase().contains(searchQuery)) {
                        allMeals.add(meal);
                    }
                }

                completedCategoryRequests++;
                checkAndShowSearchResults();
            }

            @Override
            public void onFailure(String errorMessage) {
                completedCategoryRequests++;
                checkAndShowSearchResults();
            }

            @Override
            public void onServerError(String errorMessage) {
                completedCategoryRequests++;
                checkAndShowSearchResults();
            }
        });
    }

    private void checkAndShowSearchResults() {
        if (completedCategoryRequests < totalCategories) {
            return; // Wait for all requests to complete
        }

        searchView.hideSearchLoading();

        if (allMeals.isEmpty()) {
            searchView.showError("No meals found matching your search");
        } else {
            searchView.showSearchResults(allMeals);
        }
    }
}