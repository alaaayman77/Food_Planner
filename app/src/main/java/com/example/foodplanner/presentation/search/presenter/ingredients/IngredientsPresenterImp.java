package com.example.foodplanner.presentation.search.presenter.ingredients;

import android.content.Context;
import android.net.http.HttpException;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.IngredientsNetworkResponse;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.category.CategoryResponse;
import com.example.foodplanner.data.model.search.ingredients.Ingredients;
import com.example.foodplanner.data.model.search.ingredients.IngredientsResponse;
import com.example.foodplanner.presentation.search.view.ingredients.IngredientsView;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class IngredientsPresenterImp implements  IngredientsPresenter{
    private IngredientsView ingredientsView;
    private MealsRepository mealsRepository;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    public IngredientsPresenterImp(IngredientsView ingredientsView, Context context ){
        this.ingredientsView = ingredientsView;
        mealsRepository = new MealsRepository(context);
    }


    @Override
    public void getIngredients() {
        compositeDisposable.add(
                mealsRepository.getIngredients()
                        .subscribeOn(Schedulers.io())
                        .map(IngredientsResponse::getIngredientsList)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item->{
                                    List<Ingredients> ingredientsList= item;
                                    ingredientsView.setIngredients(ingredientsList);
                                },
                                error -> {



                                    if (error instanceof IOException) {
                                        ingredientsView.showError("Network error: " + error.getMessage());
                                    } else if (error instanceof HttpException) {
                                        HttpException httpException = (HttpException) error;
                                        ingredientsView.showError("Server error: " + error.getMessage());
                                    } else {
                                        ingredientsView.showError(error.getMessage());
                                    }
                                }
                        )
        );
    }

//    @Override
//    public void getIngredients() {
//        mealsRepository.getIngredients(new IngredientsNetworkResponse() {
//            @Override
//            public void onSuccess(List<Ingredients> ingredientsList) {
//                if(!ingredientsList.isEmpty()){
//                    ingredientsView.setIngredients(ingredientsList);
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                ingredientsView.showError(errorMessage);
//            }
//
//            @Override
//            public void onServerError(String errorMessage) {
//                ingredientsView.showError(errorMessage);
//            }
//        });
//    }
}
