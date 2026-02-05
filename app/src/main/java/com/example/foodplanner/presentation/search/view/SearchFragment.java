package com.example.foodplanner.presentation.search.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.foodplanner.R;
import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.search.area.Area;
import com.example.foodplanner.data.network.MealsService;
import com.example.foodplanner.data.network.Network;
import com.example.foodplanner.presentation.home.presenter.HomePresenter;
import com.example.foodplanner.presentation.multi_filter.MultiFilterPresenter;
import com.example.foodplanner.presentation.multi_filter.MultiFilterPresenterImp;
import com.example.foodplanner.presentation.multi_filter.MultiFilterView;
import com.example.foodplanner.presentation.search.presenter.area.AreaPresenter;
import com.example.foodplanner.presentation.search.presenter.area.AreaPresenterImp;
import com.example.foodplanner.presentation.search.presenter.category.CategoryPresenter;
import com.example.foodplanner.presentation.search.presenter.category.CategoryPresenterImp;
import com.example.foodplanner.presentation.search.view.area.AreaView;
import com.example.foodplanner.presentation.search.view.area.CountryChipAdapter;
import com.example.foodplanner.presentation.search.view.category.CategoryView;
import com.example.foodplanner.presentation.search.view.category.ChipCategoryAdapter;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements ChipCategoryAdapter.OnCategoryChipClickListener , AreaView, CountryChipAdapter.OnCountryChipClickListener , MultiFilterView  , CategoryView {

    private Button searchButton;
    private ChipGroup categoryChipGroup;
    private ChipGroup areaChipGroup;

    private ChipCategoryAdapter categoryChipAdapter;
    private CountryChipAdapter countryChipAdapter;
    private MealsRepository mealsRepository;
    private MealsService mealsService;
    private AreaPresenter areaPresenter;
    private CategoryPresenter categoryPresenter;
    private MultiFilterPresenter multiFilterPresenter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mealsRepository = new MealsRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchButton = view.findViewById(R.id.searchButton);
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup);
        areaChipGroup = view.findViewById(R.id.countryChipGroup);
        categoryChipAdapter = new ChipCategoryAdapter(requireContext(), categoryChipGroup, this);
        countryChipAdapter = new CountryChipAdapter(requireContext() , areaChipGroup , this);
         mealsService = Network.getInstance().getMealsService();
         categoryPresenter = new CategoryPresenterImp(this);
        areaPresenter = new AreaPresenterImp(this);
        multiFilterPresenter = new MultiFilterPresenterImp(this);
        areaPresenter.getArea();
        categoryPresenter.getCategory();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });


    }


    @Override
    public void onChipClicked(Category category, boolean isSelected) {
        // Optional: Show toast when category is selected
        // showToast((isSelected ? "Selected: " : "Deselected: ") + category.getCategoryName());
    }


    @Override
    public void setArea(List<Area> areaList) {
        countryChipAdapter.setCountries(areaList);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

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
        categoryChipAdapter.setCategories(categoryList);
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCountryChipClicked(Area area, boolean isSelected) {

    }

    private void performSearch() {
        // Get selected filters
        List<Category> selectedCategories = categoryChipAdapter.getSelectedCategories();
        List<Area> selectedAreas = countryChipAdapter.getSelectedAreas();

        // Convert to string lists
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

}