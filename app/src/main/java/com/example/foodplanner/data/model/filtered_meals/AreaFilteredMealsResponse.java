package com.example.foodplanner.data.model.filtered_meals;

import com.example.foodplanner.data.model.category.MealsByCategory;

import java.util.List;

public class AreaFilteredMealsResponse {
    private List<AreaFilteredMeals> meals;

    public AreaFilteredMealsResponse() {
    }

    public List<AreaFilteredMeals> getMealsFilteredByArea() {
        return meals;
    }
}
