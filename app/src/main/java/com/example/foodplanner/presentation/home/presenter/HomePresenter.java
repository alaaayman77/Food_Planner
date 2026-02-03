package com.example.foodplanner.presentation.home.presenter;

import com.example.foodplanner.data.model.category.Category;

public interface HomePresenter {
    public void getRandomMeal();
    public void getCategory();
    public void onCategoryClick(Category category);
}
