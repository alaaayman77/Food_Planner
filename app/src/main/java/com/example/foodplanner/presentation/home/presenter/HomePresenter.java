package com.example.foodplanner.presentation.home.presenter;

import androidx.lifecycle.LifecycleOwner;

import com.example.foodplanner.data.model.FavoriteMeal;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.data.model.search.area.Area;

public interface HomePresenter {
    public void getRandomMeal();
    public void getCategory();
    void getArea();
    public void onCategoryClick(Category category);
    public void onAreaClick(Area area);

    public void addMealToPlan(MealPlan mealPlan);
    public void addToFav(FavoriteMeal favoriteMeal);
    void removeFromFav(String mealId);

    void onFavoriteClick(RandomMeal meal);
    void checkIfMealIsFavorite(String mealId);


}
