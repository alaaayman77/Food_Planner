package com.example.foodplanner.data;

import com.example.foodplanner.data.datasource.CategoryNetworkResponse;
import com.example.foodplanner.data.datasource.MealNetworkResponse;
import com.example.foodplanner.data.datasource.MealsByCategoryNetworkResponse;
import com.example.foodplanner.data.datasource.MealsRemoteDataSource;

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
}

