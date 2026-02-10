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

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface MealPlanDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        Completable insertMealPlan(MealPlan mealPlan);

        @Query("SELECT * FROM meal_plan")
        LiveData<List<MealPlan>> getAllMealPlans();

        @Query("SELECT * FROM meal_plan WHERE dayOfWeek = :day")
        Observable<List<MealPlan>> getMealPlansByDay(String day);

//        @Query("DELETE FROM meal_plan")
//        void deleteAllMealPlans();
        @Query("DELETE FROM meal_plan WHERE id = :mealPlanId")
        Completable deleteMealPlanById(int mealPlanId);
        }

