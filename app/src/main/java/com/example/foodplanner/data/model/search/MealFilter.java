package com.example.foodplanner.data.model.search;

import java.io.Serializable;

public class MealFilter implements Serializable {
    String category;
    String ingredient;
    String country;
    public MealFilter(String category, String ingredient, String country) {
        this.category = category;
        this.ingredient = ingredient;
        this.country = country;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}
