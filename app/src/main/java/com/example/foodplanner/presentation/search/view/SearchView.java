package com.example.foodplanner.presentation.search.view;

import com.example.foodplanner.data.model.category.Category;

import java.util.List;

public interface SearchView {
    void setCategoryList(List<Category> categoryList);
    void showError(String errorMessage);
    void onCategorySelected(Category category);
    void updateSearchButtonText(String searchText);
}
