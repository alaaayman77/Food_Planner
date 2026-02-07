package com.example.foodplanner.presentation.home.view;

import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.random_meals.RandomMeal;

import java.util.List;

public interface HomeView {
    public void setCategoryList(List<Category> categoryList);
    public void showError(String errorMessage);
    public void displayMeal(RandomMeal meal);
    public void OnCategoryClickSuccess(Category category);
    public void onMealPlanAddedSuccess();
    public void onMealPlanAddedFailure(String error);
}
