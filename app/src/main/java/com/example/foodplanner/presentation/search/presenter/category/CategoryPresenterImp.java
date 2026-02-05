package com.example.foodplanner.presentation.search.presenter.category;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.CategoryNetworkResponse;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.presentation.search.view.category.CategoryView;

import java.util.List;

public class CategoryPresenterImp implements CategoryPresenter{
    private CategoryView categoryView;
    private MealsRepository mealsRepository;

    public CategoryPresenterImp(CategoryView categoryView ){
        this.categoryView = categoryView;
        mealsRepository = new MealsRepository();
    }


    @Override
    public void getCategory() {
        mealsRepository.getCategory(new CategoryNetworkResponse() {
            @Override
            public void onSuccess(List<Category> categoryList) {
                if(!categoryList.isEmpty()){
                    categoryView.setCategory(categoryList);
                }
                else{
                    categoryView.showError("No Areas found ");
                }

            }

            @Override
            public void onFailure(String errorMessage) {
                categoryView.showError(errorMessage);
            }

            @Override
            public void onServerError(String errorMessage) {
                categoryView.showError(errorMessage);
            }
        });
    }
}
