package com.example.foodplanner.data.network;



import com.example.foodplanner.data.model.category.CategoryResponse;
import com.example.foodplanner.data.model.category.MealsByCategoryResponse;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMeals;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMealsResponse;
import com.example.foodplanner.data.model.filtered_meals.IngredientFilteredMealsResponse;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.data.model.random_meals.RandomMealResponse;
import com.example.foodplanner.data.model.recipe_details.RecipeDetailsResponse;
import com.example.foodplanner.data.model.search.area.AreaResponse;
import com.example.foodplanner.data.model.search.ingredients.IngredientsResponse;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealsService {
    @GET("random.php")
    Single<RandomMealResponse> getRandomMeal();

    @GET("categories.php")
    Observable<CategoryResponse> getCategory();

    @GET("filter.php")
    Observable<MealsByCategoryResponse> getMealsByCategory(@Query("c") String category);

    @GET("filter.php")
    Observable<AreaFilteredMealsResponse> getFilteredMealsByArea(@Query("a") String area);
    @GET("filter.php")
    Observable<IngredientFilteredMealsResponse> getFilteredMealsByIngredient(@Query("i") String ingredient);

    @GET("list.php?i=list")
    Observable<IngredientsResponse> getIngredients();

    @GET("list.php?a=list")
    Observable<AreaResponse> getArea();

    @GET("lookup.php")
    Call<RecipeDetailsResponse> getRecipeDetails(@Query("i") String id);
}
