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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.category.MealsByCategory;
import com.example.foodplanner.data.model.search.area.Area;
import com.example.foodplanner.data.model.search.ingredients.Ingredients;
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
import com.example.foodplanner.utility.NetworkUtils;
import com.google.android.material.button.MaterialButton;
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

    // UI Components
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
    private NestedScrollView mainContent;

    // Offline state views
    private ConstraintLayout offlineStateContainer;
    private MaterialButton retryButton;

    // Adapters
    private SearchCategoryAdapter searchCategoryAdapter;
    private SearchIngredientsAdapter searchIngredientsAdapter;
    private CountryChipAdapter countryChipAdapter;
    private SearchResultsAdapter searchResultsAdapter;

    // Presenters
    private AreaPresenter areaPresenter;
    private CategoryPresenter categoryPresenter;
    private IngredientsPresenter ingredientsPresenter;
    private MultiFilterPresenter multiFilterPresenter;
    private SearchPresenter searchPresenter;

    private String[] mealsIds;
    private PaginationManager<Ingredients> ingredientsPaginationManager;
    private static final int INGREDIENTS_PAGE_SIZE = 15;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
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
        checkNetworkAndLoadData();
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
        mainContent = view.findViewById(R.id.scrollView);


        offlineStateContainer = view.findViewById(R.id.offlineStateContainer);
        retryButton = view.findViewById(R.id.retryButton);
    }

    private void checkNetworkAndLoadData() {
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Log.d(TAG, "No network - showing offline state");
            showOfflineState();
        } else {
            Log.d(TAG, "Network available - loading data");
            hideOfflineState();
            loadInitialData();
        }
    }

    private void loadInitialData() {
        areaPresenter.getArea();
        categoryPresenter.getCategory();
        ingredientsPresenter.getIngredients();
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
                    if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                        safeShowToast("No internet connection. Please check your network.");
                        return;
                    }

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
            // Check network before filtering
            if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                safeShowToast("No internet connection. Please check your network.");
                return;
            }

            List<Category> selectedCategories = searchCategoryAdapter.getSelectedCategories();
            List<Area> selectedAreas = countryChipAdapter.getSelectedAreas();
            List<Ingredients> selectedIngredients = searchIngredientsAdapter.getSelectedIngredients();

            if (selectedCategories.isEmpty() && selectedAreas.isEmpty() && selectedIngredients.isEmpty()) {
                safeShowToast("Please select at least one filter");
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

            isFilterMode = false;
            clearAllFilters();
            enableSearch();
            showFilterUI();
        });

        // Retry Button (Offline State)
        if (retryButton != null) {
            retryButton.setOnClickListener(v -> {
                Log.d(TAG, "Retry button clicked");
                animateRetryButton();
                retryConnection();
            });
        }
    }

    private void retryConnection() {
        boolean hasNetwork = NetworkUtils.isNetworkAvailable(requireContext());
        Log.d(TAG, "Retry - Network available: " + hasNetwork);

        if (hasNetwork) {
            hideOfflineState();
            loadInitialData();
        } else {
            safeShowToast("Still offline. Please check your connection.");
        }
    }

    private void showOfflineState() {
        if (mainContent != null) {
            mainContent.setVisibility(View.GONE);
        }
        if (offlineStateContainer != null) {
            offlineStateContainer.setVisibility(View.VISIBLE);
        }
    }

    private void hideOfflineState() {
        if (mainContent != null) {
            mainContent.setVisibility(View.VISIBLE);
        }
        if (offlineStateContainer != null) {
            offlineStateContainer.setVisibility(View.GONE);
        }
    }

    private void animateRetryButton() {
        if (retryButton != null) {
            retryButton.setEnabled(false);

            android.animation.ObjectAnimator rotation =
                    android.animation.ObjectAnimator.ofFloat(retryButton, "rotation", 0f, 360f);
            rotation.setDuration(500);
            rotation.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    if (retryButton != null) {
                        retryButton.setEnabled(true);
                        retryButton.setRotation(0f);
                    }
                }
            });
            rotation.start();
        }
    }

    private boolean hasAnyFiltersSelected() {
        List<Category> selectedCategories = searchCategoryAdapter.getSelectedCategories();
        List<Area> selectedAreas = countryChipAdapter.getSelectedAreas();
        List<Ingredients> selectedIngredients = searchIngredientsAdapter.getSelectedIngredients();

        return !selectedCategories.isEmpty() || !selectedAreas.isEmpty() || !selectedIngredients.isEmpty();
    }

    private void updateFilterMode() {
        boolean hasFilters = hasAnyFiltersSelected();

        if (hasFilters && !isFilterMode) {
            isFilterMode = true;
            disableSearch();
            if (searchEditText.getText().length() > 0) {
                searchEditText.setText("");
            }
            Log.d(TAG, "Filter selected - search disabled");
        } else if (!hasFilters && isFilterMode) {
            isFilterMode = false;
            enableSearch();
            Log.d(TAG, "No filters selected - search enabled");
        }
    }

    private void disableSearch() {
        searchEditText.setEnabled(false);
        searchEditText.setAlpha(0.5f);
        searchEditText.setHint("Filters active - clear filters to search by name");
    }

    private void enableSearch() {
        searchEditText.setEnabled(true);
        searchEditText.setAlpha(1.0f);
        searchEditText.setHint("Search recipes...");
    }

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

    /**
     * Safely show toast - only if fragment is attached
     */
    private void safeShowToast(String message) {
        if (isAdded() && getContext() != null) {
            try {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Failed to show toast: " + e.getMessage());
            }
        } else {
            Log.w(TAG, "Cannot show toast - fragment not attached: " + message);
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
            safeShowToast("No meals found with selected filters");
            return;
        }

        safeShowToast("Found " + mealIds.size() + " meals!");

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
        safeShowToast(errorMessage);
    }

    @Override
    public void onCountryChipClicked(Area area, boolean isSelected) {
        updateFilterMode();
    }

    @Override
    public void onCategoryClicked(Category category, boolean isSelected) {
        updateFilterMode();
    }

    @Override
    public void onIngredientClicked(Ingredients ingredient, boolean isSelected) {
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
            safeShowToast("Showing " + loaded + " of " + total + " results");
        }
    }

    @Override
    public void onMealClicked(MealsByCategory meal) {
        safeShowToast("Clicked: " + meal.getMealName());
        SearchFragmentDirections.ActionSearchFragmentToRecipeDetailsFragment action =
                SearchFragmentDirections.actionSearchFragmentToRecipeDetailsFragment(meal.getMealId());
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onLoadMoreClicked() {
        searchPresenter.loadMoreResults();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.clear();
    }
}