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
        SearchResultsAdapter.OnMealClickListener,
        SearchResultsAdapter.OnLoadMoreClickListener {

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

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Add filter mode flag
    private boolean isFilterMode = false;

    private static final String TAG = "SearchFragment";

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

        initViews(view);
        setupAdapters();
        setupPresenters();
        setupSearchObservable();
        setupClickListeners();

        // Load initial data
        areaPresenter.getArea();
        categoryPresenter.getCategory();
        ingredientsPresenter.getIngredients();
    }

    private void initViews(View view) {
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
    }

    private void setupAdapters() {
        searchCategoryAdapter = new SearchCategoryAdapter(requireContext(), this);
        searchIngredientsAdapter = new SearchIngredientsAdapter(requireContext(), this);
        countryChipAdapter = new CountryChipAdapter(requireContext(), areaChipGroup, this);
        searchResultsAdapter = new SearchResultsAdapter(requireContext(), this);

        searchResultsAdapter.setOnLoadMoreClickListener(this);

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
    }

    private void setupPresenters() {
        categoryPresenter = new CategoryPresenterImp(this, requireContext());
        areaPresenter = new AreaPresenterImp(this, requireContext());
        ingredientsPresenter = new IngredientsPresenterImp(this, requireContext());
        multiFilterPresenter = new MultiFilterPresenterImp(this, requireContext());
        searchPresenter = new SearchPresenterImp(this, requireContext());
    }

    private void setupSearchObservable() {
        Observable<String> searchObservable = Observable.create(emitter -> {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Don't process text changes if in filter mode
                    if (isFilterMode) {
                        return;
                    }

                    if (s.length() > 0) {
                        clearSearchButton.setVisibility(View.VISIBLE);
                    } else {
                        clearSearchButton.setVisibility(View.GONE);
                        showFilterUI();
                    }
                    emitter.onNext(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        });

        Disposable searchDisposable = searchObservable
                .debounce(1, TimeUnit.SECONDS)
                .filter(query -> !isFilterMode && query.length() > 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(query -> {
                    hideFilterUI();
                    searchResultsRecyclerView.setVisibility(View.VISIBLE);
                    searchPresenter.searchMealsByName(query);
                }, error -> {
                    Log.e(TAG, "Error in search observable", error);
                });

        compositeDisposable.add(searchDisposable);
    }

    private void setupClickListeners() {
        // Apply Filters Button
        searchButton.setOnClickListener(v -> {
            List<Category> selectedCategories = searchCategoryAdapter.getSelectedCategories();
            List<Area> selectedAreas = countryChipAdapter.getSelectedAreas();
            List<Ingredients> selectedIngredients = searchIngredientsAdapter.getSelectedIngredients();

            // Check if any filters are selected
            if (selectedCategories.isEmpty() && selectedAreas.isEmpty() && selectedIngredients.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one filter", Toast.LENGTH_SHORT).show();
                return;
            }

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

        // Clear Search Button
        clearSearchButton.setOnClickListener(v -> {
            searchEditText.setText("");
            searchResultsAdapter.clearMeals();

            // Reset filter mode
            isFilterMode = false;
            clearAllFilters();
            enableSearch();
            showFilterUI();
        });
    }

    /**
     * Check if any filters are currently selected
     */
    private boolean hasAnyFiltersSelected() {
        List<Category> selectedCategories = searchCategoryAdapter.getSelectedCategories();
        List<Area> selectedAreas = countryChipAdapter.getSelectedAreas();
        List<Ingredients> selectedIngredients = searchIngredientsAdapter.getSelectedIngredients();

        return !selectedCategories.isEmpty() || !selectedAreas.isEmpty() || !selectedIngredients.isEmpty();
    }

    /**
     * Update filter mode based on current selections
     */
    private void updateFilterMode() {
        boolean hasFilters = hasAnyFiltersSelected();

        if (hasFilters && !isFilterMode) {
            // Filters just got selected - disable search
            isFilterMode = true;
            disableSearch();
            // Clear any text search
            if (searchEditText.getText().length() > 0) {
                searchEditText.setText("");
            }
            Log.d(TAG, "Filter selected - search disabled");
        } else if (!hasFilters && isFilterMode) {
            // All filters cleared - enable search
            isFilterMode = false;
            enableSearch();
            Log.d(TAG, "No filters selected - search enabled");
        }
    }

    /**
     * Disable search input when filters are active
     */
    private void disableSearch() {
        searchEditText.setEnabled(false);
        searchEditText.setAlpha(0.5f);
        searchEditText.setHint("Filters active - clear filters to search by name");
    }

    /**
     * Enable search input
     */
    private void enableSearch() {
        searchEditText.setEnabled(true);
        searchEditText.setAlpha(1.0f);
        searchEditText.setHint("Search recipes...");
    }

    /**
     * Hide filter UI elements (when searching by text)
     */
    private void hideFilterUI() {
        browseByCategoryTitle.setVisibility(View.GONE);
        categoryRecyclerView.setVisibility(View.GONE);
        exploreByCountryTitle.setVisibility(View.GONE);
        viewAllCountries.setVisibility(View.GONE);
        areaChipGroup.setVisibility(View.GONE);
        searchByIngredientTitle.setVisibility(View.GONE);
        seeAllIngredients.setVisibility(View.GONE);
        ingredientsRecyclerView.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
    }

    /**
     * Show filter UI elements (default state)
     */
    private void showFilterUI() {
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

    /**
     * Clear all filter selections
     */
    private void clearAllFilters() {
        if (searchCategoryAdapter != null) {
            searchCategoryAdapter.clearSelections();
        }
        if (countryChipAdapter != null) {
            countryChipAdapter.clearSelections();
        }
        if (searchIngredientsAdapter != null) {
            searchIngredientsAdapter.clearSelections();
        }
    }

    // ==================== Interface Implementations ====================

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

        if (mealIds.isEmpty()) {
            Toast.makeText(getContext(), "No meals found with selected filters", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Found " + mealIds.size() + " meals!", Toast.LENGTH_SHORT).show();

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
        // Update filter mode immediately when chip is clicked
        updateFilterMode();
    }

    @Override
    public void onCategoryClicked(Category category, boolean isSelected) {
        // Update filter mode immediately when category is clicked
        updateFilterMode();
    }

    @Override
    public void onIngredientClicked(Ingredients ingredient, boolean isSelected) {
        // Update filter mode immediately when ingredient is clicked
        updateFilterMode();
    }

    @Override
    public void showSearchLoading() {
        searchButton.setEnabled(false);
        searchEditText.setEnabled(false);
    }

    @Override
    public void hideSearchLoading() {
        searchButton.setEnabled(true);
        if (!isFilterMode) {
            searchEditText.setEnabled(true);
        }
    }

    @Override
    public void showSearchResults(List<MealsByCategory> meals) {
        searchResultsAdapter.setMeals(meals);
    }

    @Override
    public void appendSearchResults(List<MealsByCategory> meals) {
        searchResultsAdapter.addMeals(meals);
    }

    @Override
    public void updateSearchInfo(int loaded, int total, boolean hasMore) {
        searchResultsAdapter.setLoadMoreVisible(hasMore, total);
        if (loaded > 0) {
            Toast.makeText(getContext(), "Showing " + loaded + " of " + total + " results", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMealClicked(MealsByCategory meal) {
        Toast.makeText(getContext(), "Clicked: " + meal.getMealName(), Toast.LENGTH_SHORT).show();
        SearchFragmentDirections.ActionSearchFragmentToRecipeDetailsFragment action =
                SearchFragmentDirections.actionSearchFragmentToRecipeDetailsFragment(meal.getMealId());
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onLoadMoreClicked() {
        searchPresenter.loadMoreResults();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
