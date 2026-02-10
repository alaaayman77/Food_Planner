package com.example.foodplanner.data.datasource.local;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.foodplanner.data.db.AppDatabase;
import com.example.foodplanner.data.model.FavoriteMeal;
import com.example.foodplanner.data.model.meal_plan.MealPlan;

import java.util.List;
import java.util.function.Consumer;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;


public class MealLocalDataSource {
    MealPlanDao mealPlanDao;
    FavoriteDao favoriteDao;
    public MealLocalDataSource(Context context){
        this.mealPlanDao = AppDatabase.getInstance(context).MealPlanDao();
        this.favoriteDao = AppDatabase.getInstance(context).FavoriteDao();
    }
    public Completable insertMealPlan(MealPlan mealPlan){
        return  mealPlanDao.insertMealPlan(mealPlan);
    }
//    public void insertMealPlan(MealPlan mealPlan){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mealPlanDao.insertMealPlan(mealPlan);
//            }
//        }).start();
//    }

    public LiveData<List<MealPlan>> getAllMealPlans(){
        return mealPlanDao.getAllMealPlans();
    }

    public Observable<List<MealPlan>> getMealPlansByDay(String day){
        return mealPlanDao.getMealPlansByDay(day);
    }
    public Completable deleteMealPlanById(int mealPlanId){
        return mealPlanDao.deleteMealPlanById(mealPlanId);
    }

//    public  void deleteAllMealPlans(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mealPlanDao.deleteAllMealPlans();
//            }
//        }).start();
//    }
//    public void deleteMealPlanById(int mealPlanId) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mealPlanDao.deleteMealPlanById(mealPlanId);
//            }
//        }).start();
//    }

    public void addToFav(FavoriteMeal favoriteMeal){
        new Thread(new Runnable() {
            @Override
            public void run() {
                favoriteDao.addToFav(favoriteMeal);
            }
        }).start();
    }

    public void deleteFavByMealId(String mealId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                favoriteDao.deleteFavByMealId(mealId);
            }
        }).start();
    }

    public LiveData<List<FavoriteMeal>> getAllFavorites(){
        return favoriteDao.getAllFavorites();
    }
    public void isFavorite(String mealId, Consumer<Boolean> callback){
        new Thread(() -> {
            boolean result = favoriteDao.isMealFavorited(mealId);
            callback.accept(result);
        }).start();
    }
}
