package com.example.foodplanner.ui.category;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodplanner.R;
import com.example.foodplanner.adapters.MealsByCategoryAdapter;
import com.example.foodplanner.data.network.Network;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.category.MealsByCategoryResponse;
import com.example.foodplanner.data.network.MealsService;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CategoryFragment extends Fragment {
    MaterialButton backButton;
    String category;
    private TextView category_tv;
    private ImageView mealImage;
    private TextView mealTitle;
    private TextView tag1;
    private  TextView tag2;

    private RecyclerView mealByCategoriesRecyclerView;
    private MealsByCategoryAdapter mealByCategoryAdapter;
    private List<MealsByCategory> mealsByCategoryList;

    public CategoryFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            CategoryFragmentArgs args = CategoryFragmentArgs.fromBundle(getArguments());
            category = args.getCategory();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        category_tv = view.findViewById(R.id.categoryTitle_tv);
        category_tv.setText(category);
         backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        mealImage = view.findViewById(R.id.mealImage_category);
        mealTitle = view.findViewById(R.id.mealFab);

        mealByCategoriesRecyclerView = view.findViewById(R.id.mealByCategoryRecyclerView);

        mealsByCategoryList = new ArrayList<>();
        mealByCategoryAdapter = new MealsByCategoryAdapter(getContext(), mealsByCategoryList);
        mealByCategoriesRecyclerView.setAdapter(mealByCategoryAdapter);
        mealByCategoryAdapter.setOnMealsByCategoryClickListener(category -> {
            CategoryFragmentDirections.ActionCategoryFragmentToRecipeDetailsFragment action =
                    CategoryFragmentDirections.actionCategoryFragmentToRecipeDetailsFragment(category.getMealId());
            Navigation.findNavController(view).navigate(action);
                }
        );
        getMealsByCategory();


    }

    private void getMealsByCategory(){
        MealsService mealsService = Network.getInstance().getMealsService();
        mealsService.getMealsByCategory(category).enqueue(new Callback<MealsByCategoryResponse>() {

            @Override
            public void onResponse(Call<MealsByCategoryResponse> call, Response<MealsByCategoryResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response body: " + response.body());

                if(response.isSuccessful() && response.body() != null){
                    MealsByCategoryResponse mealsByCategoryResponse = response.body();

                    Log.d(TAG, "Meals list: " + mealsByCategoryResponse.getMealsByCategories());

                    if (mealsByCategoryResponse.getMealsByCategories() != null) {
                        List<MealsByCategory> meals = mealsByCategoryResponse.getMealsByCategories();
                        Log.d(TAG, "Number of meals: " + meals.size());

                        // Log first meal details if available
                        if (!meals.isEmpty()) {
                            MealsByCategory firstMeal = meals.get(0);
                            Log.d(TAG, "First meal name: " + firstMeal.getMealName());
                            Log.d(TAG, "First meal thumb: " + firstMeal.getMealThumbnail());
                        }

                        mealByCategoryAdapter.setMeals(meals);
                    } else {
                        Log.e(TAG, "Meals list is null!");
                        showError("No meals found for " + category);
                    }
                }
                else {
                    Log.e(TAG, "Response unsuccessful: " + response.code());
                    Log.e(TAG, "Error body: " + response.errorBody());
                    showError("Failed to load meal");
                }
            }

            @Override
            public void onFailure(Call<MealsByCategoryResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                showError("Network error: " + t.getMessage());
            }
        });
    }
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


}