package com.example.foodplanner.presentation.filter_results.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.filter_results.presenter.FilterResultsPresenterImp;
import com.example.foodplanner.presentation.filter_results.presenter.FilteredResultsPresenter;
import com.example.foodplanner.presentation.meals_by_category.view.MealsByCategoryFragmentDirections;
import com.google.android.material.button.MaterialButton;


import java.util.List;

public class FilterResultsFragment extends Fragment implements FilterResultsView , OnMealClick {
    private String[] mealIds;
    private RecyclerView recyclerView;
    private FilterResultsAdapter adapter;
    private FilteredResultsPresenter presenter;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private MaterialButton backBtn;

    public FilterResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FilterResultsFragmentArgs args = FilterResultsFragmentArgs.fromBundle(getArguments());
            mealIds = args.getIdMeals();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_filter_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewFilterResults);
        progressBar = view.findViewById(R.id.progressBar);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        backBtn = view.findViewById(R.id.back_button);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FilterResultsAdapter( this);
        presenter = new FilterResultsPresenterImp(this);

        recyclerView.setAdapter(adapter);
        presenter.getFilteredRecipes(mealIds);
        backBtn.setOnClickListener(v->{
            Navigation.findNavController(v).navigateUp();
        });
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayMeals(List<RecipeDetails> meals) {
        recyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        adapter.setMeals(meals);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
        emptyTextView.setVisibility(View.VISIBLE);
        emptyTextView.setText("Failed to load meals");
    }

    @Override
    public void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
        emptyTextView.setText("No meals found");
    }

    @Override
    public void onMealClicked(RecipeDetails meal) {
        FilterResultsFragmentDirections.ActionFilterResultsFragmentToRecipeDetailsFragment action =
                FilterResultsFragmentDirections.actionFilterResultsFragmentToRecipeDetailsFragment(meal.getIdMeal());
        Navigation.findNavController(requireView()).navigate(action);
    }
}
