package com.example.foodplanner.presentation.home.view;

import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.data.model.search.area.Area;

import java.util.List;

public interface HomeView {
    public void setCategoryList(List<Category> categoryList);
    public void showError(String errorMessage);
    public void displayMeal(RandomMeal meal);
    public void OnCategoryClickSuccess(Category category);
    public void onMealPlanAddedSuccess();
    public void showLoading();
    public void hideLoading();
    public void onMealPlanAddedFailure(String error);
    public void onFavAddedSuccess();
    public void onFavAddedFailure(String error);
    public void onFavRemovedSuccess();
    void updateFavoriteIcon(boolean isFavorite);
    void showSignInPrompt(String featureName, String message);
    void setArea(List<Area> areaList);
    void  OnAreaClickSuccess(Area area);
}
