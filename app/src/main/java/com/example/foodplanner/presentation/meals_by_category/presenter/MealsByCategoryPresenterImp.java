package com.example.foodplanner.presentation.meals_by_category.presenter;

import android.content.Context;

import android.net.http.HttpException;
import android.util.Log;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.model.favoriteMeal.FavoriteMeal;
import com.example.foodplanner.data.model.category.MealsByCategoryResponse;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.meals_by_category.view.MealsByCategoryView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealsByCategoryPresenterImp implements MealsByCategoryPresenter {

    private MealsByCategoryView mealsByCategoryView;
    private MealsRepository mealsRepository;
    private FirebaseAuth mAuth;
    private static final String TAG = "MealsByCategoryPresenter";
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MealsByCategoryPresenterImp(MealsByCategoryView mealsByCategoryView, Context context) {
        this.mealsByCategoryView = mealsByCategoryView;
        mealsRepository = new MealsRepository(context);
        this.mAuth = FirebaseAuth.getInstance();
    }

    private boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
    }
    @Override
    public void getMealsByCategory(String category){
        mealsByCategoryView.showLoading();

        compositeDisposable.add(
                mealsRepository.getMealsByCategory(category)
                        .subscribeOn(Schedulers.io())
                        .map(MealsByCategoryResponse::getMealsByCategories)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                mealsByCategoryList -> {
                                    mealsByCategoryView.hideLoading();
                                    if (!mealsByCategoryList.isEmpty()) {
                                        mealsByCategoryView.setMealByCategoryList(mealsByCategoryList);
                                    } else {
                                        mealsByCategoryView.showError("No meals found for " + category);
                                    }
                                },
                                error -> {
                                    mealsByCategoryView.hideLoading();
                                    if (error instanceof IOException) {
                                        mealsByCategoryView.showError("Network error: " + error.getMessage());
                                    } else if (error instanceof HttpException) {
                                        mealsByCategoryView.showError("Server error: " + error.getMessage());
                                    } else {
                                        mealsByCategoryView.showError("Unknown error occurred");
                                    }
                                }
                        )
        );
    }


//    @Override
//    public void getMealsByCategory(String category) {
//        mealsByCategoryView.showLoading();
//
//        mealsRepository.getMealsByCategory(category, new MealsByCategoryNetworkResponse() {
//            @Override
//            public void onSuccess(List<MealsByCategory> mealsByCategoryList) {
//                if (!mealsByCategoryList.isEmpty()) {
//                    mealsByCategoryView.setMealByCategoryList(mealsByCategoryList);
//                } else {
//                    mealsByCategoryView.hideLoading();
//                    mealsByCategoryView.showError("No meals found for " + category);
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                mealsByCategoryView.showError(errorMessage);
//            }
//
//            @Override
//            public void onServerError(String errorMessage) {
//                mealsByCategoryView.showError(errorMessage);
//            }
//        });
//    }




    @Override
    public void addMealToPlan(MealPlan mealPlan) {
        if (!isUserSignedIn()) {
            mealsByCategoryView.showSignInPrompt(
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
                                    Log.d(TAG, " Meal plan saved locally");
                                    mealsByCategoryView.onMealPlanAddedSuccess();

//                                    if (NetworkUtils.isNetworkAvailable(context) && mAuth.getCurrentUser() != null) {
                                        saveMealPlanToFirestore(mealPlan);

                                        Log.d(TAG, "Offline: Meal plan saved locally only");

                                },
                                error -> {

                                    Log.e(TAG, " Error saving meal plan locally", error);
                                    mealsByCategoryView.onMealPlanAddedFailure("Failed to save meal plan: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void addToFav(FavoriteMeal favoriteMeal) {
        mealsRepository.addtoFav(favoriteMeal);
        mealsByCategoryView.onFavAddedSuccess();
    }

    @Override
    public void removeFromFav(String mealId) {
        // No guest check needed - if user isn't signed in, there won't be favorites to remove
        mealsRepository.deleteFav(mealId);

    }

    @Override
    public void fetchRecipeDetailsAndAddToFavorites(String mealId) {
        if (!isUserSignedIn()) {
            mealsByCategoryView.showSignInPrompt(
                    "Save to Favorites",
                    "Sign in to save your favorite meals and sync them across devices!"
            );
            return;
        }

        compositeDisposable.add(
                mealsRepository.getRecipeDetails(mealId)
                        .subscribeOn(Schedulers.io())
                        .map(response -> {
                            List<RecipeDetails> recipeDetails = response.getRecipeDetails();
                            if (recipeDetails == null || recipeDetails.isEmpty()) {
                                throw new Exception("Could not fetch recipe details");
                            }
                            return recipeDetails.get(0);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                recipe -> {
                                    FavoriteMeal favoriteMeal = FavoriteMeal.fromRecipeDetails(recipe);
                                    addToFav(favoriteMeal);
                                },
                                error -> {
                                    Log.e(TAG, "Error fetching recipe details", error);

                                    String errorMessage;
                                    if (error instanceof IOException) {
                                        errorMessage = "Network error: " + error.getMessage();
                                    } else if (error instanceof HttpException) {
                                        errorMessage = "Server error: " + error.getMessage();
                                    } else {
                                        errorMessage = error.getMessage();
                                    }

                                    mealsByCategoryView.onFavAddedFailure(errorMessage);
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
                mealsByCategoryView.onMealPlanAddedFailure(error);
            }
        });
    }
}