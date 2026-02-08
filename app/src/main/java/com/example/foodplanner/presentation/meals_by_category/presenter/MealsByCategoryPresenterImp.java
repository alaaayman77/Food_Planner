package com.example.foodplanner.presentation.meals_by_category.presenter;

import android.content.Context;
import android.util.Log;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealsByCategoryNetworkResponse;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
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
        } else {
            //guest mode
        }
    }
    private void saveMealPlanToFirestore(MealPlan mealPlan) {
        MealPlanFirestore firestorePlan = MealPlanFirestore.fromMealPlan(mealPlan);

        mealsRepository.saveMealPlanToFirestore(firestorePlan, new MealPlanFirestoreNetworkResponse() {
            @Override
            public void onSaveSuccess() {
                Log.d(TAG, "Meal plan synced to Firestore");
                mealsByCategoryView.onMealPlanAddedSuccess();
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