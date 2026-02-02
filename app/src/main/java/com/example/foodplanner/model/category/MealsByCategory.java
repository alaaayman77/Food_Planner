package com.example.foodplanner.model.category;

public class MealsByCategory {
    String strMeal;
    String idMeal;
    String strMealThumb;

    public MealsByCategory(String strMeal, String idMeal, String strMealThumb) {
        this.strMeal = strMeal;
        this.idMeal = idMeal;
        this.strMealThumb = strMealThumb;
    }

    public String getStrMeal() {
        return strMeal;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }
}
