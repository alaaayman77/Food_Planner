package com.example.foodplanner.presentation.multi_filter;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.AreaFilteredMealsNetworkResponse;
import com.example.foodplanner.data.datasource.MealsByCategoryNetworkResponse;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMeals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


    public class MultiFilterPresenterImp implements MultiFilterPresenter {
        private MultiFilterView multiFilterView;
        private MealsRepository mealsRepository;

        private List<String> categoryMealIds = new ArrayList<>();
        private List<String> areaMealIds = new ArrayList<>();
        private int completedRequests = 0;
        private int totalRequests = 0;

        public MultiFilterPresenterImp(MultiFilterView multiFilterView) {
            this.multiFilterView = multiFilterView;
            this.mealsRepository = new MealsRepository();
        }


        @Override
        public void searchWithFilters(List<String> categories, List<String> areas) {

            categoryMealIds.clear();
            areaMealIds.clear();
            completedRequests = 0;


            if (categories.isEmpty() && areas.isEmpty()) {
                multiFilterView.showError("Please select at least one filter");
                return;
            }


            totalRequests = categories.size() + areas.size();
            multiFilterView.showLoading();


            for (String category : categories) {
                fetchMealsByCategory(category);
            }


            for (String area : areas) {
                fetchMealsByArea(area);
            }
        }


        private void fetchMealsByCategory(String category) {
            mealsRepository.getMealsByCategory(category, new MealsByCategoryNetworkResponse() {
                @Override
                public void onSuccess(List<MealsByCategory> mealsByCategoryList) {
                    for (MealsByCategory meal : mealsByCategoryList) {
                        categoryMealIds.add(meal.getMealId());
                    }
                    completedRequests++;

                   checkAndShowResults();
                }

                @Override
                public void onFailure(String errorMessage) {
                    multiFilterView.hideLoading();
                    multiFilterView.showError("Error fetching category meals: " + errorMessage);
                }

                @Override
                public void onServerError(String errorMessage) {
                    multiFilterView.hideLoading();
                    multiFilterView.showError("Server error: " + errorMessage);
                }
            });
        }

        private void fetchMealsByArea(String area) {
            mealsRepository.getAreaFilteredMeals(area, new AreaFilteredMealsNetworkResponse() {
                @Override
                public void onSuccess(List<AreaFilteredMeals> areaFilteredMealsList) {
                    for (AreaFilteredMeals meal : areaFilteredMealsList) {
                        areaMealIds.add(meal.getIdMeal());
                    }
                    completedRequests++;
                    checkAndShowResults();
                }

                @Override
                public void onFailure(String errorMessage) {
                    multiFilterView.hideLoading();
                    multiFilterView.showError("Error fetching area meals: " + errorMessage);
                }

                @Override
                public void onServerError(String errorMessage) {
                    multiFilterView.hideLoading();
                    multiFilterView.showError("Server error: " + errorMessage);
                }
            });
        }

        private void checkAndShowResults() {

            if (completedRequests < totalRequests) {
                return;
            }

            multiFilterView.hideLoading();
            List<String> resultMealIds = new ArrayList<>();

            if (!categoryMealIds.isEmpty() && !areaMealIds.isEmpty()) {
                // BOTH selected: Find intersection (meals in BOTH sets)
                // (Seafood OR Dessert) AND (Canadian OR Italian)
                for (String categoryId : categoryMealIds) {
                    if (areaMealIds.contains(categoryId)) {
                        resultMealIds.add(categoryId);
                    }
                }
            } else if (!categoryMealIds.isEmpty()) {
                // ONLY categories selected: Return union of all categories
                // Seafood OR Dessert
                resultMealIds.addAll(categoryMealIds);
            } else if (!areaMealIds.isEmpty()) {//Union of all areas
                // Canadian OR Italian
                resultMealIds.addAll(areaMealIds);
            }

            // Show results
            if (resultMealIds.isEmpty()) {
                multiFilterView.showError("No meals found matching your filters");
            } else {
                multiFilterView.showFilteredResults(resultMealIds);
            }
        }
    }

