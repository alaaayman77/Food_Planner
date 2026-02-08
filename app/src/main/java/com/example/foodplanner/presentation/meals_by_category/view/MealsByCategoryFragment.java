package com.example.foodplanner.presentation.meals_by_category.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodplanner.MainActivity;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.presentation.mealplanner.view.MealPlanHelper;
import com.example.foodplanner.presentation.meals_by_category.presenter.MealsByCategoryPresenter;
import com.example.foodplanner.presentation.meals_by_category.presenter.MealsByCategoryPresenterImp;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class MealsByCategoryFragment extends Fragment implements OnMealByCategoryClick , MealsByCategoryView {
    MaterialButton backButton;
    String category;
    private TextView category_tv;
    private TextView emptyStateTitle;
    private TextView emptyStateSubtitle;

    private RecyclerView mealByCategoriesRecyclerView;
    private MealsByCategoryAdapter mealByCategoryAdapter;
    private MealsByCategoryPresenter mealsByCategoryPresenter;

    private List<MealsByCategory> allMealsList = new ArrayList<>();
    private List<MealsByCategory> filteredMealsList = new ArrayList<>();

    private EditText searchEditText;
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

        mealByCategoriesRecyclerView = view.findViewById(R.id.mealByCategoryRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        mealByCategoryAdapter = new MealsByCategoryAdapter(this);
        emptyStateTitle = view.findViewById(R.id.emptyStateTitle);
        emptyStateSubtitle = view.findViewById(R.id.emptyStateSubtitle);
        mealByCategoriesRecyclerView.setAdapter(mealByCategoryAdapter);
        mealsByCategoryPresenter = new MealsByCategoryPresenterImp(this,requireContext());
        mealsByCategoryPresenter.getMealsByCategory(category);
        mealByCategoryAdapter.setOnMealActionListener(this);
        searchEditText.setHint("Search " + category.toLowerCase() + "...");

        setupSearchFunctionality();


    }

    @Override
    public void setMealByCategoryList(List<MealsByCategory> mealsByCategoryList) {
        mealByCategoryAdapter.setMeals(mealsByCategoryList);
        allMealsList.clear();
        allMealsList.addAll(mealsByCategoryList);

        filteredMealsList.clear();
        filteredMealsList.addAll(mealsByCategoryList);
        hideLoading();
        updateUI();
    }
   @Override
    public void showError(String message) {
       hideLoading();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
       mealByCategoriesRecyclerView.setVisibility(View.GONE);
       emptyStateTitle.setVisibility(View.VISIBLE);
       emptyStateSubtitle.setVisibility(View.VISIBLE);
       emptyStateTitle.setText("Error loading meals");
       emptyStateSubtitle.setText(message);
    }
    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMeals(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void filterMeals(String query) {
        filteredMealsList.clear();

        if (query.isEmpty()) {

            filteredMealsList.addAll(allMealsList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (MealsByCategory meal : allMealsList) {
                if (meal.getMealName() != null &&
                        meal.getMealName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredMealsList.add(meal);
                }
            }
        }

        updateUI();
    }

    private void updateUI() {
        if (filteredMealsList.isEmpty()) {

            mealByCategoriesRecyclerView.setVisibility(View.GONE);
            emptyStateTitle.setVisibility(View.VISIBLE);
            emptyStateSubtitle.setVisibility(View.VISIBLE);

            if (searchEditText.getText().toString().trim().isEmpty()) {
                emptyStateTitle.setText("No meals found");
                emptyStateSubtitle.setText("This category has no meals");
            } else {
                emptyStateTitle.setText("No results found");
                emptyStateSubtitle.setText("Try a different search term");
            }
        } else {

            mealByCategoriesRecyclerView.setVisibility(View.VISIBLE);
            emptyStateTitle.setVisibility(View.GONE);
            emptyStateSubtitle.setVisibility(View.GONE);
            mealByCategoryAdapter.setMeals(filteredMealsList);
        }
    }




    @Override
    public void showLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showLoading();
        }
        mealByCategoriesRecyclerView.setVisibility(View.GONE);
        emptyStateTitle.setVisibility(View.GONE);
        emptyStateSubtitle.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideLoading();
        }
    }

    @Override
    public void onMealPlanAddedSuccess() {
        Toast.makeText(getContext(),
                "Meal added to your plan! 📅",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMealPlanAddedFailure(String error) {
        Toast.makeText(getContext(),
                "Failed to add meal: " + error,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setOnMealClick(MealsByCategory meal) {
        MealsByCategoryFragmentDirections.ActionMealsByCategoryFragmentToRecipeDetailsFragment action =
                MealsByCategoryFragmentDirections.actionMealsByCategoryFragmentToRecipeDetailsFragment(meal.getMealId());
        Navigation.findNavController(requireView()).navigate(action);
    }



    @Override
    public void onAddToPlanClick(MealsByCategory meal) {
        MealPlanHelper.showAddMealPlanDialog(
                getParentFragmentManager(),
                meal.getMealId(),
                meal.getMealName(),
                meal.getMealThumbnail(),
                mealPlan -> {
                    mealsByCategoryPresenter.addMealToPlan(mealPlan);
                }
        );
    }

    @Override
    public void onFavoriteClick(MealsByCategory meal) {

    }
}