package com.example.foodplanner.presentation.home.presenter;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.model.FavoriteMeal;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.category.CategoryResponse;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.data.model.search.area.Area;
import com.example.foodplanner.data.model.search.area.AreaResponse;
import com.example.foodplanner.utility.NetworkUtils;
import com.example.foodplanner.presentation.home.view.HomeView;

import com.example.foodplanner.utility.UserPrefManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

public class HomePresenterImp implements HomePresenter {

    private HomeView homeView;
    private MealsRepository mealsRepository;
    private FirebaseAuth mAuth;
    private LifecycleOwner owner;
    private Context context;
    private Set<String> favoriteMealIds = new HashSet<>();
    private UserPrefManager userPrefManager;
    private static final String TAG = "HomePresenterImp";
    private static final int TIMEOUT_SECONDS = 5; // Shorter timeout
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public HomePresenterImp(HomeView homeView, Context context, LifecycleOwner owner) {
        this.homeView = homeView;
        this.mealsRepository = new MealsRepository(context);
        this.mAuth = FirebaseAuth.getInstance();
        this.owner = owner;
        this.context = context;
        this.userPrefManager = new UserPrefManager(context);

        Log.d(TAG, "Presenter initialized");
        loadUserName();
        loadFavorites();

        // Check network status immediately
        boolean hasNetwork = NetworkUtils.isNetworkAvailable(context);
        Log.d(TAG, "Initial network check: " + hasNetwork);
        Log.d(TAG, "Network type: " + NetworkUtils.getNetworkType(context));

        if (!hasNetwork) {
            Log.d(TAG, "No network detected - showing offline banner immediately");
            homeView.hideLoading();
            homeView.showOfflineBanner();
        }
    }
    private void loadUserName() {
        if (mAuth.getCurrentUser() != null) {
            String displayName = mAuth.getCurrentUser().getDisplayName();
            String email = mAuth.getCurrentUser().getEmail();

            if (displayName != null && !displayName.isEmpty()) {
                homeView.displayUserName(displayName);
            } else if (email != null) {

                String emailPrefix = email.split("@")[0];
                homeView.displayUserName(emailPrefix);
            } else {
                homeView.displayUserName("Guest");
            }
        } else {
            homeView.displayUserName("Guest");
        }
    }
    private void loadFavorites() {
        mealsRepository.getAllFav().observe(owner, favorites -> {
            favoriteMealIds.clear();
            if (favorites != null) {
                for (FavoriteMeal favorite : favorites) {
                    favoriteMealIds.add(favorite.getMealId());
                }
            }
        });
    }

    private boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    @Override
    public void getRandomMeal() {
        Log.d(TAG, "getRandomMeal() called");

        // Check if we have a valid cached meal
        RandomMeal cachedMeal = userPrefManager.getCachedMealOfTheDay();
        if (cachedMeal != null) {
            Log.d(TAG, "✅ Using cached meal of the day: " + cachedMeal.getMealName());
            long timeRemaining = userPrefManager.getTimeUntilCacheExpires();
            Log.d(TAG, "Cache expires in: " + (timeRemaining / (1000 * 60 * 60)) + " hours");

            homeView.hideLoading();
            homeView.displayMeal(cachedMeal);
            checkIfMealIsFavorite(cachedMeal.getMealId());
            return;
        }

        // No valid cache - fetch from network
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.d(TAG, "No network available and no cache - aborting request");
            homeView.hideLoading();
            homeView.showOfflineBanner();
            return;
        }

        Log.d(TAG, "No valid cache - fetching new meal from API");
        homeView.showLoading();

        compositeDisposable.add(
                mealsRepository.getRandomMeal()
                        .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .map(response -> {
                            List<RandomMeal> meals = response.getRandomMeal();
                            if (meals == null || meals.isEmpty()) {
                                throw new Exception("No meal found");
                            }
                            return meals.get(0);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meal -> {
                                    Log.d(TAG, "Random meal loaded: " + meal.getMealName());

                                    // Cache the meal
                                    userPrefManager.saveMealOfTheDay(meal);
                                    Log.d(TAG, "Meal cached for 24 hours");

                                    homeView.hideLoading();
                                    homeView.displayMeal(meal);
                                    checkIfMealIsFavorite(meal.getMealId());
                                },
                                error -> {
                                    Log.e(TAG, "Error loading random meal: " + error.getClass().getSimpleName());
                                    homeView.hideLoading();

                                    if (isNetworkError(error)) {
                                        Log.d(TAG, "Network error - showing offline banner");
                                        homeView.showOfflineBanner();
                                    } else {
                                        Log.d(TAG, "Non-network error - showing error message");
                                        safeShowError("Unable to load meal");
                                    }
                                }
                        )
        );
    }


    @Override
    public void getCategory() {
        Log.d(TAG, "getCategory() called");

        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.d(TAG, "No network - skipping category load");
            return;
        }

