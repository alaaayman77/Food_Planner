package com.example.foodplanner.data.model.category;

import java.util.List;

public class MealsByCategoryResponse {
    private List<MealsByCategory> meals;

    public MealsByCategoryResponse() {
    }

    public List<MealsByCategory> getMealsByCategories() {

        return meals;
    }
}
