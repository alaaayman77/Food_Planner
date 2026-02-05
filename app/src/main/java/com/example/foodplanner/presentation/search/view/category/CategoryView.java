package com.example.foodplanner.presentation.search.view.category;

import com.example.foodplanner.data.model.category.Category;

import java.util.List;

public interface CategoryView {
    public void setCategory(List<Category> categoryList);
    public void showError(String errorMessage);
}