        compositeDisposable.add(
                mealsRepository.getCategory()
                        .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .map(CategoryResponse::getCategories)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                categoriesList -> {
                                    Log.d(TAG, "Categories loaded: " + categoriesList.size());
                                    homeView.setCategoryList(categoriesList);
                                },
                                error -> {
                                    Log.e(TAG, "Error loading categories: " + error.getMessage());
                                    if (isNetworkError(error)) {
                                        homeView.showOfflineBanner();
                                    }
                                }
                        )
        );
    }

    @Override
    public void getArea() {
        Log.d(TAG, "getArea() called");

        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.d(TAG, "No network - skipping area load");
            return;
        }

        compositeDisposable.add(
                mealsRepository.getArea()
                        .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .map(AreaResponse::getAreasList)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                areaList -> {
                                    Log.d(TAG, "Areas loaded: " + areaList.size());
                                    homeView.setArea(areaList);
                                },
                                error -> {
                                    Log.e(TAG, "Error loading areas: " + error.getMessage());
                                    if (isNetworkError(error)) {
                                        homeView.showOfflineBanner();
                                    }
                                }
                        )
        );
    }

    @Override
    public void onCategoryClick(Category category) {
        homeView.OnCategoryClickSuccess(category);
    }

    @Override
    public void onAreaClick(Area area) {
        homeView.OnAreaClickSuccess(area);
    }

    @Override
    public void addMealToPlan(MealPlan mealPlan) {
        if (!isUserSignedIn()) {
            homeView.showSignInPrompt(
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
                                    homeView.onMealPlanAddedSuccess();

                                    if (NetworkUtils.isNetworkAvailable(context) && mAuth.getCurrentUser() != null) {
                                        saveMealPlanToFirestore(mealPlan);
                                    } else {
                                        Log.d(TAG, "Offline: Meal plan saved locally only");
                                    }
                                },
                                error -> {

                                    Log.e(TAG, " Error saving meal plan locally", error);
                                    homeView.onMealPlanAddedFailure("Failed to save meal plan: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void addToFav(FavoriteMeal favoriteMeal) {
        mealsRepository.addtoFav(favoriteMeal);
        homeView.onFavAddedSuccess();
    }

    @Override
    public void removeFromFav(String mealId) {
        mealsRepository.deleteFav(mealId);
        homeView.onFavRemovedSuccess();
    }

    @Override
    public void onFavoriteClick(RandomMeal meal) {
        if (!isUserSignedIn()) {
            homeView.showSignInPrompt(
                    "Save to Favorites",
                    "Sign in to save your favorite meals and sync them across devices!"
            );
            return;
        }

        boolean isFavorite = favoriteMealIds.contains(meal.getMealId());

        if (isFavorite) {
            removeFromFav(meal.getMealId());
        } else {
            if (NetworkUtils.isNetworkAvailable(context)) {
                fetchRecipeDetailsAndAddToFavorites(meal.getMealId());
            } else {
                homeView.showOfflineMessage("Unable to add to favorites while offline");
            }
        }
    }

    @Override
    public void checkIfMealIsFavorite(String mealId) {
        mealsRepository.getAllFav().observe(owner, favoriteMeals -> {
            if (favoriteMeals != null) {
                boolean isFavorite = false;
                for (FavoriteMeal fav : favoriteMeals) {
                    if (fav.getMealId().equals(mealId)) {
                        isFavorite = true;
                        break;
                    }
                }
                homeView.updateFavoriteIcon(isFavorite);
            }
        });
    }

    public void fetchRecipeDetailsAndAddToFavorites(String mealId) {
        compositeDisposable.add(
                mealsRepository.getRecipeDetails(mealId)
                        .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
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
                                    if (isNetworkError(error)) {
                                        homeView.showOfflineBanner();
                                        homeView.onFavAddedFailure("No internet connection");
                                    } else {
                                        homeView.onFavAddedFailure(error.getMessage());
                                    }
                                }
                        )
        );
    }

    private void saveMealPlanToFirestore(MealPlan mealPlan) {
        MealPlanFirestore firestorePlan = MealPlanFirestore.fromMealPlan(mealPlan);

        mealsRepository.saveMealPlanToFirestore(firestorePlan,
                new com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse() {
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

    public void retryConnection() {
        Log.d(TAG, "Retry button clicked");

        boolean hasNetwork = NetworkUtils.isNetworkAvailable(context);
        Log.d(TAG, "Network status: " + hasNetwork);
        Log.d(TAG, "Network type: " + NetworkUtils.getNetworkType(context));

        if (hasNetwork) {
            Log.d(TAG, "Connection restored - reloading content");
            homeView.hideOfflineBanner();
            homeView.showLoading();

            getRandomMeal();
            getCategory();
            getArea();
        } else {
            Log.d(TAG, " Still offline");
            homeView.showOfflineMessage("Still offline. Please check your connection.");
        }
    }

    /**
     * Check if error is network-related
     */
    private boolean isNetworkError(Throwable error) {
        boolean isTimeout = error instanceof java.util.concurrent.TimeoutException;
        boolean isSocketTimeout = error instanceof SocketTimeoutException;
        boolean isIOError = error instanceof IOException;
        boolean isUnknownHost = error instanceof UnknownHostException;
        boolean isServerError = error instanceof HttpException &&
                ((HttpException) error).code() >= 500;

        boolean result = isTimeout || isSocketTimeout || isIOError ||
                isUnknownHost || isServerError;

        Log.d(TAG, "Error type check - Timeout: " + isTimeout +
                ", SocketTimeout: " + isSocketTimeout +
                ", IO: " + isIOError +
                ", UnknownHost: " + isUnknownHost +
                ", ServerError: " + isServerError +
                " -> Network error: " + result);

        return result;
    }

    /**
     * Safely show error message
     */
    private void safeShowError(String message) {
        try {
            homeView.showError(message);
        } catch (Exception e) {
            Log.e(TAG, "Could not show error: " + e.getMessage());
        }
    }

    public void onDestroy() {
        Log.d(TAG, "Presenter destroyed - disposing subscriptions");
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}