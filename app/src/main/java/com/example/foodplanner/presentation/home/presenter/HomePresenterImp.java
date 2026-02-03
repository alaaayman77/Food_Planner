package com.example.foodplanner.presentation.home.presenter;

import com.example.foodplanner.data.datasource.CategoryNetworkResponse;
import com.example.foodplanner.data.datasource.MealNetworkResponse;
import com.example.foodplanner.data.datasource.MealsRemoteDataSource;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.presentation.home.view.HomeView;

import java.util.List;

public class HomePresenterImp implements HomePresenter{
    HomeView homeView;
    private MealsRemoteDataSource mealRemoteDataSource;

    public HomePresenterImp(HomeView homeView) {
        this.mealRemoteDataSource = new MealsRemoteDataSource();
        this.homeView = homeView;
    }

    public void getRandomMeal() {
        mealRemoteDataSource.getRandomMeal(new MealNetworkResponse() {
            @Override
            public void onSuccess(List<RandomMeal> randomMealList) {
                RandomMeal randomMeal = randomMealList.get(0);
                if (randomMeal != null) {
                    homeView.displayMeal(randomMeal);
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
        mealRemoteDataSource.getCategory(new CategoryNetworkResponse() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                List<Category> categoriesFromApi = categoryList;
                if (categoriesFromApi != null) {
                    homeView.setCategoryList(categoriesFromApi);
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


}
