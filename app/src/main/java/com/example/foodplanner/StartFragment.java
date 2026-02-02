package com.example.foodplanner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StartFragment extends Fragment {

    NavController navController;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton mealFAB;
    BottomAppBar bottomAppBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        bottomAppBar = view.findViewById(R.id.bottomAppBar);
        mealFAB = view.findViewById(R.id.mealFab);
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_container);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.categoryFragment ||destination.getId() == R.id.recipeDetailsFragment) {
                bottomNavigationView.setVisibility(View.GONE);
                bottomAppBar.setVisibility(View.GONE);
                bottomAppBar.performHide();
                mealFAB.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
                bottomAppBar.setVisibility(View.VISIBLE);
                bottomAppBar.performShow();
                mealFAB.setVisibility(View.VISIBLE);
            }
        });
        mealFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.setSelectedItemId(R.id.mealPlannerFragment);
            }
        });
    }
}