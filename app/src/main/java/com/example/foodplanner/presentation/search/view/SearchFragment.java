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
import com.example.foodplanner.data.model.category.CategoryResponse;
import com.example.foodplanner.data.model.search.area.Area;
import com.example.foodplanner.data.network.MealsService;
import com.example.foodplanner.data.network.Network;
import com.example.foodplanner.presentation.home.presenter.HomePresenterImp;
import com.example.foodplanner.presentation.search.presenter.AreaPresenter;
import com.example.foodplanner.presentation.search.presenter.AreaPresenterImp;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements ChipCategoryAdapter.OnCategoryChipClickListener , AreaView , CountryChipAdapter.OnCountryChipClickListener{

    private Button searchButton;
    private ChipGroup categoryChipGroup;
    private ChipGroup areaChipGroup;

    private ChipCategoryAdapter categoryChipAdapter;
    private CountryChipAdapter countryChipAdapter;
    private MealsRepository mealsRepository;
    private MealsService mealsService;
    private AreaPresenter areaPresenter;

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
        areaPresenter = new AreaPresenterImp(this);
        getCategoryForSearch();
        areaPresenter.getArea();


    }

    private void getCategoryForSearch() {
        mealsService.getCategory().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if(response.isSuccessful() && response.body()!=null){
                    List<Category> categoriesList = response.body().getCategories();
                    if(categoriesList!=null){
                        categoryChipAdapter.setCategories(categoriesList);
                    }
                    else{
                        showToast("Failed to load categories");
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                showToast("Network error " + t.getMessage());
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
    public void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCountryChipClicked(Area area, boolean isSelected) {

    }



}