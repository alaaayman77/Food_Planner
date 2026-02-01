package com.example.foodplanner.model.category;

public class Category {
        private String strCategory;
        private String idCategory;
        private String strCategoryThumb;



        public Category(String idCategory , String strCategory, String strCategoryThumb) {
            this.idCategory = idCategory;
            this.strCategory = strCategory;
            this.strCategoryThumb = strCategoryThumb;


        }

    public String getStrCategoryThumb() {
        return strCategoryThumb;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public String getIdCategory() {
        return idCategory;
    }
}

