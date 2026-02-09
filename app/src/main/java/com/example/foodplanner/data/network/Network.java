package com.example.foodplanner.data.network;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    public static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    static Retrofit retrofit;
    private static Network instance;
    private MealsService mealsService;

    private Network() {
    }

    public static Network getInstance() {
        if (instance == null) {
            instance = new Network();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }
        return instance;
    }
    public MealsService getMealsService(){

        mealsService  = retrofit.create(MealsService.class);
        return mealsService;
    }
}
