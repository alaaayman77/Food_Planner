package com.example.foodplanner.presentation.meals_by_category.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.presentation.meals_by_category.presenter.MealsByCategoryPresenter;
import com.example.foodplanner.presentation.meals_by_category.presenter.MealsByCategoryPresenterImp;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class MealsByCategoryFragment extends Fragment implements OnMealByCategoryClick , MealsByCategoryView {
    MaterialButton backButton;
    String category;
    private TextView category_tv;
    private ImageView mealImage;
    private TextView mealTitle;
    private TextView tag1;
    private  TextView tag2;

    private RecyclerView mealByCategoriesRecyclerView;
    private MealsByCategoryAdapter mealByCategoryAdapter;
    private MealsByCategoryPresenter mealsByCategoryPresenter;
    private List<MealsByCategory> mealsByCategoryList;


    public MealsByCategoryFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            MealsByCategoryFragmentArgs args = MealsByCategoryFragmentArgs.fromBundle(getArguments());
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

        mealByCategoryAdapter = new MealsByCategoryAdapter(this);
        mealByCategoriesRecyclerView.setAdapter(mealByCategoryAdapter);
        mealsByCategoryPresenter = new MealsByCategoryPresenterImp(this,requireContext());
        mealsByCategoryPresenter.getMealsByCategory(category);


    }

    @Override
    public void setMealByCategoryList(List<MealsByCategory> mealsByCategoryList) {
        mealByCategoryAdapter.setMeals(mealsByCategoryList);
    }
   @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void setOnMealByCategoryClick(MealsByCategory meal) {
        MealsByCategoryFragmentDirections.ActionMealsByCategoryFragmentToRecipeDetailsFragment action =
                MealsByCategoryFragmentDirections.actionMealsByCategoryFragmentToRecipeDetailsFragment(meal.getMealId());
        Navigation.findNavController(requireView()).navigate(action);
    }
}