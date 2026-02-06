package com.example.foodplanner.data.model.search.ingredients;

import com.example.foodplanner.data.model.search.area.Area;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IngredientsResponse {
    @SerializedName("meals")
    List<Ingredients> ingredientsList;
    public IngredientsResponse() {
    }

    public List<Ingredients> getIngredientsList() {
        return ingredientsList;
    }
}
