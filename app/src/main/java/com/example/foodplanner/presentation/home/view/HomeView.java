package com.example.foodplanner.presentation.home.view;

import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.data.model.search.area.Area;

import java.util.List;

public interface HomeView {
    void setCategoryList(List<Category> categoryList);
    void showError(String errorMessage);
    void displayMeal(RandomMeal meal);
    void OnCategoryClickSuccess(Category category);
    void onMealPlanAddedSuccess();
    void showLoading();
    void hideLoading();
    void onMealPlanAddedFailure(String error);
    void onFavAddedSuccess();
    void onFavRemovedSuccess();
    void onFavAddedFailure(String error);
    void updateFavoriteIcon(boolean isFavorite);
    void showSignInPrompt(String featureName, String message);
    void setArea(List<Area> areaList);
    void OnAreaClickSuccess(Area area);

    // Offline mode methods
    void showOfflineBanner();
    void hideOfflineBanner();
    void showOfflineMessage(String message);
}