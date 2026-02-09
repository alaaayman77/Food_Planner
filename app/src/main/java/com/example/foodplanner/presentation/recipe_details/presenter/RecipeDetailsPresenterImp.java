package com.example.foodplanner.presentation.recipe_details.presenter;

import android.content.Context;
import android.util.Log;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.datasource.remote.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.recipe_details.view.RecipeDetailsView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RecipeDetailsPresenterImp implements RecipeDetailsPresenter {
    RecipeDetailsView recipeDetailsView;

    private MealsRepository mealsRepository;
    private FirebaseAuth mAuth;
    private static final String TAG = "RecipeDetailsImp";
    public RecipeDetailsPresenterImp(RecipeDetailsView  recipeDetailsView, Context context) {
        this.mealsRepository = new MealsRepository(context);
        this.recipeDetailsView = recipeDetailsView;
        this.mAuth = FirebaseAuth.getInstance();
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

    @Override
    public void addMealToPlan(MealPlan mealPlan) {
        mealsRepository.insertMealToMealPlan(mealPlan);
        recipeDetailsView.onMealPlanAddedSuccess();

        if (mAuth.getCurrentUser() != null) {
            saveMealPlanToFirestore(mealPlan);
        }
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
                recipeDetailsView.onMealPlanAddedFailure(error);
            }
        });
    }

}
