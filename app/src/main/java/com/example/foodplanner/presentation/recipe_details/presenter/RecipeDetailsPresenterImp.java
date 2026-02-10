package com.example.foodplanner.presentation.recipe_details.presenter;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.model.FavoriteMeal;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.recipe_details.view.RecipeDetailsView;

import com.example.foodplanner.utility.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

public class RecipeDetailsPresenterImp implements RecipeDetailsPresenter {
    private RecipeDetailsView recipeDetailsView;
    private MealsRepository mealsRepository;
    private FirebaseAuth mAuth;
    private Context context;
    private LifecycleOwner lifecycleOwner;

    private static final String TAG = "RecipeDetailsPresenter";
    private static final int TIMEOUT_SECONDS = 5;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public RecipeDetailsPresenterImp(RecipeDetailsView recipeDetailsView, Context context, LifecycleOwner lifecycleOwner) {
        this.mealsRepository = new MealsRepository(context);
        this.recipeDetailsView = recipeDetailsView;
        this.mAuth = FirebaseAuth.getInstance();
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
    }

    private boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    @Override
    public void getRecipeDetails(String id) {
        Log.d(TAG, "Getting recipe details for ID: " + id);

        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.d(TAG, "No network - attempting to load from cache");
            recipeDetailsView.showLoading();
            loadFromCache(id);
            return;
        }

        // Network available - try to fetch from API
        Log.d(TAG, "Network available - fetching from API");
        recipeDetailsView.showLoading();

        compositeDisposable.add(
                mealsRepository.getRecipeDetails(id)
                        .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
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
                                mealDetails -> {
                                    Log.d(TAG, "Recipe details loaded from API");
                                    recipeDetailsView.hideLoading();
                                    recipeDetailsView.setRecipeDetails(mealDetails);
                                },
                                error -> {
                                    Log.e(TAG, "Error loading from API: " + error.getClass().getSimpleName());
                                    recipeDetailsView.hideLoading();

                                    if (isNetworkError(error)) {
                                        Log.d(TAG, "Network error - trying cache");
                                        loadFromCache(id);
                                    } else {
                                        safeShowError("Unable to load recipe details");
                                    }
                                }
                        )
        );
    }


    private void loadFromCache(String mealId) {
        Log.d(TAG, "Searching cache for meal ID: " + mealId);

        mealsRepository.getAllMealPlans().observe(lifecycleOwner, mealPlans -> {
            if (mealPlans != null && !mealPlans.isEmpty()) {
                for (MealPlan mealPlan : mealPlans) {
                    if (mealPlan.getMealId().equals(mealId)) {
                        Log.d(TAG, "✅ Found in meal plans cache");
                        RecipeDetails cachedDetails = convertMealPlanToRecipeDetails(mealPlan);
                        recipeDetailsView.hideLoading();
                        recipeDetailsView.setRecipeDetails(cachedDetails);
                        recipeDetailsView.showCachedDataNotice();
                        return;
                    }
                }
            }

            // If not found in meal plans, try favorites
            Log.d(TAG, "Not found in meal plans, trying favorites");
            tryLoadFromFavorites(mealId);
        });
    }

    /**
     * Try to load from favorites if not found in meal plans
     */
    private void tryLoadFromFavorites(String mealId) {
        mealsRepository.getAllFav().observe(lifecycleOwner, favorites -> {
            if (favorites != null && !favorites.isEmpty()) {
                for (FavoriteMeal favorite : favorites) {
                    if (favorite.getMealId().equals(mealId)) {
                        Log.d(TAG, "✅ Found in favorites cache");
                        RecipeDetails cachedDetails = convertFavoriteToRecipeDetails(favorite);
                        recipeDetailsView.hideLoading();
                        recipeDetailsView.setRecipeDetails(cachedDetails);
                        recipeDetailsView.showCachedDataNotice();
                        return;
                    }
                }
            }

            // Not found anywhere
            Log.d(TAG, "❌ Not found in any cache");
            recipeDetailsView.hideLoading();
            recipeDetailsView.showOfflineNoCache();
        });
    }

    /**
     * Convert MealPlan to RecipeDetails for display
     */
    private RecipeDetails convertMealPlanToRecipeDetails(MealPlan mealPlan) {
        RecipeDetails details = new RecipeDetails();
        details.setIdMeal(mealPlan.getMealId());
        details.setMealName(mealPlan.getMealName());
        details.setStrMealThumbnail(mealPlan.getMealThumbnail());
        details.setMealCategory(mealPlan.getMealCategory());
        details.setMealArea(mealPlan.getMealArea());
        details.setMealInstructions(mealPlan.getMealInstructions());

        // Note: Meal plans don't have ingredients or YouTube URL
        // These will be null/empty

        return details;
    }

    /**
     * Convert FavoriteMeal to RecipeDetails for display
     */
    private RecipeDetails convertFavoriteToRecipeDetails(FavoriteMeal favorite) {
        RecipeDetails details = new RecipeDetails();
        details.setIdMeal(favorite.getMealId());
        details.setMealName(favorite.getMealName());
        details.setStrMealThumbnail(favorite.getMealThumbnail());
        details.setMealCategory(favorite.getMealCategory());
        details.setMealArea(favorite.getMealArea());
        details.setMealInstructions(favorite.getMealInstructions());
        details.setStrYoutube(favorite.getMealYoutube());

        // Favorites have ingredients!
        details.setIngredientsWithMeasures(favorite.getIngredientsWithMeasures());

        return details;
    }

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
                                    Log.d(TAG, "Meal plan saved to local database");
                                    recipeDetailsView.onMealPlanAddedSuccess();

                                    if (NetworkUtils.isNetworkAvailable(context) && mAuth.getCurrentUser() != null) {
                                        saveMealPlanToFirestore(mealPlan);
                                    } else {
                                        Log.d(TAG, "Offline - meal plan will sync when connection restored");
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error saving meal plan locally", error);
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
            }
        });
    }

    /**
     * Check if error is network-related
     */
    private boolean isNetworkError(Throwable error) {
        return error instanceof IOException ||
                error instanceof SocketTimeoutException ||
                error instanceof UnknownHostException ||
                error instanceof java.util.concurrent.TimeoutException ||
                (error instanceof HttpException &&
                        ((HttpException) error).code() >= 500);
    }

    /**
     * Safely show error message
     */
    private void safeShowError(String message) {
        try {
            recipeDetailsView.showError(message);
        } catch (Exception e) {
            Log.e(TAG, "Could not show error: " + e.getMessage());
        }
    }

    public void onDestroy() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}