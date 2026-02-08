package com.example.foodplanner.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.foodplanner.data.datasource.local.FavoriteDao;
import com.example.foodplanner.data.datasource.local.MealPlanDao;
import com.example.foodplanner.data.model.FavoriteMeal;
import com.example.foodplanner.data.model.meal_plan.MealPlan;

@Database(entities = {MealPlan.class , FavoriteMeal.class} , version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MealPlanDao MealPlanDao();
    public abstract FavoriteDao FavoriteDao();
    private static AppDatabase INSTANCE;
    public static AppDatabase getInstance(Context context){
        if(INSTANCE == null){
            AppDatabase db = Room.databaseBuilder(context,AppDatabase.class ,"meal_plan").build();
            INSTANCE = db;
        }
        return INSTANCE;
    }
}
