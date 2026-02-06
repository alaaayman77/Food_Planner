package com.example.foodplanner.presentation.multi_filter;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.AreaFilteredMealsNetworkResponse;
import com.example.foodplanner.data.datasource.IngredientFilteredMealsNetworkResponse;
import com.example.foodplanner.data.datasource.MealsByCategoryNetworkResponse;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMeals;
import com.example.foodplanner.data.model.filtered_meals.IngredientFilteredMeals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


    public class MultiFilterPresenterImp implements MultiFilterPresenter {
        private MultiFilterView multiFilterView;
        private MealsRepository mealsRepository;

        private List<String> categoryMealIds = new ArrayList<>();
        private List<String> areaMealIds = new ArrayList<>();
        private List<String> ingredientsMealIds = new ArrayList<>();
        private int completedRequests = 0;
        private int totalRequests = 0;

        public MultiFilterPresenterImp(MultiFilterView multiFilterView) {
            this.multiFilterView = multiFilterView;
            this.mealsRepository = new MealsRepository();
        }


        @Override
        public void searchWithFilters(List<String> categories, List<String> areas, List<String> ingredients) {

            categoryMealIds.clear();
            areaMealIds.clear();
            ingredientsMealIds.clear();
            completedRequests = 0;


            if (categories.isEmpty() && areas.isEmpty() && ingredients.isEmpty()) {
                multiFilterView.showError("Please select at least one filter");
                return;
            }


            totalRequests = categories.size() + areas.size() + ingredients.size();
            multiFilterView.showLoading();


            for (String category : categories) {
                fetchMealsByCategory(category);
            }


            for (String area : areas) {
                fetchMealsByArea(area);
            }
            for (String ingredient : ingredients) {
                fetchMealsByIngredient(ingredient);
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

        private void fetchMealsByIngredient(String ingredient){
            mealsRepository.getIngredientFilteredMeals(ingredient, new IngredientFilteredMealsNetworkResponse() {
                @Override
                public void onSuccess(List<IngredientFilteredMeals> ingredientFilteredMealsList) {
                    for (IngredientFilteredMeals meal : ingredientFilteredMealsList) {
                        ingredientsMealIds.add(meal.getIdMeal());
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

            // Determine which filters are active
            boolean hasCategories = !categoryMealIds.isEmpty();
            boolean hasAreas = !areaMealIds.isEmpty();
            boolean hasIngredients = !ingredientsMealIds.isEmpty();

            if (hasCategories && hasAreas && hasIngredients) {
                // ALL THREE selected: Find intersection (meals in ALL three sets)
                // (Seafood OR Dessert) AND (Canadian OR Italian) AND (Chicken OR Beef)
                Set<String> categorySet = new HashSet<>(categoryMealIds);
                Set<String> areaSet = new HashSet<>(areaMealIds);
                Set<String> ingredientsSet = new HashSet<>(ingredientsMealIds);

                for (String mealId : categorySet) {
                    if (areaSet.contains(mealId) && ingredientsSet.contains(mealId)) {
                        resultMealIds.add(mealId);
                    }
                }
            } else if (hasCategories && hasAreas) {
                // Categories AND Areas selected (NO ingredients)
                // (Seafood OR Dessert) AND (Canadian OR Italian)
                Set<String> areaSet = new HashSet<>(areaMealIds);
                for (String categoryId : categoryMealIds) {
                    if (areaSet.contains(categoryId)) {
                        resultMealIds.add(categoryId);
                    }
                }
            } else if (hasCategories && hasIngredients) {
                // Categories AND Ingredients selected (NO areas)
                // (Seafood OR Dessert) AND (Chicken OR Beef)
                Set<String> ingredientsSet = new HashSet<>(ingredientsMealIds);
                for (String categoryId : categoryMealIds) {
                    if (ingredientsSet.contains(categoryId)) {
                        resultMealIds.add(categoryId);
                    }
                }
            } else if (hasAreas && hasIngredients) {
                // Areas AND Ingredients selected (NO categories)
                // (Canadian OR Italian) AND (Chicken OR Beef)
                Set<String> ingredientsSet = new HashSet<>(ingredientsMealIds);
                for (String areaId : areaMealIds) {
                    if (ingredientsSet.contains(areaId)) {
                        resultMealIds.add(areaId);
                    }
                }
            } else if (hasCategories) {
                // ONLY categories selected: Return union of all categories
                // Seafood OR Dessert
                resultMealIds.addAll(categoryMealIds);
            } else if (hasAreas) {
                // ONLY areas selected: Return union of all areas
                // Canadian OR Italian
                resultMealIds.addAll(areaMealIds);
            } else if (hasIngredients) {
                // ONLY ingredients selected: Return union of all ingredients
                // Chicken OR Beef
                resultMealIds.addAll(ingredientsMealIds);
            }

            // Show results
            if (resultMealIds.isEmpty()) {
                multiFilterView.showError("No meals found matching your filters");
            } else {
                multiFilterView.showFilteredResults(resultMealIds);
            }
        }
    }

