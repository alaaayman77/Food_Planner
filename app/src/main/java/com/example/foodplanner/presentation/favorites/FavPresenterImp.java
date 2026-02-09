package com.example.foodplanner.presentation.favorites;


import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.model.FavoriteMeal;

import java.util.List;

public class FavPresenterImp implements FavPresenter {

    private FavView favoritesView;
    private MealsRepository mealsRepository;
    private LifecycleOwner lifecycleOwner;

    public FavPresenterImp(FavView favoritesView, Context context, LifecycleOwner lifecycleOwner) {
        this.favoritesView = favoritesView;
        this.mealsRepository = new MealsRepository(context);
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public void loadFavorites() {
        favoritesView.showLoading();


        mealsRepository.getAllFav().observe(lifecycleOwner, new Observer<List<FavoriteMeal>>() {
            @Override
            public void onChanged(List<FavoriteMeal> favoriteMeals) {
                favoritesView.hideLoading();

                if (favoriteMeals != null && !favoriteMeals.isEmpty()) {
                    favoritesView.hideEmptyState();
                    favoritesView.showFavorites(favoriteMeals);
                    favoritesView.updateFavoritesCount(favoriteMeals.size());
                } else {
                    favoritesView.showEmptyState();
                    favoritesView.updateFavoritesCount(0);
                }
            }
        });
    }

    @Override
    public void removeFavorite(String mealId) {
        mealsRepository.deleteFav(mealId);
        favoritesView.onFavoriteRemoved("Removed from favorites");
    }

    @Override
    public void onFavoriteClick(FavoriteMeal favoriteMeal) {
        favoritesView.navigateToMealDetails(favoriteMeal);
    }
}