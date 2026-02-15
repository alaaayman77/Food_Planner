package com.example.foodplanner.presentation.onboarding;



import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.foodplanner.R;
import com.example.foodplanner.data.model.onboarding.OnBoarding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class OnBoarding1Fragment extends Fragment {


    private ViewPager2     viewPager;
    private TabLayout      dotsIndicator;
    private MaterialButton btnNext;
    private TextView       tvSkip;


    private List<OnBoarding> pages;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pages = Arrays.asList(
                new OnBoarding(
                        R.raw.sushi,
                        "Discover Recipes",
                        "Explore thousands of delicious recipes from cuisines all around the world."
                ),
                new OnBoarding(
                        R.raw.avocado,
                        "Plan Your Meals",
                        "Organise your weekly meals with a personal planner built just for you."
                ),
                new OnBoarding(
                        R.raw.recipe_save,
                        "Save Your Favorites",
                        "Bookmark any recipe and access it offline whenever you need it."
                )
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the HOST layout (ViewPager2 + dots + buttons)
        return inflater.inflate(R.layout.fragment_on_boarding1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager     = view.findViewById(R.id.viewPager);
        dotsIndicator = view.findViewById(R.id.dotsIndicator);
        btnNext       = view.findViewById(R.id.btnNext);
        tvSkip        = view.findViewById(R.id.tvSkip);

        setupViewPager();
        setupDots();
        setupButtons();
    }

    private void setupViewPager() {
        OnBoardingAdapter adapter = new OnBoardingAdapter(pages);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateButtonLabel(position);
            }
        });
    }


    private void setupDots() {
        new TabLayoutMediator(dotsIndicator, viewPager,
                (tab, position) -> { }
        ).attach();
    }


    private void setupButtons() {

        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < pages.size() - 1) {

                viewPager.setCurrentItem(current + 1);
            } else {

                navigateToAuth();
            }
        });

        tvSkip.setOnClickListener(v -> navigateToAuth());
    }

    private void updateButtonLabel(int position) {
        if (position == pages.size() - 1) {
            btnNext.setText("Get Started");
            tvSkip.setVisibility(View.INVISIBLE);
        } else {
            btnNext.setText("Next");
            tvSkip.setVisibility(View.VISIBLE);
        }
    }

    private void navigateToAuth() {

        requireContext()
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("onboarding_complete", true)
                .apply();

        Navigation.findNavController(requireView())
                .navigate(R.id.action_onboardingFragment_to_authFragment);
    }
}