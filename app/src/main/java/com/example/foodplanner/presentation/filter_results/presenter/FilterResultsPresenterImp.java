package com.example.foodplanner.presentation.filter_results.presenter;

import android.content.Context;
import android.net.http.HttpException;
import android.util.Log;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.filter_results.view.FilterResultsView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class FilterResultsPresenterImp implements FilteredResultsPresenter {
    private static final String TAG = "FilterResultsPresenter";

    private FilterResultsView filterResultsView;
    private MealsRepository mealsRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<RecipeDetails> mealDetailsList = new ArrayList<>();

    public FilterResultsPresenterImp(FilterResultsView filterResultsView, Context context) {
        this.mealsRepository = new MealsRepository(context);
        this.filterResultsView = filterResultsView;
    }

    @Override
    public void getFilteredRecipes(String[] mealIds) {

        if (mealIds == null || mealIds.length == 0) {
            filterResultsView.showEmptyState();
            return;
        }

        filterResultsView.showLoading();
        mealDetailsList.clear();

        compositeDisposable.add(
                Observable.fromArray(mealIds)
                        .flatMap(mealId ->
                                mealsRepository.getRecipeDetails(mealId)
                                        .subscribeOn(Schedulers.io())
                                        .map(response -> {
                                            List<RecipeDetails> recipeDetails = response.getRecipeDetails();
                                            return (recipeDetails == null || recipeDetails.isEmpty())
                                                    ? null
                                                    : recipeDetails.get(0);
                                        })

                        )
                        .filter(meal -> meal != null)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                        //progressive loading
                                meal -> {
                                    mealDetailsList.add(meal);
                                    filterResultsView.displayMeals(new ArrayList<>(mealDetailsList));
                                },

                                error -> {
                                    filterResultsView.hideLoading();
                                    handleError(error);
                                },

                                () -> {
                                    filterResultsView.hideLoading();

                                    if (mealDetailsList.isEmpty()) {
                                        filterResultsView.showEmptyState();
                                    }
                                }
                        )
        );
    }


    private void handleError(Throwable error) {
        Log.e(TAG, "Error loading filtered recipes", error);

        if (error instanceof IOException) {
            filterResultsView.showError("Network error: " + error.getMessage());
        } else if (error instanceof HttpException) {
            filterResultsView.showError("Server error: " + error.getMessage());
        } else {
            filterResultsView.showError("Error loading recipes: " + error.getMessage());
        }
    }

    public void onDestroy() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}