package com.example.foodplanner.data.datasource.local;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.foodplanner.data.db.AppDatabase;
import com.example.foodplanner.data.model.meal_plan.MealPlan;

import java.util.List;


public class MealPlanLocalDataSource {
    MealPlanDao mealPlanDao;
    public MealPlanLocalDataSource(Context context){
        this.mealPlanDao = AppDatabase.getInstance(context).MealPlanDao();
    }
    public void insertMealPlan(MealPlan mealPlan){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mealPlanDao.insertMealPlan(mealPlan);
            }
        }).start();
    }

    public LiveData<List<MealPlan>> getAllMealPlans(){
        return mealPlanDao.getAllMealPlans();
    }

    public LiveData<List<MealPlan>> getMealPlansByDay(String day){
        return mealPlanDao.getMealPlansByDay(day);
    }

    public  void deleteAllMealPlans(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mealPlanDao.deleteAllMealPlans();
            }
        }).start();
    }
    public void deleteMealPlanById(int mealPlanId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mealPlanDao.deleteMealPlanById(mealPlanId);
            }
        }).start();
    }
}
