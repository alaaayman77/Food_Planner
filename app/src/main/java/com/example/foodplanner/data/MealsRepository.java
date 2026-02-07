package com.example.foodplanner.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.foodplanner.data.datasource.local.MealPlanLocalDataSource;
import com.example.foodplanner.data.datasource.remote.AreaFilteredMealsNetworkResponse;
import com.example.foodplanner.data.datasource.remote.AreaNetworkResponse;
import com.example.foodplanner.data.datasource.remote.CategoryNetworkResponse;
import com.example.foodplanner.data.datasource.remote.IngredientFilteredMealsNetworkResponse;
import com.example.foodplanner.data.datasource.remote.IngredientsNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealsByCategoryNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealsRemoteDataSource;
import com.example.foodplanner.data.datasource.remote.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.meal_plan.MealPlan;

import java.util.List;

public class MealsRepository {

    private MealsRemoteDataSource mealsRemoteDataSource;
    private MealPlanLocalDataSource mealPlanLocalDataSource;
    public MealsRepository(Context context){

        mealsRemoteDataSource = new MealsRemoteDataSource();
        mealPlanLocalDataSource = new MealPlanLocalDataSource(context);
    }
    public void getRandomMeal(MealNetworkResponse response) {
        mealsRemoteDataSource.getRandomMeal(response);
    }

    public void getCategory(CategoryNetworkResponse response){
        mealsRemoteDataSource.getCategory(response);
    }
    public void getMealsByCategory( String category ,MealsByCategoryNetworkResponse response){
        mealsRemoteDataSource.getMealsByCategory(category ,response);
    }

    public void getArea( AreaNetworkResponse response){
        mealsRemoteDataSource.getArea(response);
    }

    public void getAreaFilteredMeals(String area, AreaFilteredMealsNetworkResponse response){
        mealsRemoteDataSource.getFilteredMealsByArea(area,response);
    }

    public void getIngredients(IngredientsNetworkResponse response){
        mealsRemoteDataSource.getIngredients(response);
    }

    public void getIngredientFilteredMeals(String ingredient, IngredientFilteredMealsNetworkResponse response){
        mealsRemoteDataSource.getFilteredMealsByIngredient(ingredient,response);
    }

    public void getRecipeDetails(String id , RecipeDetailsNetworkResponse response){
        mealsRemoteDataSource.getRecipeDetails(id , response);
    }

    public void insertMealToMealPlan(MealPlan mealPlan){
        mealPlanLocalDataSource.insertMealPlan(mealPlan);
    }


    public LiveData<List<MealPlan>> getAllMealPlans(){
        return mealPlanLocalDataSource.getAllMealPlans();
    }

    public LiveData<List<MealPlan>> getMealPlansByDay(String day){
        return  mealPlanLocalDataSource.getMealPlansByDay(day);
    }


    public void deleteAllMealPlans(){
        mealPlanLocalDataSource.deleteAllMealPlans();
    }
    public void deleteMealPlanById(int mealPlanId) {
        mealPlanLocalDataSource.deleteMealPlanById(mealPlanId);
    }
}

