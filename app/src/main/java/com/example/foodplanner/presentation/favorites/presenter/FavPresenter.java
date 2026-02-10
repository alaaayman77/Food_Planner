package com.example.foodplanner.presentation.favorites.presenter;

import com.example.foodplanner.data.model.favoriteMeal.FavoriteMeal;

public interface FavPresenter {
    void loadFavorites();
    void removeFavorite(String mealId);
    void onFavoriteClick(FavoriteMeal favoriteMeal);
}
