package com.example.foodplanner.presentation.search.presenter.ingredients;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.IngredientsNetworkResponse;
import com.example.foodplanner.data.model.search.ingredients.Ingredients;
import com.example.foodplanner.presentation.search.view.ingredients.IngredientsView;

import java.util.List;

public class IngredientsPresenterImp implements  IngredientsPresenter{
    private IngredientsView ingredientsView;
    private MealsRepository mealsRepository;

    public IngredientsPresenterImp(IngredientsView ingredientsView ){
        this.ingredientsView = ingredientsView;
        mealsRepository = new MealsRepository();
    }

    @Override
    public void getIngredients() {
        mealsRepository.getIngredients(new IngredientsNetworkResponse() {
            @Override
            public void onSuccess(List<Ingredients> ingredientsList) {
                if(!ingredientsList.isEmpty()){
                    ingredientsView.setIngredients(ingredientsList);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                ingredientsView.showError(errorMessage);
            }

            @Override
            public void onServerError(String errorMessage) {
                ingredientsView.showError(errorMessage);
            }
        });
    }
}
