package com.example.foodplanner.data.network;

import com.example.foodplanner.data.model.category.CategoryResponse;
import com.example.foodplanner.data.model.category.MealsByCategoryResponse;
import com.example.foodplanner.data.model.random_meals.RandomMealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealsService {
    @GET("random.php")
    Call<RandomMealResponse> getRandomMeal();

    @GET("categories.php")
    Call<CategoryResponse> getCategory();

    @GET("filter.php")
    Call<MealsByCategoryResponse> getMealsByCategory(@Query("c") String category);
}
