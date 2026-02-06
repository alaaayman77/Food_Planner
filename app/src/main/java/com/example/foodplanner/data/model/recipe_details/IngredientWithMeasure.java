package com.example.foodplanner.data.model.recipe_details;

import java.io.Serializable;

public class IngredientWithMeasure  implements Serializable {
    private String name;
    private String measure;
    private String imageUrl;

    public IngredientWithMeasure(String name, String measure, String imageUrl) {
        this.name = name;
        this.measure = measure;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getName() { return name; }
    public String getMeasure() { return measure; }
    public String getImageUrl() { return imageUrl; }
}
