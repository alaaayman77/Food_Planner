package com.example.foodplanner.presentation.meals_by_category.presenter;

import com.example.foodplanner.data.datasource.MealsByCategoryNetworkResponse;
import com.example.foodplanner.data.datasource.MealsRemoteDataSource;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.presentation.meals_by_category.view.MealsByCategoryView;

import java.util.List;

public class MealsByCategoryPresenterImp implements MealsByCategoryPresenter {
    private MealsByCategoryView mealsByCategoryView;

    private MealsRemoteDataSource mealsRemoteDataSource;

    public MealsByCategoryPresenterImp(MealsByCategoryView mealsByCategoryView ){
        this.mealsByCategoryView = mealsByCategoryView;
        mealsRemoteDataSource = new MealsRemoteDataSource();
    }
    public void getMealsByCategory(String category){
        mealsRemoteDataSource.getMealsByCategory(category,new MealsByCategoryNetworkResponse() {
            @Override
            public void onSuccess(List<MealsByCategory> mealsByCategoryList) {
                if(!mealsByCategoryList.isEmpty()){
                    mealsByCategoryView.setMealByCategoryList(mealsByCategoryList);
                }
                else{
                    mealsByCategoryView.showError("No meals found for " + category);
                }

            }

            @Override
            public void onFailure(String errorMessage) {

                mealsByCategoryView.showError(errorMessage);
            }

            @Override
            public void onServerError(String errorMessage) {
                mealsByCategoryView.showError(errorMessage);
            }
        });
    }

}
