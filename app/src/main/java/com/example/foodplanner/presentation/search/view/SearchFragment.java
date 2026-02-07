package com.example.foodplanner.presentation.search.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.search.area.Area;
import com.example.foodplanner.data.model.search.ingredients.Ingredients;

import com.example.foodplanner.presentation.filter_results.view.FilterResultsFragmentDirections;
import com.example.foodplanner.presentation.multi_filter.MultiFilterPresenter;
import com.example.foodplanner.presentation.multi_filter.MultiFilterPresenterImp;
import com.example.foodplanner.presentation.multi_filter.MultiFilterView;
import com.example.foodplanner.presentation.search.presenter.area.AreaPresenter;
import com.example.foodplanner.presentation.search.presenter.area.AreaPresenterImp;
import com.example.foodplanner.presentation.search.presenter.category.CategoryPresenter;
import com.example.foodplanner.presentation.search.presenter.category.CategoryPresenterImp;
import com.example.foodplanner.presentation.search.presenter.ingredients.IngredientsPresenter;
import com.example.foodplanner.presentation.search.presenter.ingredients.IngredientsPresenterImp;
import com.example.foodplanner.presentation.search.presenter.search.SearchPresenter;
import com.example.foodplanner.presentation.search.presenter.search.SearchPresenterImp;
import com.example.foodplanner.presentation.search.view.area.AreaView;
import com.example.foodplanner.presentation.search.view.area.CountryChipAdapter;
import com.example.foodplanner.presentation.search.view.category.CategoryView;
import com.example.foodplanner.presentation.search.view.category.SearchCategoryAdapter;
import com.example.foodplanner.presentation.search.view.ingredients.IngredientsView;
import com.example.foodplanner.presentation.search.view.ingredients.PaginationManager;
import com.example.foodplanner.presentation.search.view.ingredients.SearchIngredientsAdapter;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class SearchFragment extends Fragment implements
        SearchCategoryAdapter.OnCategoryClickListener,
        AreaView,
        CountryChipAdapter.OnCountryChipClickListener,
        MultiFilterView,
        CategoryView,
        IngredientsView,
        SearchIngredientsAdapter.OnIngredientClickListener,
        SearchView,
        SearchResultsAdapter.OnMealClickListener {

    private EditText searchEditText;
    private ImageView clearSearchButton;
    private Button searchButton;
    private RecyclerView categoryRecyclerView;
    private RecyclerView ingredientsRecyclerView;
    private RecyclerView searchResultsRecyclerView;
    private ChipGroup areaChipGroup;
    private TextView seeAllIngredients;
    private TextView browseByCategoryTitle;
    private TextView exploreByCountryTitle;
    private TextView viewAllCountries;
    private TextView searchByIngredientTitle;

    private SearchCategoryAdapter searchCategoryAdapter;
    private SearchIngredientsAdapter searchIngredientsAdapter;
    private CountryChipAdapter countryChipAdapter;
    private SearchResultsAdapter searchResultsAdapter;

    private AreaPresenter areaPresenter;
    private CategoryPresenter categoryPresenter;
    private IngredientsPresenter ingredientsPresenter;
    private MultiFilterPresenter multiFilterPresenter;
    private SearchPresenter searchPresenter;
    private String[] mealsIds;
    private PaginationManager<Ingredients> ingredientsPaginationManager;
    private static final int INGREDIENTS_PAGE_SIZE = 15;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ingredientsPaginationManager = new PaginationManager<>(INGREDIENTS_PAGE_SIZE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        searchEditText = view.findViewById(R.id.searchEditText);
        clearSearchButton = view.findViewById(R.id.clearSearchButton);
        searchButton = view.findViewById(R.id.filterButton);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        ingredientsRecyclerView = view.findViewById(R.id.ingredientRecyclerView);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        areaChipGroup = view.findViewById(R.id.countryChipGroup);
        seeAllIngredients = view.findViewById(R.id.seeAllIngredients);
        browseByCategoryTitle = view.findViewById(R.id.browseByCategoryTitle);
        exploreByCountryTitle = view.findViewById(R.id.countryTitle);
        viewAllCountries = view.findViewById(R.id.viewAllCountries);
        searchByIngredientTitle = view.findViewById(R.id.ingredientTitle);


        searchCategoryAdapter = new SearchCategoryAdapter(requireContext(), this);
        searchIngredientsAdapter = new SearchIngredientsAdapter(requireContext(), this);
        countryChipAdapter = new CountryChipAdapter(requireContext(), areaChipGroup, this);
        searchResultsAdapter = new SearchResultsAdapter(requireContext(), this);

        searchCategoryAdapter.setLoading(true);
        searchIngredientsAdapter.setLoading(true);
        categoryRecyclerView.setAdapter(searchCategoryAdapter);

        LinearLayoutManager ingredientsLayoutManager = new LinearLayoutManager(requireContext());
        ingredientsRecyclerView.setLayoutManager(ingredientsLayoutManager);
        ingredientsRecyclerView.setAdapter(searchIngredientsAdapter);
        ingredientsRecyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager searchResultsLayoutManager = new LinearLayoutManager(requireContext());
        searchResultsRecyclerView.setLayoutManager(searchResultsLayoutManager);
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
        searchResultsRecyclerView.setNestedScrollingEnabled(false);
        searchCategoryAdapter.setLoading(true);
        searchIngredientsAdapter.setLoading(true);
        searchIngredientsAdapter.setOnSeeMoreClickListener(() -> {
            List<Ingredients> nextPage = ingredientsPaginationManager.getNextPage();
            if (!nextPage.isEmpty()) {
                searchIngredientsAdapter.addIngredients(nextPage);
            }
            boolean hasMore = ingredientsPaginationManager.hasMorePages();
            searchIngredientsAdapter.setSeeMoreVisible(hasMore);

            int loaded = ingredientsPaginationManager.getLoadedItemsCount();
            int total = ingredientsPaginationManager.getTotalItems();
            if (loaded >= total) {
                seeAllIngredients.setText(total + " ingredients");
            } else {
                seeAllIngredients.setText(loaded + " of " + total);
            }
        });

        // Setup presenters
        categoryPresenter = new CategoryPresenterImp(this , requireContext());
        areaPresenter = new AreaPresenterImp(this, requireContext());
        ingredientsPresenter = new IngredientsPresenterImp(this , requireContext());
        multiFilterPresenter = new MultiFilterPresenterImp(this , requireContext());
        searchPresenter = new SearchPresenterImp(this , requireContext());

        // Load initial data
        areaPresenter.getArea();
        categoryPresenter.getCategory();
        ingredientsPresenter.getIngredients();

        // Search button click for filters
        searchButton.setOnClickListener(v -> {
            List<Category> selectedCategories = searchCategoryAdapter.getSelectedCategories();
            List<Area> selectedAreas = countryChipAdapter.getSelectedAreas();
            List<Ingredients> selectedIngredients = searchIngredientsAdapter.getSelectedIngredients();

            List<String> categoryNames = new ArrayList<>();
            for (Category category : selectedCategories) {
                categoryNames.add(category.getCategoryName());
            }

            List<String> areaNames = new ArrayList<>();
            for (Area area : selectedAreas) {
                areaNames.add(area.getStrArea());
            }

            List<String> ingredientNames = new ArrayList<>();
            for (Ingredients ingredients : selectedIngredients) {
                ingredientNames.add(ingredients.getIngredientName());
            }

            multiFilterPresenter.searchWithFilters(categoryNames, areaNames, ingredientNames);


        });


        Observable<String> searchObservable = Observable.create(emitter -> {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        clearSearchButton.setVisibility(View.VISIBLE);
                    } else {
                        clearSearchButton.setVisibility(View.GONE);
                        // Show filters, hide search results
                        browseByCategoryTitle.setVisibility(View.VISIBLE);
                        categoryRecyclerView.setVisibility(View.VISIBLE);
                        exploreByCountryTitle.setVisibility(View.VISIBLE);
                        viewAllCountries.setVisibility(View.VISIBLE);
                        areaChipGroup.setVisibility(View.VISIBLE);
                        searchByIngredientTitle.setVisibility(View.VISIBLE);
                        seeAllIngredients.setVisibility(View.VISIBLE);
                        ingredientsRecyclerView.setVisibility(View.VISIBLE);
                        searchButton.setVisibility(View.VISIBLE);
                        searchResultsRecyclerView.setVisibility(View.GONE);
                    }
                    emitter.onNext(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        });

        searchObservable
                .debounce(1, TimeUnit.SECONDS)
                .filter(query -> query.length() > 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(query -> {
                    // Hide filters
                    browseByCategoryTitle.setVisibility(View.GONE);
                    categoryRecyclerView.setVisibility(View.GONE);
                    exploreByCountryTitle.setVisibility(View.GONE);
                    viewAllCountries.setVisibility(View.GONE);
                    areaChipGroup.setVisibility(View.GONE);
                    searchByIngredientTitle.setVisibility(View.GONE);
                    seeAllIngredients.setVisibility(View.GONE);
                    ingredientsRecyclerView.setVisibility(View.GONE);
                    searchButton.setVisibility(View.GONE);

                    // Show search results
                    searchResultsRecyclerView.setVisibility(View.VISIBLE);

                    // Perform search
                    searchPresenter.searchMealsByName(query);
                }, error -> {
                    Log.e("SearchFragment", "Error in search observable", error);
                });



        // Clear search button
        clearSearchButton.setOnClickListener(v -> {
            searchEditText.setText("");
            searchResultsAdapter.clearMeals();
        });
    }

    @Override
    public void setIngredients(List<Ingredients> ingredientsList) {
        searchIngredientsAdapter.setLoading(false);
        ingredientsPaginationManager.setAllItems(ingredientsList);
        searchIngredientsAdapter.clearIngredients();

        List<Ingredients> nextPage = ingredientsPaginationManager.getNextPage();
        if (!nextPage.isEmpty()) {
            searchIngredientsAdapter.addIngredients(nextPage);
        }

        boolean hasMore = ingredientsPaginationManager.hasMorePages();
        searchIngredientsAdapter.setSeeMoreVisible(hasMore);

        int loaded = ingredientsPaginationManager.getLoadedItemsCount();
        int total = ingredientsPaginationManager.getTotalItems();
        if (loaded >= total) {
            seeAllIngredients.setText(total + " ingredients");
        } else {
            seeAllIngredients.setText(loaded + " of " + total);
        }
    }

    @Override
    public void setArea(List<Area> areaList) {
        countryChipAdapter.setCountries(areaList);
    }

    @Override
    public void showLoading() {
        searchButton.setEnabled(false);
        searchButton.setText("Searching...");
    }

    @Override
    public void hideLoading() {
        searchButton.setEnabled(true);
        searchButton.setText("Apply Filters");
    }

    @Override
    public void showFilteredResults(List<String> mealIds) {
         this.mealsIds = mealIds.toArray(new String[0]);

        StringBuilder message = new StringBuilder();
        message.append("Found ").append(mealIds.size()).append(" meals!\n\n");

        int displayCount = Math.min(5, mealIds.size());
        for (int i = 0; i < displayCount; i++) {
            message.append("Meal ID: ").append(mealIds.get(i)).append("\n");
        }

        if (mealIds.size() > 5) {
            message.append("... and ").append(mealIds.size() - 5).append(" more");
        }

        showError(message.toString());

        SearchFragmentDirections.ActionSearchFragmentToFilterResultsFragment action =
                SearchFragmentDirections.actionSearchFragmentToFilterResultsFragment(mealsIds);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void setCategory(List<Category> categoryList) {
        searchCategoryAdapter.setLoading(false);
        searchCategoryAdapter.setCategories(categoryList);
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCountryChipClicked(Area area, boolean isSelected) {
        // Handle country chip selection
    }

    @Override
    public void onCategoryClicked(Category category, boolean isSelected) {
        // Handle category selection
    }

    @Override
    public void onIngredientClicked(Ingredients ingredient, boolean isSelected) {
        // Handle ingredient selection
    }

    @Override
    public void showSearchLoading() {
        searchButton.setEnabled(false);
        searchEditText.setEnabled(false);
    }

    @Override
    public void hideSearchLoading() {
        searchButton.setEnabled(true);
        searchEditText.setEnabled(true);
    }

    @Override
    public void showSearchResults(List<MealsByCategory> meals) {
        searchResultsAdapter.setMeals(meals);
        Toast.makeText(getContext(), "Found " + meals.size() + " meals", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMealClicked(MealsByCategory meal) {
        Toast.makeText(getContext(), "Clicked: " + meal.getMealName(), Toast.LENGTH_SHORT).show();
        SearchFragmentDirections.ActionSearchFragmentToRecipeDetailsFragment action =
                SearchFragmentDirections.actionSearchFragmentToRecipeDetailsFragment(meal.getMealId());
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}