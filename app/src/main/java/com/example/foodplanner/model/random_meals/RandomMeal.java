package com.example.foodplanner.model.random_meals;

public class RandomMeal {
    String idMeal;
    String strMeal;
    String strCategory;
    String strArea;
    String strInstructions;
    String strMealThumb;
    String strTags;

    public RandomMeal(String idMeal, String strMeal, String strCategory, String strArea, String strInstructions, String strMealThumb, String strTags) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strCategory = strCategory;
        this.strArea = strArea;
        this.strInstructions = strInstructions;
        this.strMealThumb = strMealThumb;
        this.strTags = strTags;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public String getStrMeal() {
        return strMeal;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public String getStrArea() {
        return strArea;
    }

    public String getStrInstructions() {
        return strInstructions;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public String getStrTags() {
        return strTags;
    }
}
