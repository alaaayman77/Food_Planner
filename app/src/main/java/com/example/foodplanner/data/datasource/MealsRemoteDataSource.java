package com.example.foodplanner.data.datasource;

import android.util.Log;

import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.category.CategoryResponse;
import com.example.foodplanner.data.model.random_meals.RandomMealResponse;
import com.example.foodplanner.data.network.MealsService;
import com.example.foodplanner.data.network.Network;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealsRemoteDataSource {
    MealsService mealsService;

    public MealsRemoteDataSource(){
        mealsService = Network.getInstance().getMealsService();
    }

    public void getRandomMeal(MealNetworkResponse callback){
        mealsService.getRandomMeal().enqueue(new Callback<RandomMealResponse>() {

            @Override
            public void onResponse(Call<RandomMealResponse> call, Response<RandomMealResponse> response) {
                if(response.isSuccessful() &&response.body()!=null){
                    callback.onSuccess( response.body().getRandomMeal());




                }
                else {
                    callback.onServerError("Error code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RandomMealResponse> call, Throwable t) {
                if(t instanceof IOException){
                    callback.onFailure("Network error");
                }
                else{
                    callback.onFailure("Conversion Error");
                }
            }
        });
    }

    public void getCategory(CategoryNetworkResponse callback){
        mealsService.getCategory().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    callback.onSuccess(response.body().getCategories());

                } else {
                    callback.onServerError("Error code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                if(t instanceof IOException){
                    callback.onFailure("Network error");
                }
                else{
                    callback.onFailure("Conversion Error");
                }
            }
        });

    }
}
