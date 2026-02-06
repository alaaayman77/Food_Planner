package com.example.foodplanner.presentation.search.view.ingredients;

import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.search.ingredients.Ingredients;

import java.util.List;

public interface IngredientsView {
    public void setIngredients(List<Ingredients> ingredientsList);
    public void showError(String errorMessage);
}
