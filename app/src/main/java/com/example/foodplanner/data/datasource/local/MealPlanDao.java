package com.example.foodplanner.data.datasource.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.foodplanner.data.model.meal_plan.MealPlan;

import java.util.List;
@Dao
public interface MealPlanDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertMealPlan(MealPlan mealPlan);

        @Query("SELECT * FROM meal_plan")
        LiveData<List<MealPlan>> getAllMealPlans();

        @Query("SELECT * FROM meal_plan WHERE dayOfWeek = :day")
        LiveData<List<MealPlan>> getMealPlansByDay(String day);

        @Query("DELETE FROM meal_plan")
        void deleteAllMealPlans();
        @Query("DELETE FROM meal_plan WHERE id = :mealPlanId")
        void deleteMealPlanById(int mealPlanId);
        }

