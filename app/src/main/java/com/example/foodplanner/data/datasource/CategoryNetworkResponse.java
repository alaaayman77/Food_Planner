package com.example.foodplanner.data.datasource;

import com.example.foodplanner.data.model.category.Category;

import java.util.List;

public interface CategoryNetworkResponse {
    public void onSuccess(List<Category> categoryList);

    public void onFailure(String errorMessage);

    public void onServerError(String errorMessage);
}
