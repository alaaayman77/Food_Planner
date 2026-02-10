package com.example.foodplanner.data;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.foodplanner.data.datasource.local.MealLocalDataSource;
import com.example.foodplanner.data.datasource.remote.AreaFilteredMealsNetworkResponse;
import com.example.foodplanner.data.datasource.remote.AreaNetworkResponse;
import com.example.foodplanner.data.datasource.remote.CategoryNetworkResponse;
import com.example.foodplanner.data.datasource.remote.IngredientFilteredMealsNetworkResponse;
import com.example.foodplanner.data.datasource.remote.IngredientsNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreDatasource;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealsByCategoryNetworkResponse;
import com.example.foodplanner.data.datasource.remote.MealsRemoteDataSource;
import com.example.foodplanner.data.datasource.remote.RecipeDetailsNetworkResponse;
import com.example.foodplanner.data.model.FavoriteMeal;
import com.example.foodplanner.data.model.category.CategoryResponse;
import com.example.foodplanner.data.model.category.MealsByCategoryResponse;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMealsResponse;
import com.example.foodplanner.data.model.filtered_meals.IngredientFilteredMealsResponse;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.data.model.random_meals.RandomMealResponse;
import com.example.foodplanner.data.model.recipe_details.RecipeDetailsResponse;
import com.example.foodplanner.data.model.search.area.AreaResponse;
import com.example.foodplanner.data.model.search.ingredients.IngredientsResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.function.Consumer;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class MealsRepository {

    private MealsRemoteDataSource mealsRemoteDataSource;
    private MealLocalDataSource mealLocalDataSource;
    private MealPlanFirestoreDatasource mealPlanFirestoreDatasource;
    private FirebaseAuth mAuth;
    public MealsRepository(Context context){

        mealsRemoteDataSource = new MealsRemoteDataSource();
        mealLocalDataSource = new MealLocalDataSource(context);
        mealPlanFirestoreDatasource = new MealPlanFirestoreDatasource();
        mAuth = FirebaseAuth.getInstance();
    }
    public Single<RandomMealResponse> getRandomMeal() {
         return mealsRemoteDataSource.getRandomMeal();
    }

    public Observable<CategoryResponse> getCategory(){
        return mealsRemoteDataSource.getCategory();
    }
    public Observable<MealsByCategoryResponse> getMealsByCategory(String category){
        return mealsRemoteDataSource.getMealsByCategory(category);
    }

    public Observable<AreaResponse> getArea(){
        return mealsRemoteDataSource.getAreas();
    }

    public Observable<AreaFilteredMealsResponse> getAreaFilteredMeals(String area){
        return mealsRemoteDataSource.getFilteredMealsByArea(area);
    }

    public Observable<IngredientsResponse> getIngredients(){
        return mealsRemoteDataSource.getIngredients();
    }

    public Observable<IngredientFilteredMealsResponse> getIngredientFilteredMeals(String ingredient){
       return  mealsRemoteDataSource.getFilteredMealsByIngredient(ingredient);
    }

    public Observable<RecipeDetailsResponse> getRecipeDetails(String id ){
       return  mealsRemoteDataSource.getRecipeDetails(id);
    }

    public Completable insertMealToMealPlan(MealPlan mealPlan){
        return mealLocalDataSource.insertMealPlan(mealPlan);
    }


    public LiveData<List<MealPlan>> getAllMealPlans(){
        return mealLocalDataSource.getAllMealPlans();
    }

    public Observable<List<MealPlan>> getMealPlansByDay(String day){
        return  mealLocalDataSource.getMealPlansByDay(day);
    }


//    public void deleteAllMealPlans(){
//        mealLocalDataSource.deleteAllMealPlans();
//    }
    public Completable deleteMealPlanById(int mealPlanId) {
        return mealLocalDataSource.deleteMealPlanById(mealPlanId);
    }

    public void saveMealPlanToFirestore(MealPlanFirestore mealPlan, MealPlanFirestoreNetworkResponse callback) {
        mealPlanFirestoreDatasource.saveMealPlan(mealPlan, callback);
    }

    public void getAllMealPlansFromFirestore(MealPlanFirestoreNetworkResponse callback) {
        mealPlanFirestoreDatasource.getAllMealPlans(callback);
    }

    public void deleteMealPlanFromFirestore(String mealId, String dayOfWeek, String mealType, MealPlanFirestoreNetworkResponse callback) {
        mealPlanFirestoreDatasource.deleteMealPlan(mealId, dayOfWeek, mealType, callback);
    }

    public void deleteAllMealPlansFromFirestore(MealPlanFirestoreNetworkResponse callback) {
        mealPlanFirestoreDatasource.deleteAllMealPlans(callback);
    }
    public void addtoFav(FavoriteMeal favoriteMeal){
        mealLocalDataSource.addToFav(favoriteMeal);
    }
    public LiveData<List<FavoriteMeal>> getAllFav(){
        return mealLocalDataSource.getAllFavorites();
    }
    public void deleteFav(String mealId) {
        mealLocalDataSource.deleteFavByMealId(mealId);
    }
    public void isFavorite(String mealId, Consumer<Boolean> callback){
        mealLocalDataSource.isFavorite(mealId, callback);
    }
    public void observeFavorites(
            LifecycleOwner owner,
            Observer<List<FavoriteMeal>> observer) {

        mealLocalDataSource.getAllFavorites().observe(owner, observer);
    }


}

