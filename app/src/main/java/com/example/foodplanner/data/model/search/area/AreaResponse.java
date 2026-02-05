package com.example.foodplanner.data.model.search.area;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AreaResponse {
    @SerializedName("meals")
    List<Area> areaList;
    public AreaResponse() {
    }

    public List<Area> getMealsArea() {
        return areaList;
    }
}
