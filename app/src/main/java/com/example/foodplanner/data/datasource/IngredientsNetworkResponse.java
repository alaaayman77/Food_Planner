package com.example.foodplanner.data.datasource;

import com.example.foodplanner.data.model.search.area.Area;
import com.example.foodplanner.data.model.search.ingredients.Ingredients;

import java.util.List;

public interface IngredientsNetworkResponse {
    public void onSuccess(List<Ingredients> ingredientsList);

    public void onFailure(String errorMessage);

    public void onServerError(String errorMessage);
}
