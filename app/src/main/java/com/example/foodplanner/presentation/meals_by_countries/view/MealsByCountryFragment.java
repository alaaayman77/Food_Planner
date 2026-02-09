package com.example.foodplanner.presentation.meals_by_countries.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.MainActivity;
import com.example.foodplanner.R;
import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.model.FavoriteMeal;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.filtered_meals.AreaFilteredMeals;
import com.example.foodplanner.presentation.auth.SignInPromptDialog;
import com.example.foodplanner.presentation.filter_results.presenter.FilteredResultsPresenter;
import com.example.foodplanner.presentation.mealplanner.view.MealPlanHelper;
import com.example.foodplanner.presentation.meals_by_category.presenter.MealsByCategoryPresenter;
import com.example.foodplanner.presentation.meals_by_category.presenter.MealsByCategoryPresenterImp;
import com.example.foodplanner.presentation.meals_by_category.view.MealsByCategoryFragmentArgs;
import com.example.foodplanner.presentation.meals_by_category.view.MealsByCategoryFragmentDirections;
import com.example.foodplanner.presentation.meals_by_countries.presenter.MealsByCountryPresenter;
import com.example.foodplanner.presentation.meals_by_countries.presenter.MealsByCountryPresenterImp;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MealsByCountryFragment extends Fragment implements OnMealByCountryClick, MealsByCountryView {

    MaterialButton backButton;

    private TextView countryTitle;

    private String countryName;

    private MealsByCountryPresenter mealsByCountryPresenter;

    private TextView emptyStateTitle;
    private TextView emptyStateSubtitle;
    private RecyclerView mealByCountryRecyclerView;
    private MealsByCountryAdapter mealByCountryAdapter;

    private MealsRepository mealsRepository;

    private List<AreaFilteredMeals> allMealsList = new ArrayList<>();
    private List<AreaFilteredMeals> filteredMealsList = new ArrayList<>();
    private Set<String> favoriteMealIds = new HashSet<>();

    private EditText searchEditText;

    public MealsByCountryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            MealsByCountryFragmentArgs args = MealsByCountryFragmentArgs.fromBundle(getArguments());
            countryName = args.getCountryName();
        }
        mealsRepository = new MealsRepository(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_country, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        countryTitle = view.findViewById(R.id.countryTitle_tv);
        countryTitle.setText(countryName);

        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        mealByCountryRecyclerView = view.findViewById(R.id.mealByCategoryRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        mealByCountryAdapter = new MealsByCountryAdapter(this);
        emptyStateTitle = view.findViewById(R.id.emptyStateTitle);
        emptyStateSubtitle = view.findViewById(R.id.emptyStateSubtitle);

        mealByCountryRecyclerView.setAdapter(mealByCountryAdapter);
        mealsByCountryPresenter = new MealsByCountryPresenterImp(this, requireContext());
        mealsByCountryPresenter.getMealsByArea(countryName);
        mealByCountryAdapter.setOnMealActionListener(this);
        searchEditText.setHint("Search " + countryName.toLowerCase() + "...");

        setupSearchFunctionality();
        loadFavorites();
    }

    private void loadFavorites() {
        mealsRepository.getAllFav().observe(getViewLifecycleOwner(), favorites -> {
            favoriteMealIds.clear();
            if (favorites != null) {
                for (FavoriteMeal favorite : favorites) {
                    favoriteMealIds.add(favorite.getMealId());
                }
            }
            mealByCountryAdapter.setFavoriteMealIds(favoriteMealIds);
        });
    }

    @Override
    public void setMealByAreaList(List<AreaFilteredMeals> mealsByAreaList) {
        mealByCountryAdapter.setMeals(mealsByAreaList);
        allMealsList.clear();
        allMealsList.addAll(mealsByAreaList);

        filteredMealsList.clear();
        filteredMealsList.addAll(mealsByAreaList);
        hideLoading();
        updateUI();
    }

    @Override
    public void showError(String message) {
        hideLoading();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        mealByCountryRecyclerView.setVisibility(View.GONE);
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
            for (AreaFilteredMeals meal : allMealsList) {
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
            mealByCountryRecyclerView.setVisibility(View.GONE);
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
            mealByCountryRecyclerView.setVisibility(View.VISIBLE);
            emptyStateTitle.setVisibility(View.GONE);
            emptyStateSubtitle.setVisibility(View.GONE);
            mealByCountryAdapter.setMeals(filteredMealsList);
        }
    }

    @Override
    public void showLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showLoading();
        }
        mealByCountryRecyclerView.setVisibility(View.GONE);
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
    public void onFavAddedSuccess() {
        Toast.makeText(getContext(),
                "Added to favorites! ❤️",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavAddedFailure(String error) {
        Toast.makeText(getContext(),
                "Failed to add to favorites: " + error,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSignInPrompt(String featureName, String message) {
        SignInPromptDialog dialog = SignInPromptDialog.newInstance(featureName, message);
        dialog.setListener(new SignInPromptDialog.SignInPromptListener() {
            @Override
            public void onSignInClicked() {

                NavController navController =
                        NavHostFragment.findNavController(
                                requireActivity()
                                        .getSupportFragmentManager()
                                        .findFragmentById(R.id.nav_host_fragment_main)
                        );

                navController.navigate(R.id.authFragment);
            }

            @Override
            public void onContinueAsGuestClicked() {

                Toast.makeText(getContext(),
                        "Continuing as guest. Your data won't be saved.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getParentFragmentManager(), "SignInPromptDialog");
    }

    @Override
    public void setOnMealClick(AreaFilteredMeals meal) {
        MealsByCountryFragmentDirections.ActionMealsByCountryFragmentToRecipeDetailsFragment action =
                MealsByCountryFragmentDirections.actionMealsByCountryFragmentToRecipeDetailsFragment(meal.getIdMeal());
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onAddToPlanClick(AreaFilteredMeals meal) {
        MealPlanHelper.showAddMealPlanDialog(
                getParentFragmentManager(),
                meal.getIdMeal(),
                meal.getMealName(),
                meal.getIdMeal(),
                mealPlan -> {
                    mealsByCountryPresenter.addMealToPlan(mealPlan);
                }
        );
    }

    @Override
    public void onFavoriteClick(AreaFilteredMeals meal) {
        boolean isFavorite = favoriteMealIds.contains(meal.getIdMeal());

        if (isFavorite) {

            mealsByCountryPresenter.removeFromFav(meal.getIdMeal());
            Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {

            mealsByCountryPresenter.fetchRecipeDetailsAndAddToFavorites(meal.getIdMeal());
        }
    }
}