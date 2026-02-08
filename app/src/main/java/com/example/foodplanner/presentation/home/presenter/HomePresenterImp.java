package com.example.foodplanner.presentation.home.presenter;

import android.content.Context;
import android.util.Log;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.CategoryNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.presentation.home.view.HomeView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class HomePresenterImp implements HomePresenter{
    HomeView homeView;

    private MealsRepository mealsRepository;
    private FirebaseAuth mAuth;
    private static final String TAG = "HomePresenterImp";

    public HomePresenterImp(HomeView homeView , Context context ) {
        this.mealsRepository = new MealsRepository(context);
        this.homeView = homeView;
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void getRandomMeal() {
        homeView.showLoading();
        mealsRepository.getRandomMeal(new MealNetworkResponse() {
            @Override
            public void onSuccess(List<RandomMeal> randomMealList) {
                RandomMeal randomMeal = randomMealList.get(0);
                if (randomMeal != null) {
                    homeView.displayMeal(randomMeal);
                    homeView.hideLoading();
                } else {

                    homeView.showError("No meal found");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                homeView.showError("Network error: ");
            }

            @Override
            public void onServerError(String errorMessage) {
                homeView.showError("Failed to load meal");

            }
        });
    }

    public void getCategory() {
        homeView.showLoading();
         mealsRepository.getCategory(new CategoryNetworkResponse() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                List<Category> categoriesFromApi = categoryList;
                if (categoriesFromApi != null) {
                    homeView.setCategoryList(categoriesFromApi);
                    homeView.hideLoading();
                }
                else{

                }
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
        mealsRepository.insertMealToMealPlan(mealPlan);
        homeView.onMealPlanAddedSuccess();
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
                homeView.onMealPlanAddedSuccess();
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
