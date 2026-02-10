package com.example.foodplanner.presentation.mealplanner.presenter;

import android.content.Context;
import android.net.http.HttpException;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.datasource.remote.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.mealplanner.view.MealPlannerView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealPlannerPresenterImp implements MealPlannerPresenter {
    private static final String TAG = "MealPlannerPresenter";

    private MealPlannerView view;
    private MealsRepository repository;
    private LifecycleOwner lifecycleOwner;
    private FirebaseAuth mAuth;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    public MealPlannerPresenterImp(MealPlannerView view, Context context, LifecycleOwner lifecycleOwner) {
        this.view = view;
        this.repository = new MealsRepository(context);
        this.lifecycleOwner = lifecycleOwner;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void loadMealPlansForDay(String dayOfWeek) {
        repository.getMealPlansByDay(dayOfWeek).observe(lifecycleOwner, new Observer<List<MealPlan>>() {
            @Override
            public void onChanged(List<MealPlan> mealPlans) {
                // Reset all meal displays first
                view.hideBreakfastMeal();
                view.hideLunchMeal();
                view.hideDinnerMeal();

                if (mealPlans != null && !mealPlans.isEmpty()) {
                    // Group meals by type
                    List<MealPlan> breakfastMeals = new ArrayList<>();
                    List<MealPlan> lunchMeals = new ArrayList<>();
                    List<MealPlan> dinnerMeals = new ArrayList<>();

                    for (MealPlan mealPlan : mealPlans) {
                        String mealType = mealPlan.getMealType();
                        if (mealType != null) {
                            switch (mealType.toUpperCase()) {
                                case "BREAKFAST":
                                    breakfastMeals.add(mealPlan);
                                    break;
                                case "LUNCH":
                                    lunchMeals.add(mealPlan);
                                    break;
                                case "DINNER":
                                    dinnerMeals.add(mealPlan);
                                    break;
                            }
                        }
                    }

                    // Show meals for each type
                    if (!breakfastMeals.isEmpty()) {
                        view.showBreakfastMeals(breakfastMeals);
                    }

                    if (!lunchMeals.isEmpty()) {
                        view.showLunchMeals(lunchMeals);
                    }

                    if (!dinnerMeals.isEmpty()) {
                        view.showDinnerMeals(dinnerMeals);
                    }

                    view.displayMealPlans(mealPlans);
                } else {
                    // Empty state will be shown
                    view.displayMealPlans(new ArrayList<>());
                }
            }
        });
    }

    @Override
    public void deleteMealPlanById(MealPlan mealPlan) {
        // Delete from Room
        repository.deleteMealPlanById(mealPlan.getId());

        // Delete from Firestore if user is authenticated
        if (mAuth.getCurrentUser() != null) {
            deleteMealPlanFromFirestore(mealPlan);
        } else {
            view.onMealPlanDeletedSuccess();
        }
    }

    private void deleteMealPlanFromFirestore(MealPlan mealPlan) {
        repository.deleteMealPlanFromFirestore(
                mealPlan.getMealId(),
                mealPlan.getDayOfWeek(),
                mealPlan.getMealType(),
                new MealPlanFirestoreNetworkResponse() {
                    @Override
                    public void onSaveSuccess() {
                        // Not used
                    }

                    @Override
                    public void onFetchSuccess(List<MealPlanFirestore> mealPlans) {
                        // Not used
                    }

                    @Override
                    public void onDeleteSuccess() {
                        Log.d(TAG, "Meal plan deleted from Firestore");
                        view.onMealPlanDeletedSuccess();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to delete from Firestore: " + error);
                        view.onMealPlanDeletedFailure(error);
                    }
                }
        );
    }

    @Override
    public void syncMealPlansFromFirestore() {
        if (mAuth.getCurrentUser() == null) {
            view.showError("User not authenticated");
            return;
        }

        view.showLoading();

        repository.getAllMealPlansFromFirestore(new MealPlanFirestoreNetworkResponse() {
            @Override
            public void onSaveSuccess() {
                // Not used
            }

            @Override
            public void onFetchSuccess(List<MealPlanFirestore> firestorePlans) {
                if (firestorePlans.isEmpty()) {
                    view.hideLoading();
                    view.onSyncSuccess();
                    return;
                }

                // Fetch and save each meal plan
                for (MealPlanFirestore firestorePlan : firestorePlans) {
                    fetchAndSaveMealPlan(firestorePlan);
                }

                view.hideLoading();
                view.onSyncSuccess();
            }

            @Override
            public void onDeleteSuccess() {
                // Not used
            }

            @Override
            public void onFailure(String error) {
                view.hideLoading();
                view.showError("Sync failed: " + error);
            }
        });
    }
    private void fetchAndSaveMealPlan(MealPlanFirestore firestorePlan) {
        compositeDisposable.add(
                repository.getRecipeDetails(firestorePlan.getMealId())
                        .subscribeOn(Schedulers.io())
                        .map(response -> {
                            List<RecipeDetails> recipeDetails = response.getRecipeDetails();
                            if (recipeDetails == null || recipeDetails.isEmpty()) {
                                throw new Exception("No meal found for ID: " + firestorePlan.getMealId());
                            }
                            return recipeDetails.get(0);
                        })
                        .map(mealDetails -> {
                            // Map RecipeDetails to MealPlan
                            MealPlan mealPlan = new MealPlan(
                                    mealDetails.getIdMeal(),
                                    firestorePlan.getMealType(),
                                    firestorePlan.getDayOfWeek(),
                                    mealDetails.getMealName(),
                                    mealDetails.getStrMealThumbnail(),
                                    mealDetails.getMealCategory(),
                                    mealDetails.getMealArea(),
                                    mealDetails.getMealInstructions()
                            );
                            mealPlan.setTimestamp(firestorePlan.getTimestamp());
                            return mealPlan;
                        })
                        .flatMapCompletable(mealPlan ->
                                repository.insertMealToMealPlan(mealPlan)
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    Log.d(TAG, " Successfully synced meal: " + firestorePlan.getMealId());

                                },
                                error -> {

                                    Log.e(TAG, "Error syncing meal: " + firestorePlan.getMealId(), error);
                                }
                        )
        );
    }
//    private void fetchAndSaveMealPlan(MealPlanFirestore firestorePlan) {
//        compositeDisposable.add(
//                repository.getRecipeDetails(firestorePlan.getMealId())
//                        .subscribeOn(Schedulers.io())
//                        .map(response -> {
//                            List<RecipeDetails> recipeDetails = response.getRecipeDetails();
//                            if (recipeDetails == null || recipeDetails.isEmpty()) {
//                                throw new Exception("No recipe details found");
//                            }
//                            return recipeDetails.get(0);
//                        })
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                details -> {
//                                    MealPlan mealPlan = new MealPlan(
//                                            details.getIdMeal(),
//                                            firestorePlan.getMealType(),
//                                            firestorePlan.getDayOfWeek(),
//                                            details.getMealName(),
//                                            details.getStrMealThumbnail(),
//                                            details.getMealCategory(),
//                                            details.getMealArea(),
//                                            details.getMealInstructions()
//                                    );
//                                    mealPlan.setTimestamp(firestorePlan.getTimestamp());
//
//                                    repository.insertMealToMealPlan(mealPlan);
//                                    Log.d(TAG, "Meal plan synced to Room: " + details.getMealName());
//                                },
//                                error -> {
//                                    Log.e(TAG, "Failed to fetch meal details", error);
//                                    if (error instanceof IOException) {
//                                        view.showError("Network error: " + error.getMessage());
//                                    } else if (error instanceof HttpException) {
//                                        HttpException httpException = (HttpException) error;
//                                        view.showError("Server error: " + error.getMessage());
//                                    } else {
//                                        view.showError(error.getMessage());
//                                    }
//
//                                }
//                        )
//        );
//    }

//    private void fetchAndSaveMealPlan(MealPlanFirestore firestorePlan) {
//        repository.getRecipeDetails(firestorePlan.getMealId(), new RecipeDetailsNetworkResponse() {
//            @Override
//            public void onSuccess(List<RecipeDetails> recipeDetailsList) {
//                if (!recipeDetailsList.isEmpty()) {
//                    RecipeDetails details = recipeDetailsList.get(0);
//
//                    MealPlan mealPlan = new MealPlan(
//                            details.getIdMeal(),
//                            firestorePlan.getMealType(),
//                            firestorePlan.getDayOfWeek(),
//                            details.getMealName(),
//                            details.getStrMealThumbnail(),
//                            details.getMealCategory(),
//                            details.getMealArea(),
//                            details.getMealInstructions()
//                    );
//                    mealPlan.setTimestamp(firestorePlan.getTimestamp());
//
//                    repository.insertMealToMealPlan(mealPlan);
//                    Log.d(TAG, "Meal plan synced to Room: " + details.getMealName());
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                Log.e(TAG, "Failed to fetch meal details: " + errorMessage);
//            }
//
//            @Override
//            public void onServerError(String errorMessage) {
//                Log.e(TAG, "Server error: " + errorMessage);
//            }
//        });
//    }
}