package com.example.foodplanner.presentation.search.presenter.area;

import android.content.Context;
import android.net.http.HttpException;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.AreaNetworkResponse;
import com.example.foodplanner.data.model.search.area.Area;
import com.example.foodplanner.data.model.search.area.AreaResponse;
import com.example.foodplanner.presentation.search.view.area.AreaView;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AreaPresenterImp implements AreaPresenter{
    private AreaView areaView;
    private MealsRepository mealsRepository;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    public AreaPresenterImp(AreaView areaView , Context context){
        this.areaView = areaView;
        mealsRepository = new MealsRepository(context);
    }

    @Override
    public void getArea() {
        compositeDisposable.add(
                mealsRepository.getArea()
                        .subscribeOn(Schedulers.io())
                        .map(AreaResponse::getAreasList)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item->{
                                    List<Area> areaList= item;
                                    areaView.setArea(areaList);
                                },
                                error -> {


                                    if (error instanceof IOException) {
                                        areaView.showError("Network error: " + error.getMessage());
                                    } else if (error instanceof HttpException) {
                                        HttpException httpException = (HttpException) error;
                                        areaView.showError("Server error: " + error.getMessage());
                                    } else {
                                        areaView.showError(error.getMessage());
                                    }
                                }
                        )
        );
    }
//    @Override
//    public void getArea() {
//        mealsRepository.getArea( new AreaNetworkResponse() {
//            @Override
//            public void onSuccess(List<Area> areaList) {
//                if(!areaList.isEmpty()){
//                    areaView.setArea(areaList);
//                }
//                else{
//                        areaView.showError("No Areas found for ");
//                    }
//
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//
//                areaView.showError(errorMessage);
//            }
//
//            @Override
//            public void onServerError(String errorMessage) {
//                    areaView.showError(errorMessage);
//            }
//        });
//
//    }

}
