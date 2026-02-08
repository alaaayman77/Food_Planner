package com.example.foodplanner.presentation.meals_by_category.presenter;

import android.content.Context;
import android.util.Log;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealsByCategoryNetworkResponse;
import com.example.foodplanner.data.datasource.remote.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.FavoriteMeal;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.meals_by_category.view.MealsByCategoryView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MealsByCategoryPresenterImp implements MealsByCategoryPresenter {

    private MealsByCategoryView mealsByCategoryView;
    private MealsRepository mealsRepository;
    private FirebaseAuth mAuth;
    private static final String TAG = "MealsByCategoryPresenter";

    public MealsByCategoryPresenterImp(MealsByCategoryView mealsByCategoryView, Context context) {
        this.mealsByCategoryView = mealsByCategoryView;
        mealsRepository = new MealsRepository(context);
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getMealsByCategory(String category) {
        mealsByCategoryView.showLoading();

        mealsRepository.getMealsByCategory(category, new MealsByCategoryNetworkResponse() {
            @Override
            public void onSuccess(List<MealsByCategory> mealsByCategoryList) {
                if (!mealsByCategoryList.isEmpty()) {
                    mealsByCategoryView.setMealByCategoryList(mealsByCategoryList);
                } else {
                    mealsByCategoryView.hideLoading();
                    mealsByCategoryView.showError("No meals found for " + category);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                mealsByCategoryView.showError(errorMessage);
            }

            @Override
            public void onServerError(String errorMessage) {
                mealsByCategoryView.showError(errorMessage);
            }
        });
    }

    @Override
    public void addMealToPlan(MealPlan mealPlan) {
        mealsRepository.insertMealToMealPlan(mealPlan);
        mealsByCategoryView.onMealPlanAddedSuccess();

        if (mAuth.getCurrentUser() != null) {
            saveMealPlanToFirestore(mealPlan);
        }
    }

    @Override
    public void addToFav(FavoriteMeal favoriteMeal) {
        mealsRepository.addtoFav(favoriteMeal);
        mealsByCategoryView.onFavAddedSuccess();
    }

    @Override
    public void removeFromFav(String mealId) {
        mealsRepository.deleteFav(mealId);

    }

    @Override
    public void fetchRecipeDetailsAndAddToFavorites(String mealId) {
        mealsRepository.getRecipeDetails(mealId, new RecipeDetailsNetworkResponse() {
            @Override
            public void onSuccess(List<RecipeDetails> recipeDetailsList) {
                if (recipeDetailsList != null && !recipeDetailsList.isEmpty()) {
                    RecipeDetails recipe = recipeDetailsList.get(0);
                    FavoriteMeal favoriteMeal = FavoriteMeal.fromRecipeDetails(recipe);
                    addToFav(favoriteMeal);
                } else {
                    mealsByCategoryView.onFavAddedFailure("Could not fetch recipe details");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                mealsByCategoryView.onFavAddedFailure(errorMessage);
            }

            @Override
            public void onServerError(String errorMessage) {
                mealsByCategoryView.onFavAddedFailure(errorMessage);
            }
        });
    }

    private void saveMealPlanToFirestore(MealPlan mealPlan) {
        MealPlanFirestore firestorePlan = MealPlanFirestore.fromMealPlan(mealPlan);

        mealsRepository.saveMealPlanToFirestore(firestorePlan, new MealPlanFirestoreNetworkResponse() {
            @Override
            public void onSaveSuccess() {
                Log.d(TAG, "Meal plan synced to Firestore");
            }

            @Override
            public void onFetchSuccess(List<MealPlanFirestore> mealPlans) {
            }

            @Override
            public void onDeleteSuccess() {
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to sync to Firestore: " + error);
                mealsByCategoryView.onMealPlanAddedFailure(error);
            }
        });
    }
}