package com.example.foodplanner.data.model.category;

import com.google.gson.annotations.SerializedName;

public class MealsByCategory {
    @SerializedName("strMeal")
    String mealName;
    @SerializedName("idMeal")
    String mealId;
    @SerializedName("strMealThumb")
    String mealThumbnail;

    public MealsByCategory(String mealName, String mealId, String mealThumbnail) {
        this.mealName = mealName;
        this.mealId = mealId;
        this.mealThumbnail = mealThumbnail;
    }

    public String getMealName() {
        return mealName;
    }

    public String getMealId() {
        return mealId;
    }

    public String getMealThumbnail() {
        return mealThumbnail;
    }
}
