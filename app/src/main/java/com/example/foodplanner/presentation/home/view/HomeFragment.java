
package com.example.foodplanner.presentation.home.view;

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

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.datasource.CategoryNetworkResponse;
import com.example.foodplanner.data.datasource.MealNetworkResponse;
import com.example.foodplanner.data.datasource.MealsRemoteDataSource;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.presentation.home.presenter.HomePresenter;
import com.example.foodplanner.presentation.home.presenter.HomePresenterImp;
import com.example.foodplanner.presentation.home.view.HomeFragmentDirections;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment  implements OnCategoryClick, HomeView {
    private ImageView mealImage;
    private TextView mealTitle;
    private TextView tag1;
    private  TextView tag2;

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    private HomePresenter homePresenter;
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
        categoryAdapter = new CategoryAdapter(this);
        categoriesRecyclerView.setAdapter(categoryAdapter);
        homePresenter = new HomePresenterImp(this);
        homePresenter.getRandomMeal();
        homePresenter.getCategory();



    }


    @Override
    public void setOnCategoryClick(Category category) {
        HomeFragmentDirections.ActionHomeFragmentToMealsByCategoryFragment action =
            HomeFragmentDirections.actionHomeFragmentToMealsByCategoryFragment(category.getCategoryName());
        Navigation.findNavController(requireView()).navigate(action);
        homePresenter.onCategoryClick(category);
    }

    @Override
    public void setCategoryList(List<Category> categoryList) {
        categoryAdapter.setCategories(categoryList);
    }

    @Override
    public void showError(String errorMessage) {
        if (getContext() != null) {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void displayMeal(RandomMeal meal) {
                mealTitle.setText(meal.getMealName());


        if (meal.getMealCategory() != null) {
            tag1.setText(meal.getMealCategory().toUpperCase());
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



        if (meal.getMealThumbnail() != null && !meal.getMealThumbnail().isEmpty()) {
            Glide.with(this)
                    .load(meal.getMealThumbnail())
                    .placeholder(R.drawable.splash_bg)
                    .error(R.drawable.splash_bg)
                    .centerCrop()
                    .into(mealImage);
        }

        Log.d(TAG, "Meal displayed: " + meal.getMealName());

    }

    @Override
    public void OnCategoryClickSuccess(Category category) {
        Toast.makeText(getContext(),
                "Selected: " + category.getCategoryName(),
                Toast.LENGTH_SHORT).show();


    }
}