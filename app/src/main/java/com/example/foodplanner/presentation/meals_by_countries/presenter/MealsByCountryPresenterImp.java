package com.example.foodplanner.presentation.meals_by_countries.presenter;

import android.content.Context;
import android.net.http.HttpException;
import android.util.Log;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.datasource.remote.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.FavoriteMeal;
import com.example.foodplanner.data.model.category.MealsByCategoryResponse;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMeals;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMealsResponse;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.meals_by_category.view.MealsByCategoryView;
import com.example.foodplanner.presentation.meals_by_countries.view.MealsByCountryView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealsByCountryPresenterImp implements MealsByCountryPresenter {

    private MealsByCountryView mealsByCountryView;
    private MealsRepository mealsRepository;
    private FirebaseAuth mAuth;
    private static final String TAG = "MealsByCategoryPresenter";
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MealsByCountryPresenterImp(MealsByCountryView mealsByCountryView, Context context) {
        this.mealsByCountryView = mealsByCountryView;
        mealsRepository = new MealsRepository(context);
        this.mAuth = FirebaseAuth.getInstance();
    }

    private boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
    }
    @Override
    public void getMealsByArea(String area){
        mealsByCountryView.showLoading();

        compositeDisposable.add(
                mealsRepository.getAreaFilteredMeals(area)
                        .subscribeOn(Schedulers.io())
                        .map(AreaFilteredMealsResponse::getMealsFilteredByArea)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                mealsByAreaList -> {
                                    mealsByCountryView.hideLoading();
                                    if (!mealsByAreaList.isEmpty()) {
                                        mealsByCountryView.setMealByAreaList(mealsByAreaList);
                                    } else {
                                        mealsByCountryView.showError("No meals found for " + area);
                                    }
                                },
                                error -> {
                                    mealsByCountryView.hideLoading();
                                    if (error instanceof IOException) {
                                        mealsByCountryView.showError("Network error: " + error.getMessage());
                                    } else if (error instanceof HttpException) {
                                        mealsByCountryView.showError("Server error: " + error.getMessage());
                                    } else {
                                        mealsByCountryView.showError("Unknown error occurred");
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
            mealsByCountryView.showSignInPrompt(
                    "Save Meal Plan",
                    "Sign in to save your meal plans and access them from any device!"
            );
            return;
        }
        mealsRepository.insertMealToMealPlan(mealPlan);
        mealsByCountryView.onMealPlanAddedSuccess();

        saveMealPlanToFirestore(mealPlan);
    }

    @Override
    public void addToFav(FavoriteMeal favoriteMeal) {
        mealsRepository.addtoFav(favoriteMeal);
        mealsByCountryView.onFavAddedSuccess();
    }

    @Override
    public void removeFromFav(String mealId) {
        // No guest check needed - if user isn't signed in, there won't be favorites to remove
        mealsRepository.deleteFav(mealId);

    }

    @Override
    public void fetchRecipeDetailsAndAddToFavorites(String mealId) {
        if (!isUserSignedIn()) {
            mealsByCountryView.showSignInPrompt(
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

                                    mealsByCountryView.onFavAddedFailure(errorMessage);
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
                mealsByCountryView.onMealPlanAddedFailure(error);
            }
        });
    }
}