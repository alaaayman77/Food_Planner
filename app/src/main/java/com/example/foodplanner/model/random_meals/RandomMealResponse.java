package com.example.foodplanner.model.random_meals;



import java.util.List;

public class RandomMealResponse {
    private List<RandomMeal> meals;

    public RandomMealResponse() {
    }

    public List<RandomMeal> getMeals() {
        return meals;
    }


    public RandomMeal getRandomMeal() {
        if (meals != null && !meals.isEmpty()) {
            return meals.get(0);
        }
        return null;
    }
}