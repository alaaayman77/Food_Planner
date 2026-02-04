package com.example.foodplanner.data.model.category;

import com.google.gson.annotations.SerializedName;

public class Category {
        @SerializedName("strCategory")
        private String categoryName;
        @SerializedName("idCategory")
        private String categoryId;
        @SerializedName("strCategoryThumb")
        private String categoryThumbnail;



        public Category(String categoryId, String categoryName, String categoryThumbnail) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.categoryThumbnail = categoryThumbnail;


        }

    public String getCategoryThumbnail() {
        return categoryThumbnail;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }
}

