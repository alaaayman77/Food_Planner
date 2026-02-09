package com.example.foodplanner.presentation.search.presenter.search;

public interface SearchPresenter {
    void searchMealsByName(String mealName);
    void loadMoreResults();
}