
package com.example.foodplanner.ui.home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.adapters.CategoryAdapter;
import com.example.foodplanner.api.RetrofitClient;
import com.example.foodplanner.model.category.Category;
import com.example.foodplanner.model.category.CategoryResponse;
import com.example.foodplanner.model.random_meals.RandomMeal;
import com.example.foodplanner.model.random_meals.RandomMealResponse;
import com.example.foodplanner.services.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private ImageView mealImage;
    private TextView mealTitle;
    private TextView tag1;
    private  TextView tag2;

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private static final String TAG = "HomeFragment";
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mealImage = view.findViewById(R.id.mealImage);
        mealTitle = view.findViewById(R.id.mealTitle);
        tag1 = view.findViewById(R.id.tag1);
        tag2 = view.findViewById(R.id.tag2);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);

        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        categoriesRecyclerView.setAdapter(categoryAdapter);
        categoryAdapter.setOnCategoryClickListener(category -> {
            Toast.makeText(getContext(),
                    "Selected: " + category.getStrCategory(),
                    Toast.LENGTH_SHORT).show();

        });

        getCategory();
        getRandomMeal();
    }

    private void getRandomMeal(){
        RetrofitService retrofitService = RetrofitClient.getRetrofit().create(RetrofitService.class);
        retrofitService.getRandomMeal().enqueue(new Callback<RandomMealResponse>() {

            @Override
            public void onResponse(Call<RandomMealResponse> call, Response<RandomMealResponse> response) {
                if(response.isSuccessful() &&response.body()!=null){
                    RandomMealResponse randomMealResponse = response.body();
                    RandomMeal randomMeal = randomMealResponse.getRandomMeal();
                    if(randomMeal!=null){
                        displayMeal(randomMeal);
                    }
                    else{
                        Log.e(TAG, "No meal found in response");
                        showError("No meal found");
                    }

                }
                else {
                    Log.e(TAG, "Response unsuccessful: " + response.code());
                    showError("Failed to load meal");
                }
            }

            @Override
            public void onFailure(Call<RandomMealResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                showError("Network error: " + t.getMessage());
            }
        });
    }
    private void getCategory(){
        RetrofitService service = RetrofitClient.getRetrofit().create(RetrofitService.class);
        service.getCategory().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<Category> categoriesFromApi = response.body().getCategories();
                    if(categoriesFromApi != null){

                        categoryList.addAll(categoriesFromApi);
                        categoryAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e(TAG, "Failed to load categories: " + response.code());
                    showError("Failed to load categories");
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                showError("Network error: " + t.getMessage());
            }
        });
    }
    private void displayMeal(RandomMeal meal) {

        mealTitle.setText(meal.getStrMeal());


        if (meal.getStrCategory() != null) {
            tag1.setText(meal.getStrCategory().toUpperCase());
            tag1.setVisibility(View.VISIBLE);
        } else {
            tag1.setVisibility(View.GONE);
        }


        if (meal.getStrArea() != null) {
            tag2.setText(meal.getStrArea().toUpperCase());
            tag2.setVisibility(View.VISIBLE);
        } else {
            tag2.setVisibility(View.GONE);
        }



        if (meal.getStrMealThumb() != null && !meal.getStrMealThumb().isEmpty()) {
            Glide.with(this)
                    .load(meal.getStrMealThumb())
                    .placeholder(R.drawable.splash_bg)
                    .error(R.drawable.splash_bg)
                    .centerCrop()
                    .into(mealImage);
        }

        Log.d(TAG, "Meal displayed: " + meal.getStrMeal());
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}