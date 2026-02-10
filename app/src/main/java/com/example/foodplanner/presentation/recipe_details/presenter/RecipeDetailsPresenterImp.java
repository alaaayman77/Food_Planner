package com.example.foodplanner.presentation.recipe_details.presenter;

import android.content.Context;
import android.net.http.HttpException;
import android.util.Log;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.datasource.remote.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.data.model.recipe_details.RecipeDetailsResponse;
import com.example.foodplanner.presentation.recipe_details.view.RecipeDetailsView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RecipeDetailsPresenterImp implements RecipeDetailsPresenter {
    RecipeDetailsView recipeDetailsView;

    private MealsRepository mealsRepository;
    private FirebaseAuth mAuth;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String TAG = "RecipeDetailsImp";
    public RecipeDetailsPresenterImp(RecipeDetailsView  recipeDetailsView, Context context) {
        this.mealsRepository = new MealsRepository(context);
        this.recipeDetailsView = recipeDetailsView;
        this.mAuth = FirebaseAuth.getInstance();
    }
    private boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    @Override
    public void getRecipeDetails(String id) {
        recipeDetailsView.showLoading();
        compositeDisposable.add(

                mealsRepository.getRecipeDetails(id)
                        .subscribeOn(Schedulers.io())
                        .map(response -> {
                            List<RecipeDetails> recipeDetails = response.getRecipeDetails();
                            if (recipeDetails == null || recipeDetails.isEmpty()) {
                                throw new Exception("No meal found");
                            }
                            return recipeDetails.get(0);
                        })

                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(

                                mealDetails->{
                                    recipeDetailsView.hideLoading();
                                    recipeDetailsView.setRecipeDetails(mealDetails);
                                },
                                error -> {
                                    recipeDetailsView.hideLoading();


                                    if (error instanceof IOException) {
                                        recipeDetailsView.showError("Network error: " + error.getMessage());
                                    } else if (error instanceof HttpException) {
                                        HttpException httpException = (HttpException) error;
                                        recipeDetailsView.showError("Server error: " + error.getMessage());
                                    } else {
                                        recipeDetailsView.showError(error.getMessage());
                                    }
                                }
                        )
        );
    }
//
//    @Override
//    public void getRecipeDetails(String id) {
//        mealsRepository.getRecipeDetails(id, new RecipeDetailsNetworkResponse() {
//            @Override
//            public void onSuccess(List<RecipeDetails> recipeDetailsList) {
//                RecipeDetails recipeDetails = recipeDetailsList.get(0);
//                if (recipeDetails != null) {
//                    recipeDetailsView.setRecipeDetails(recipeDetails);
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                recipeDetailsView.showError(errorMessage);
//            }
//
//            @Override
//            public void onServerError(String errorMessage) {
//                recipeDetailsView.showError(errorMessage);
//            }
//        });
//    }
@Override
public void addMealToPlan(MealPlan mealPlan) {
    if (!isUserSignedIn()) {
        recipeDetailsView.showSignInPrompt(
                "Save Meal Plan",
                "Sign in to save your meal plans and access them from any device!"
        );
        return;
    }

    compositeDisposable.add(
            mealsRepository.insertMealToMealPlan(mealPlan)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                                // onComplete - successfully saved locally
                                Log.d(TAG, " Meal plan saved to local database");
                                recipeDetailsView.onMealPlanAddedSuccess();

                                // Sync to Firestore if user is signed in
                                if (mAuth.getCurrentUser() != null) {
                                    saveMealPlanToFirestore(mealPlan);
                                }
                            },
                            error -> {
                                // onError - failed to save locally
                                Log.e(TAG, " Error saving meal plan locally", error);
                                recipeDetailsView.onMealPlanAddedFailure("Failed to save meal plan");
                            }
                    )
    );
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
