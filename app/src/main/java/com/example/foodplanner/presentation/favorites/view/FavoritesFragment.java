package com.example.foodplanner.presentation.favorites.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.favoriteMeal.FavoriteMeal;
import com.example.foodplanner.presentation.favorites.presenter.FavPresenter;
import com.example.foodplanner.presentation.favorites.presenter.FavPresenterImp;


import java.util.List;

public class FavoritesFragment extends Fragment implements FavView, FavoritesAdapter.OnFavoriteActionListener {


    private RecyclerView favoritesRecyclerView;
    private LinearLayout emptyStateLayout;
    private ProgressBar loadingProgress;
    private TextView favoritesCount;

    // MVP
    private FavPresenter presenter;
    private FavoritesAdapter adapter;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        setupRecyclerView();

        presenter = new FavPresenterImp(this, requireContext(), getViewLifecycleOwner());


        presenter.loadFavorites();
    }

    private void initViews(View view) {
        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        loadingProgress = view.findViewById(R.id.loadingProgress);
        favoritesCount = view.findViewById(R.id.favoritesCount);
    }

    private void setupRecyclerView() {
        adapter = new FavoritesAdapter(this);
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        favoritesRecyclerView.setAdapter(adapter);
    }



    @Override
    public void showFavorites(List<FavoriteMeal> favorites) {
        adapter.setFavorites(favorites);
        favoritesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyState() {
        emptyStateLayout.setVisibility(View.VISIBLE);
        favoritesRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideEmptyState() {
        emptyStateLayout.setVisibility(View.GONE);
        favoritesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        loadingProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingProgress.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateFavoritesCount(int count) {
        favoritesCount.setText(String.valueOf(count));
    }

    @Override
    public void onFavoriteRemoved(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToMealDetails(FavoriteMeal favoriteMeal) {

    }



    @Override
    public void onFavoriteClick(FavoriteMeal favoriteMeal) {
        presenter.onFavoriteClick(favoriteMeal);
    }

    @Override
    public void onRemoveFavoriteClick(FavoriteMeal favoriteMeal) {
        presenter.removeFavorite(favoriteMeal.getMealId());
    }
}