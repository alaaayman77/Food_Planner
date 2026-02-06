package com.example.foodplanner.data.model.search.ingredients;

import com.google.gson.annotations.SerializedName;

public class Ingredients {
    @SerializedName("idIngredient")
    private String idIngredient;
    @SerializedName("strIngredient")
    private String ingredientName;
    @SerializedName("strDescription")

    private String ingredientDesc;
    @SerializedName("strType")

    private String strType;
    @SerializedName("strThumb")
    private String  ingredientThumbnail;

    public Ingredients(String idIngredient, String ingredientName, String ingredientDesc, String strType, String ingredientThumbnail) {
        this.idIngredient = idIngredient;
        this.ingredientName = ingredientName;
        this.ingredientDesc = ingredientDesc;
        this.strType = strType;
        this.ingredientThumbnail = ingredientThumbnail;
    }

    public String getIdIngredient() {
        return idIngredient;
    }

    public String getIngredientDesc() {
        return ingredientDesc;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public String getStrType() {
        return strType;
    }

    public String getIngredientThumbnail() {
        return ingredientThumbnail;
    }
}
