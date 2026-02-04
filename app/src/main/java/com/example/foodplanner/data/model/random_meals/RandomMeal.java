package com.example.foodplanner.data.model.random_meals;

import com.google.gson.annotations.SerializedName;

public class RandomMeal  {
    @SerializedName("idMeal")
    String mealId;
    @SerializedName("strMeal")
    String mealName;
    @SerializedName("strCategory")
    String mealCategory;
    @SerializedName("strArea")
    String strArea;
    @SerializedName("strInstructions")
    String mealInstructions;
    @SerializedName("strMealThumb")
    String mealThumbnail;
    @SerializedName("strTags")
    String mealTags;

    public RandomMeal(String mealId, String mealName, String mealCategory, String strArea, String mealInstructions, String mealThumbnail, String mealTags) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealCategory = mealCategory;
        this.strArea = strArea;
        this.mealInstructions = mealInstructions;
        this.mealThumbnail = mealThumbnail;
        this.mealTags = mealTags;
    }

    public String getMealId() {
        return mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public String getMealCategory() {
        return mealCategory;
    }

    public String getStrArea() {
        return strArea;
    }

    public String getMealInstructions() {
        return mealInstructions;
    }

    public String getMealThumbnail() {
        return mealThumbnail;
    }

    public String getMealTags() {
        return mealTags;
    }
}
