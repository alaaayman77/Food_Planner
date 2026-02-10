package com.example.foodplanner.presentation.home.presenter;

import com.example.foodplanner.data.model.favoriteMeal.FavoriteMeal;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.data.model.search.area.Area;

public interface HomePresenter {
    void getRandomMeal();
    void getCategory();
    void getArea();
    void onCategoryClick(Category category);
    void onAreaClick(Area area);
    void addMealToPlan(MealPlan mealPlan);
    void addToFav(FavoriteMeal favoriteMeal);
    void removeFromFav(String mealId);
    void onFavoriteClick(RandomMeal meal);
    void checkIfMealIsFavorite(String mealId);
    void retryConnection();
    void onDestroy();



}
