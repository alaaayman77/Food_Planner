package com.example.foodplanner.data;

import com.example.foodplanner.data.datasource.AreaFilteredMealsNetworkResponse;
import com.example.foodplanner.data.datasource.AreaNetworkResponse;
import com.example.foodplanner.data.datasource.CategoryNetworkResponse;
import com.example.foodplanner.data.datasource.IngredientFilteredMealsNetworkResponse;
import com.example.foodplanner.data.datasource.IngredientsNetworkResponse;
import com.example.foodplanner.data.datasource.MealNetworkResponse;
import com.example.foodplanner.data.datasource.MealsByCategoryNetworkResponse;
import com.example.foodplanner.data.datasource.MealsRemoteDataSource;
import com.example.foodplanner.data.datasource.RecipeDetailsNetworkResponse;

public class MealsRepository {

    private MealsRemoteDataSource mealsRemoteDataSource;
    public MealsRepository(){
        mealsRemoteDataSource = new MealsRemoteDataSource();
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
}

