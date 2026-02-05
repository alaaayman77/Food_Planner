package com.example.foodplanner.presentation.search.view.area;

import com.example.foodplanner.data.model.search.area.Area;

import java.util.List;

public interface AreaView {
    public void setArea(List<Area> areaList);
    public void showError(String errorMessage);
}
