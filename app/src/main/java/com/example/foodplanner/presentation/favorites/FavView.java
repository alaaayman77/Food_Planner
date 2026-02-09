package com.example.foodplanner.presentation.favorites;

import com.example.foodplanner.data.model.FavoriteMeal;

import java.util.List;

public interface FavView {
    void showFavorites(List<FavoriteMeal> favorites);
    void showEmptyState();
    void hideEmptyState();
    void showLoading();
    void hideLoading();
    void showError(String message);
    void updateFavoritesCount(int count);
    void onFavoriteRemoved(String message);
    void navigateToMealDetails(FavoriteMeal favoriteMeal);
}
