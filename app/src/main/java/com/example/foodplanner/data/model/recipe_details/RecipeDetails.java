package com.example.foodplanner.data.model.recipe_details;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetails {
    @SerializedName("idMeal")
    private String idMeal;

    @SerializedName("strMeal")
    private String mealName;


    @SerializedName("strCategory")
    private String mealCategory;

    @SerializedName("strArea")
    private String mealArea;

    @SerializedName("strInstructions")
    private String mealInstructions;

    @SerializedName("strMealThumb")
    private String strMealThumbnail;

    @SerializedName("strTags")
    private String strTags;

    @SerializedName("strYoutube")
    private String strYoutube;

    @SerializedName("strIngredient1")
    private String strIngredient1;

    @SerializedName("strIngredient2")
    private String strIngredient2;

    @SerializedName("strIngredient3")
    private String strIngredient3;

    @SerializedName("strIngredient4")
    private String strIngredient4;

    @SerializedName("strIngredient5")
    private String strIngredient5;

    @SerializedName("strIngredient6")
    private String strIngredient6;

    @SerializedName("strIngredient7")
    private String strIngredient7;

    @SerializedName("strIngredient8")
    private String strIngredient8;

    @SerializedName("strIngredient9")
    private String strIngredient9;

    @SerializedName("strIngredient10")
    private String strIngredient10;

    @SerializedName("strIngredient11")
    private String strIngredient11;

    @SerializedName("strIngredient12")
    private String strIngredient12;

    @SerializedName("strIngredient13")
    private String strIngredient13;

    @SerializedName("strIngredient14")
    private String strIngredient14;

    @SerializedName("strIngredient15")
    private String strIngredient15;

    @SerializedName("strIngredient16")
    private String strIngredient16;

    @SerializedName("strIngredient17")
    private String strIngredient17;

    @SerializedName("strIngredient18")
    private String strIngredient18;

    @SerializedName("strIngredient19")
    private String strIngredient19;

    @SerializedName("strIngredient20")
    private String strIngredient20;

    // Measures (1-20)
    @SerializedName("strMeasure1")
    private String strMeasure1;

    @SerializedName("strMeasure2")
    private String strMeasure2;

    @SerializedName("strMeasure3")
    private String strMeasure3;

    @SerializedName("strMeasure4")
    private String strMeasure4;

    @SerializedName("strMeasure5")
    private String strMeasure5;

    @SerializedName("strMeasure6")
    private String strMeasure6;

    @SerializedName("strMeasure7")
    private String strMeasure7;

    @SerializedName("strMeasure8")
    private String strMeasure8;

    @SerializedName("strMeasure9")
    private String strMeasure9;

    @SerializedName("strMeasure10")
    private String strMeasure10;

    @SerializedName("strMeasure11")
    private String strMeasure11;

    @SerializedName("strMeasure12")
    private String strMeasure12;

    @SerializedName("strMeasure13")
    private String strMeasure13;

    @SerializedName("strMeasure14")
    private String strMeasure14;

    @SerializedName("strMeasure15")
    private String strMeasure15;

    @SerializedName("strMeasure16")
    private String strMeasure16;

    @SerializedName("strMeasure17")
    private String strMeasure17;

    @SerializedName("strMeasure18")
    private String strMeasure18;

    @SerializedName("strMeasure19")
    private String strMeasure19;

    @SerializedName("strMeasure20")
    private String strMeasure20;

    @SerializedName("strSource")
    private String strSource;
    @SerializedName("strImageSource")
    private String strImageSource;

    public RecipeDetails(String idMeal, String mealName, String mealCategory, String mealArea, String strMealThumbnail, String mealInstructions, String strTags,
                         String strYoutube, String strIngredient1, String strIngredient2, String strIngredient3, String strIngredient4, String strIngredient5,
                         String strIngredient6, String strIngredient7, String strIngredient8, String strIngredient9, String strIngredient10, String strIngredient11,
                         String strIngredient12, String strIngredient13, String strIngredient14, String strIngredient15, String strIngredient16,
                         String strIngredient17, String strIngredient18, String strIngredient19, String strIngredient20, String strMeasure1,
                         String strMeasure2, String strMeasure3, String strMeasure4, String strMeasure5, String strMeasure6, String strMeasure7,
                         String strMeasure8, String strMeasure9, String strMeasure10, String strMeasure11, String strMeasure12, String strMeasure14,
                         String strMeasure13, String strMeasure15, String strMeasure16, String strMeasure17, String strMeasure18, String strMeasure19,
                         String strMeasure20, String strSource, String strImageSource) {
        this.idMeal = idMeal;
        this.mealName = mealName;
        this.mealCategory = mealCategory;
        this.mealArea = mealArea;
        this.strMealThumbnail = strMealThumbnail;
        this.mealInstructions = mealInstructions;
        this.strTags = strTags;
        this.strYoutube = strYoutube;
        this.strIngredient1 = strIngredient1;
        this.strIngredient2 = strIngredient2;
        this.strIngredient3 = strIngredient3;
        this.strIngredient4 = strIngredient4;
        this.strIngredient5 = strIngredient5;
        this.strIngredient6 = strIngredient6;
        this.strIngredient7 = strIngredient7;
        this.strIngredient8 = strIngredient8;
        this.strIngredient9 = strIngredient9;
        this.strIngredient10 = strIngredient10;
        this.strIngredient11 = strIngredient11;
        this.strIngredient12 = strIngredient12;
        this.strIngredient13 = strIngredient13;
        this.strIngredient14 = strIngredient14;
        this.strIngredient15 = strIngredient15;
        this.strIngredient16 = strIngredient16;
        this.strIngredient17 = strIngredient17;
        this.strIngredient18 = strIngredient18;
        this.strIngredient19 = strIngredient19;
        this.strIngredient20 = strIngredient20;
        this.strMeasure1 = strMeasure1;
        this.strMeasure2 = strMeasure2;
        this.strMeasure3 = strMeasure3;
        this.strMeasure4 = strMeasure4;
        this.strMeasure5 = strMeasure5;
        this.strMeasure6 = strMeasure6;
        this.strMeasure7 = strMeasure7;
        this.strMeasure8 = strMeasure8;
        this.strMeasure9 = strMeasure9;
        this.strMeasure10 = strMeasure10;
        this.strMeasure11 = strMeasure11;
        this.strMeasure12 = strMeasure12;
        this.strMeasure14 = strMeasure14;
        this.strMeasure13 = strMeasure13;
        this.strMeasure15 = strMeasure15;
        this.strMeasure16 = strMeasure16;
        this.strMeasure17 = strMeasure17;
        this.strMeasure18 = strMeasure18;
        this.strMeasure19 = strMeasure19;
        this.strMeasure20 = strMeasure20;
        this.strSource = strSource;
        this.strImageSource = strImageSource;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public String getMealName() {
        return mealName;
    }

    public String getMealCategory() {
        return mealCategory;
    }

    public String getMealArea() {
        return mealArea;
    }

    public String getMealInstructions() {
        return mealInstructions;
    }

    public String getStrMealThumbnail() {
        return strMealThumbnail;
    }

    public String getStrTags() {
        return strTags;
    }

    public String getStrYoutube() {
        return strYoutube;
    }

    public String getStrIngredient1() {
        return strIngredient1;
    }

    public String getStrIngredient2() {
        return strIngredient2;
    }

    public String getStrIngredient3() {
        return strIngredient3;
    }

    public String getStrIngredient4() {
        return strIngredient4;
    }

    public String getStrIngredient5() {
        return strIngredient5;
    }

    public String getStrIngredient6() {
        return strIngredient6;
    }

    public String getStrIngredient7() {
        return strIngredient7;
    }

    public String getStrIngredient8() {
        return strIngredient8;
    }

    public String getStrIngredient9() {
        return strIngredient9;
    }

    public String getStrIngredient10() {
        return strIngredient10;
    }

    public String getStrIngredient11() {
        return strIngredient11;
    }

    public String getStrIngredient12() {
        return strIngredient12;
    }

    public String getStrIngredient13() {
        return strIngredient13;
    }

    public String getStrIngredient14() {
        return strIngredient14;
    }

    public String getStrIngredient15() {
        return strIngredient15;
    }

    public String getStrIngredient16() {
        return strIngredient16;
    }

    public String getStrIngredient17() {
        return strIngredient17;
    }

    public String getStrIngredient18() {
        return strIngredient18;
    }

    public String getStrIngredient19() {
        return strIngredient19;
    }

    public String getStrIngredient20() {
        return strIngredient20;
    }

    public String getStrMeasure1() {
        return strMeasure1;
    }

    public String getStrMeasure2() {
        return strMeasure2;
    }

    public String getStrMeasure3() {
        return strMeasure3;
    }

    public String getStrMeasure4() {
        return strMeasure4;
    }

    public String getStrMeasure5() {
        return strMeasure5;
    }

    public String getStrMeasure6() {
        return strMeasure6;
    }

    public String getStrMeasure7() {
        return strMeasure7;
    }

    public String getStrMeasure8() {
        return strMeasure8;
    }

    public String getStrMeasure9() {
        return strMeasure9;
    }

    public String getStrMeasure10() {
        return strMeasure10;
    }

    public String getStrMeasure11() {
        return strMeasure11;
    }

    public String getStrMeasure12() {
        return strMeasure12;
    }

    public String getStrMeasure13() {
        return strMeasure13;
    }

    public String getStrMeasure14() {
        return strMeasure14;
    }

    public String getStrMeasure15() {
        return strMeasure15;
    }

    public String getStrMeasure16() {
        return strMeasure16;
    }

    public String getStrMeasure17() {
        return strMeasure17;
    }

    public String getStrMeasure18() {
        return strMeasure18;
    }

    public String getStrMeasure19() {
        return strMeasure19;
    }

    public String getStrMeasure20() {
        return strMeasure20;
    }

    public String getStrSource() {
        return strSource;
    }

    public String getStrImageSource() {
        return strImageSource;
    }


    public List<IngredientWithMeasure> getIngredientsWithMeasures() {
        List<IngredientWithMeasure> result = new ArrayList<>();

        // Loop through ingredients 1-20
        String[] ingredients = {strIngredient1, strIngredient2, strIngredient3,
                strIngredient4, strIngredient5, strIngredient6,
                strIngredient7, strIngredient8, strIngredient9,
                strIngredient10, strIngredient11, strIngredient12,
                strIngredient13, strIngredient14, strIngredient15,
                strIngredient16, strIngredient17, strIngredient18,
                strIngredient19, strIngredient20};

        String[] measures = {strMeasure1, strMeasure2, strMeasure3,
                strMeasure4, strMeasure5, strMeasure6,
                strMeasure7, strMeasure8, strMeasure9,
                strMeasure10, strMeasure11, strMeasure12,
                strMeasure13, strMeasure14, strMeasure15,
                strMeasure16, strMeasure17, strMeasure18,
                strMeasure19, strMeasure20};

        for (int i = 0; i < ingredients.length; i++) {
            String ingredient = ingredients[i];
            String measure = measures[i];

            if (ingredient != null && !ingredient.trim().isEmpty()) {
                String imageUrl = "https://www.themealdb.com/images/ingredients/"
                        + ingredient.trim() + ".png";
                result.add(new IngredientWithMeasure(ingredient, measure, imageUrl));
            }
        }

        return result;
    }
}
