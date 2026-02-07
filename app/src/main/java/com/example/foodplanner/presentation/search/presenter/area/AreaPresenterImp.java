package com.example.foodplanner.presentation.search.presenter.area;

import android.content.Context;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.AreaNetworkResponse;
import com.example.foodplanner.data.model.search.area.Area;
import com.example.foodplanner.presentation.search.view.area.AreaView;

import java.util.List;

public class AreaPresenterImp implements AreaPresenter{
    private AreaView areaView;
    private MealsRepository mealsRepository;
    public AreaPresenterImp(AreaView areaView , Context context){
        this.areaView = areaView;
        mealsRepository = new MealsRepository(context);
    }
    @Override
    public void getArea() {
        mealsRepository.getArea( new AreaNetworkResponse() {
            @Override
            public void onSuccess(List<Area> areaList) {
                if(!areaList.isEmpty()){
                    areaView.setArea(areaList);
                }
                else{
                        areaView.showError("No Areas found for ");
                    }

            }

            @Override
            public void onFailure(String errorMessage) {

                areaView.showError(errorMessage);
            }

            @Override
            public void onServerError(String errorMessage) {
                    areaView.showError(errorMessage);
            }
        });

    }

}
