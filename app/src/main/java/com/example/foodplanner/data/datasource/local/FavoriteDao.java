package com.example.foodplanner.data.datasource.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.foodplanner.data.model.FavoriteMeal;
import com.example.foodplanner.data.model.meal_plan.MealPlan;

import java.util.List;
@Dao
public interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addToFav(FavoriteMeal favoriteMeal);


    @Query("DELETE FROM favorites WHERE mealId = :mealId")
    void deleteFavByMealId(String mealId);

    @Query("SELECT * FROM favorites")
    LiveData<List<FavoriteMeal>> getAllFavorites();


    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE mealId = :mealId)")
    boolean isMealFavorited(String mealId);


}
