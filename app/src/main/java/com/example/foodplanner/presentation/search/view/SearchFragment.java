package com.example.foodplanner.presentation.search.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.category.Category;
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

public class SearchFragment extends Fragment implements
        SearchCategoryAdapter.OnCategoryClickListener,
        AreaView,
        CountryChipAdapter.OnCountryChipClickListener,
        MultiFilterView,
        CategoryView,
        IngredientsView,
        SearchIngredientsAdapter.OnIngredientClickListener {

    private Button searchButton;
    private RecyclerView categoryRecyclerView;
    private RecyclerView ingredientsRecyclerView;
    private ChipGroup areaChipGroup;
    private TextView seeAllIngredients;

    private SearchCategoryAdapter searchCategoryAdapter;
    private SearchIngredientsAdapter searchIngredientsAdapter;
    private CountryChipAdapter countryChipAdapter;

    private AreaPresenter areaPresenter;
    private CategoryPresenter categoryPresenter;
    private IngredientsPresenter ingredientsPresenter;
    private MultiFilterPresenter multiFilterPresenter;

    // Pagination
    private PaginationManager<Ingredients> ingredientsPaginationManager;
    private static final int INGREDIENTS_PAGE_SIZE = 15; // Load 15 at a time

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

        // Initialize views
        searchButton = view.findViewById(R.id.searchButton);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        ingredientsRecyclerView = view.findViewById(R.id.ingredientRecyclerView);
        areaChipGroup = view.findViewById(R.id.countryChipGroup);
        seeAllIngredients = view.findViewById(R.id.seeAllIngredients);

        // Setup adapters
        searchCategoryAdapter = new SearchCategoryAdapter(requireContext(), this);
        searchIngredientsAdapter = new SearchIngredientsAdapter(requireContext(), this);

        // Setup RecyclerViews
        categoryRecyclerView.setAdapter(searchCategoryAdapter);

        LinearLayoutManager ingredientsLayoutManager = new LinearLayoutManager(requireContext());
        ingredientsRecyclerView.setLayoutManager(ingredientsLayoutManager);
        ingredientsRecyclerView.setAdapter(searchIngredientsAdapter);
        ingredientsRecyclerView.setNestedScrollingEnabled(false);

        // Setup See More button click listener in adapter
        searchIngredientsAdapter.setOnSeeMoreClickListener(() -> loadMoreIngredients());

        countryChipAdapter = new CountryChipAdapter(requireContext(), areaChipGroup, this);

        // Initialize presenters
        categoryPresenter = new CategoryPresenterImp(this);
        areaPresenter = new AreaPresenterImp(this);
        ingredientsPresenter = new IngredientsPresenterImp(this);
        multiFilterPresenter = new MultiFilterPresenterImp(this);

        // Load data
        areaPresenter.getArea();
        categoryPresenter.getCategory();
        ingredientsPresenter.getIngredients();

        // Setup click listeners
        searchButton.setOnClickListener(v -> performSearch());
    }

    @Override
    public void setIngredients(List<Ingredients> ingredientsList) {
        // Store all ingredients in pagination manager
        ingredientsPaginationManager.setAllItems(ingredientsList);

        // Clear existing items
        searchIngredientsAdapter.clearIngredients();

        // Load first page
        loadMoreIngredients();

        // Update counter
        updateIngredientsCounter();
    }

    private void loadMoreIngredients() {
        // Get next page
        List<Ingredients> nextPage = ingredientsPaginationManager.getNextPage();

        // Add items
        if (!nextPage.isEmpty()) {
            searchIngredientsAdapter.addIngredients(nextPage);
        }

        // Update See More button visibility
        boolean hasMore = ingredientsPaginationManager.hasMorePages();
        searchIngredientsAdapter.setSeeMoreVisible(hasMore);

        // Update counter
        updateIngredientsCounter();
    }

    private void updateIngredientsCounter() {
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
    }

    @Override
    public void setCategory(List<Category> categoryList) {
        searchCategoryAdapter.setCategories(categoryList);
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCountryChipClicked(Area area, boolean isSelected) {
        // Optional feedback
    }

    private void performSearch() {
        List<Category> selectedCategories = searchCategoryAdapter.getSelectedCategories();
        List<Area> selectedAreas = countryChipAdapter.getSelectedAreas();

        List<String> categoryNames = new ArrayList<>();
        for (Category category : selectedCategories) {
            categoryNames.add(category.getCategoryName());
        }

        List<String> areaNames = new ArrayList<>();
        for (Area area : selectedAreas) {
            areaNames.add(area.getStrArea());
        }

        multiFilterPresenter.searchWithFilters(categoryNames, areaNames);
    }

    @Override
    public void onCategoryClicked(Category category, boolean isSelected) {
        // Optional feedback
    }

    @Override
    public void onIngredientClicked(Ingredients ingredient, boolean isSelected) {
        // Optional feedback
    }
}