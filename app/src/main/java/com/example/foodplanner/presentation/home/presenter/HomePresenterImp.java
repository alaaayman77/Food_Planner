package com.example.foodplanner.presentation.home.presenter;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.CategoryNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.datasource.remote.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.FavoriteMeal;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.home.view.HomeView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomePresenterImp implements HomePresenter {

    private HomeView homeView;
    private MealsRepository mealsRepository;
    private FirebaseAuth mAuth;
    private LifecycleOwner owner;
    private Set<String> favoriteMealIds = new HashSet<>();

    private static final String TAG = "HomePresenterImp";

    public HomePresenterImp(HomeView homeView, Context context, LifecycleOwner owner) {
        this.homeView = homeView;
        this.mealsRepository = new MealsRepository(context);
        this.mAuth = FirebaseAuth.getInstance();
        this.owner = owner;
        loadFavorites();
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
        homeView.showLoading();
        mealsRepository.getRandomMeal(new MealNetworkResponse() {
            @Override
            public void onSuccess(List<RandomMeal> randomMeals) {
                if (randomMeals != null && !randomMeals.isEmpty()) {
                    RandomMeal meal = randomMeals.get(0);
                    homeView.displayMeal(meal);
                    homeView.hideLoading();

                    checkIfMealIsFavorite(meal.getMealId());
                } else {
                    homeView.hideLoading();
                    homeView.showError("No meal found");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                homeView.hideLoading();
                homeView.showError(errorMessage);
            }

            @Override
            public void onServerError(String errorMessage) {
                homeView.hideLoading();
                homeView.showError(errorMessage);
            }
        });
    }

    @Override
    public void getCategory() {
        mealsRepository.getCategory(new CategoryNetworkResponse() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                homeView.setCategoryList(categoryList);
            }

            @Override
            public void onFailure(String errorMessage) {
                homeView.showError(errorMessage);
            }

            @Override
            public void onServerError(String errorMessage) {
                homeView.showError(errorMessage);
            }
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        homeView.OnCategoryClickSuccess(category);
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

        mealsRepository.insertMealToMealPlan(mealPlan);
        homeView.onMealPlanAddedSuccess();
        if (mAuth.getCurrentUser() != null) {
            saveMealPlanToFirestore(mealPlan);
        }

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
        // Check if user is signed in
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
            fetchRecipeDetailsAndAddToFavorites(meal.getMealId());
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
        mealsRepository.getRecipeDetails(mealId, new RecipeDetailsNetworkResponse() {
            @Override
            public void onSuccess(List<RecipeDetails> recipeDetailsList) {
                if (recipeDetailsList != null && !recipeDetailsList.isEmpty()) {
                    RecipeDetails recipe = recipeDetailsList.get(0);
                    FavoriteMeal favoriteMeal = FavoriteMeal.fromRecipeDetails(recipe);
                    addToFav(favoriteMeal);
                } else {
                    homeView.onFavAddedFailure("Could not fetch recipe details");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                homeView.onFavAddedFailure(errorMessage);
            }

            @Override
            public void onServerError(String errorMessage) {
                homeView.onFavAddedFailure(errorMessage);
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
                homeView.onMealPlanAddedFailure(error);
            }
        });
    }
}