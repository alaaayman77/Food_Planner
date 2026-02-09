package com.example.foodplanner.presentation.search.presenter.search;

import android.content.Context;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.category.MealsByCategoryResponse;
import com.example.foodplanner.data.model.category.CategoryResponse;
import com.example.foodplanner.presentation.search.view.SearchView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenterImp implements SearchPresenter {
    private SearchView searchView;
    private MealsRepository mealsRepository;

    private List<MealsByCategory> allMeals = new ArrayList<>();
    private static final int PAGE_SIZE = 20;
    private int currentPage = 0;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SearchPresenterImp(SearchView searchView, Context context) {
        this.searchView = searchView;
        this.mealsRepository = new MealsRepository(context);
    }

    @Override
    public void searchMealsByName(String mealName) {
        if (mealName == null || mealName.trim().isEmpty()) {
            searchView.showError("Please enter a meal name");
            return;
        }

        // Reset for new search
        currentPage = 0;
        allMeals.clear();
        searchView.showSearchLoading();

        String searchQuery = mealName.trim().toLowerCase();

        Disposable disposable = mealsRepository.getCategory()
                .subscribeOn(Schedulers.io())
                .map(CategoryResponse::getCategories)
                .flatMapIterable(categories -> categories)
                .flatMap(category ->
                        mealsRepository.getMealsByCategory(category.getCategoryName())
                                .onErrorReturn(error -> new MealsByCategoryResponse())
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
                            searchView.hideSearchLoading();
                            allMeals.addAll(filteredMeals);

                            if (filteredMeals.isEmpty()) {
                                searchView.showError("No meals found matching '" + mealName + "'");
                            } else {
                                // Show first page
                                showCurrentPage();
                            }
                        },
                        error -> {
                            searchView.hideSearchLoading();
                            searchView.showError(error.getMessage());
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
}