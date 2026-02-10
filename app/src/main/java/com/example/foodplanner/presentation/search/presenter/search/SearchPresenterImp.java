package com.example.foodplanner.presentation.search.presenter.search;

import android.content.Context;
import android.util.Log;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.category.MealsByCategoryResponse;
import com.example.foodplanner.data.model.category.CategoryResponse;
import com.example.foodplanner.presentation.search.view.SearchView;
import com.example.foodplanner.utility.NetworkUtils;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

public class SearchPresenterImp implements SearchPresenter {
    private SearchView searchView;
    private MealsRepository mealsRepository;
    private Context context;

    private List<MealsByCategory> allMeals = new ArrayList<>();
    private static final int PAGE_SIZE = 20;
    private static final int TIMEOUT_SECONDS = 10;
    private int currentPage = 0;

    private static final String TAG = "SearchPresenterImp";
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SearchPresenterImp(SearchView searchView, Context context) {
        this.searchView = searchView;
        this.mealsRepository = new MealsRepository(context);
        this.context = context;
    }

    @Override
    public void searchMealsByName(String mealName) {
        if (mealName == null || mealName.trim().isEmpty()) {
            safeShowError("Please enter a meal name");
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.d(TAG, "No network - cannot search");
            safeShowError("No internet connection. Please check your network.");
            return;
        }

        // Reset for new search
        currentPage = 0;
        allMeals.clear();
        searchView.showSearchLoading();

        String searchQuery = mealName.trim().toLowerCase();
        Log.d(TAG, "Searching for: " + searchQuery);

        Disposable disposable = mealsRepository.getCategory()
                .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map(CategoryResponse::getCategories)
                .flatMapIterable(categories -> categories)
                .flatMap(category ->
                        mealsRepository.getMealsByCategory(category.getCategoryName())
                                .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                                .onErrorReturn(error -> {
                                    Log.e(TAG, "Error fetching category: " + category.getCategoryName(), error);
                                    return new MealsByCategoryResponse();
                                })
                )
                .flatMapIterable(response -> {
                    List<MealsByCategory> meals = response.getMealsByCategories();
                    return meals != null ? meals : new ArrayList<>();
                })
                .filter(meal ->
                        meal.getMealName() != null &&
                                meal.getMealName().toLowerCase().contains(searchQuery)
                )
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        filteredMeals -> {
                            Log.d(TAG, "Search complete - found " + filteredMeals.size() + " meals");
                            searchView.hideSearchLoading();
                            allMeals.addAll(filteredMeals);

                            if (filteredMeals.isEmpty()) {
                                safeShowError("No meals found matching '" + mealName + "'");
                            } else {
                                showCurrentPage();
                            }
                        },
                        error -> {
                            Log.e(TAG, "Search error: " + error.getClass().getSimpleName(), error);
                            searchView.hideSearchLoading();

                            if (isNetworkError(error)) {
                                safeShowError("Network error. Please check your connection.");
                            } else {
                                safeShowError("Error searching meals: " + error.getMessage());
                            }
                        }
                );

        compositeDisposable.add(disposable);
    }

    @Override
    public void loadMoreResults() {
        if (hasMoreResults()) {
            currentPage++;
            showCurrentPage();
        }
    }

    private void showCurrentPage() {
        int startIndex = currentPage * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, allMeals.size());

        List<MealsByCategory> pageResults = allMeals.subList(startIndex, endIndex);

        if (currentPage == 0) {
            searchView.showSearchResults(pageResults);
        } else {
            searchView.appendSearchResults(pageResults);
        }

        searchView.updateSearchInfo(endIndex, allMeals.size(), hasMoreResults());
    }

    private boolean hasMoreResults() {
        return (currentPage + 1) * PAGE_SIZE < allMeals.size();
    }
//check if its network related
    private boolean isNetworkError(Throwable error) {
        return error instanceof IOException ||
                error instanceof SocketTimeoutException ||
                error instanceof UnknownHostException ||
                error instanceof java.util.concurrent.TimeoutException ||
                (error instanceof HttpException &&
                        ((HttpException) error).code() >= 500);
    }


    private void safeShowError(String message) {
        try {
            searchView.showError(message);
        } catch (Exception e) {
            Log.e(TAG, "Could not show error: " + e.getMessage());
        }
    }

    public void onDestroy() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}