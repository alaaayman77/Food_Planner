package com.example.foodplanner.services;

import com.example.foodplanner.model.random_meals.RandomMealResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RandomMealService {
    @GET("random.php")
    Call<RandomMealResponse> getRandomMeal();
}
