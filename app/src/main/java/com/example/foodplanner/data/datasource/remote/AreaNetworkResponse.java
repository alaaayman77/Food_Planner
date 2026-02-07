package com.example.foodplanner.data.datasource.remote;

import com.example.foodplanner.data.model.search.area.Area;

import java.util.List;

public interface AreaNetworkResponse {
    public void onSuccess(List<Area> areaList);

    public void onFailure(String errorMessage);

    public void onServerError(String errorMessage);
}
