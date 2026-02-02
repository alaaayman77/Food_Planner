package com.example.foodplanner.services;

import com.example.foodplanner.model.category.CategoryResponse;
import com.example.foodplanner.model.category.MealsByCategoryResponse;
import com.example.foodplanner.model.random_meals.RandomMealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("random.php")
    Call<RandomMealResponse> getRandomMeal();

    @GET("categories.php")
    Call<CategoryResponse> getCategory();

    @GET("filter.php")
    Call<MealsByCategoryResponse> getMealsByCategory(@Query("c") String category);
}
