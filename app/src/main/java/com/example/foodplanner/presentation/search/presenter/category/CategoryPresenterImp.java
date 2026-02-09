package com.example.foodplanner.presentation.search.presenter.category;

import android.content.Context;
import android.net.http.HttpException;

import com.airbnb.lottie.animation.content.Content;
import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.CategoryNetworkResponse;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.category.CategoryResponse;
import com.example.foodplanner.presentation.search.view.category.CategoryView;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoryPresenterImp implements CategoryPresenter{
    private CategoryView categoryView;
    private MealsRepository mealsRepository;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    public CategoryPresenterImp(CategoryView categoryView , Context context){
        this.categoryView = categoryView;
        mealsRepository = new MealsRepository(context);
    }

    @Override
    public void getCategory() {
        compositeDisposable.add(
                mealsRepository.getCategory()
                        .subscribeOn(Schedulers.io())
                        .map(CategoryResponse::getCategories)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item->{
                                    List<Category> categoriesList= item;
                                    categoryView.setCategory(categoriesList);
                                },
                                error -> {
                                    categoryView.hideLoading();


                                    if (error instanceof IOException) {
                                        categoryView.showError("Network error: " + error.getMessage());
                                    } else if (error instanceof HttpException) {
                                        HttpException httpException = (HttpException) error;
                                        categoryView.showError("Server error: " + error.getMessage());
                                    } else {
                                        categoryView.showError(error.getMessage());
                                    }
                                }
                        )
        );
    }
}
