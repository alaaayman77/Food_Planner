package com.example.foodplanner.data.model.filtered_meals;

import com.google.gson.annotations.SerializedName;

public class AreaFilteredMeals {
    @SerializedName("strMeal")
    String mealName;
    @SerializedName("idMeal")

    String idMeal;
    @SerializedName("strMealThumb")
    String strMealThumb;

    public AreaFilteredMeals(String mealName, String idMeal, String strMealThumb) {
        this.mealName = mealName;
        this.idMeal = idMeal;
        this.strMealThumb = strMealThumb;
    }

    public String getMealName() {
        return mealName;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }
}
