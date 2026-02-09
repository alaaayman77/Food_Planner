package com.example.foodplanner.presentation.favorites;

import com.example.foodplanner.data.model.FavoriteMeal;

public interface FavPresenter {
    void loadFavorites();
    void removeFavorite(String mealId);
    void onFavoriteClick(FavoriteMeal favoriteMeal);
}
