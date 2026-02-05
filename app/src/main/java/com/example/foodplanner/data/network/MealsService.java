package com.example.foodplanner.data.network;

import com.example.foodplanner.data.model.category.CategoryResponse;
import com.example.foodplanner.data.model.category.MealsByCategoryResponse;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMeals;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMealsResponse;
import com.example.foodplanner.data.model.random_meals.RandomMealResponse;
import com.example.foodplanner.data.model.search.area.AreaResponse;

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

    @GET("filter.php")
    Call<AreaFilteredMealsResponse> getFilteredMealsByArea(@Query("a") String area);

    @GET("list.php?a=list")
    Call<AreaResponse> getArea();
}
