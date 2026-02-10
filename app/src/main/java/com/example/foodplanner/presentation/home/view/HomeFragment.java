package com.example.foodplanner.presentation.home.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodplanner.MainActivity;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.data.model.search.area.Area;
import com.example.foodplanner.presentation.auth.SignInPromptDialog;
import com.example.foodplanner.presentation.home.presenter.HomePresenterImp;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HomeFragment extends Fragment implements OnCategoryClick, HomeView, OnCountryClick {
    private ImageView mealImage;
    private TextView mealTitle;
    private TextView tag1;
    private TextView tag2;
    private MaterialCardView randomMealCard;

    private RecyclerView categoriesRecyclerView;
    private RecyclerView countriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private CountryAdapter countryAdapter;

    private RandomMeal currentMeal;
    private HomePresenterImp homePresenter;
    private ImageView btnAddToPlan;
    private ImageView favoriteIcon;

    // Main content and offline state containers
    private ScrollView mainContent;
    private ConstraintLayout offlineStateContainer;
    private MaterialButton retryButton;

    private static final String TAG = "HomeFragment";

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        homePresenter = new HomePresenterImp(this, requireContext(), getViewLifecycleOwner());
        setupClickListeners();
        setupRecyclerView();

        // Load data
        homePresenter.getRandomMeal();
        homePresenter.getCategory();
        homePresenter.getArea();
    }

    private void initViews(View view) {
        mainContent = view.findViewById(R.id.mainContent);

        mealImage = view.findViewById(R.id.mealImage);
        mealTitle = view.findViewById(R.id.mealTitle);
        tag1 = view.findViewById(R.id.tag1);
        tag2 = view.findViewById(R.id.tag2);
        randomMealCard = view.findViewById(R.id.random_meal);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        countriesRecyclerView = view.findViewById(R.id.countries_recycler_view);
        btnAddToPlan = view.findViewById(R.id.btnAddToPlan);
        favoriteIcon = view.findViewById(R.id.favoriteIcon);

        offlineStateContainer = view.findViewById(R.id.offlineStateContainer);
        retryButton = view.findViewById(R.id.retryButton);
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(this);
        categoriesRecyclerView.setAdapter(categoryAdapter);
        countryAdapter = new CountryAdapter(this);
        countriesRecyclerView.setAdapter(countryAdapter);
    }

    private void setupClickListeners() {
        randomMealCard.setOnClickListener(v -> {
            if (currentMeal != null) {
                HomeFragmentDirections.ActionHomeFragmentToRecipeDetailsFragment action =
                        HomeFragmentDirections.actionHomeFragmentToRecipeDetailsFragment(currentMeal.getMealId());
                Navigation.findNavController(requireView()).navigate(action);
            }
        });

        btnAddToPlan.setOnClickListener(v -> {
            if (currentMeal != null) {
                showAddMealPlanDialog(currentMeal);
            } else {
                safeShowToast("No meal available");
            }
        });

        favoriteIcon.setOnClickListener(v -> {
            if (currentMeal != null) {
                homePresenter.onFavoriteClick(currentMeal);
            } else {
                safeShowToast("No meal available");
            }
        });

        if (retryButton != null) {
            retryButton.setOnClickListener(v -> {
                Log.d(TAG, "Retry button clicked");
                animateRetryButton();
                homePresenter.retryConnection();
            });
        }
    }

    private void showAddMealPlanDialog(RandomMeal meal) {
        MealPlanBottomSheet bottomSheet = MealPlanBottomSheet.newInstance(meal.getMealId());
        bottomSheet.setOnMealPlanSelectedListener((selectedMealId, day, mealType) -> {
            MealPlan mealPlan = new MealPlan(
                    meal.getMealId(),
                    mealType,
                    day,
                    meal.getMealName(),
                    meal.getMealThumbnail(),
                    meal.getMealCategory(),
                    meal.getStrArea(),
                    meal.getMealInstructions()
            );
            homePresenter.addMealToPlan(mealPlan);
        });
        bottomSheet.show(getParentFragmentManager(), "AddMealPlanBottomSheet");
    }

    @Override
    public void setOnCategoryClick(Category category) {
        HomeFragmentDirections.ActionHomeFragmentToMealsByCategoryFragment action =
                HomeFragmentDirections.actionHomeFragmentToMealsByCategoryFragment(category.getCategoryName());
        Navigation.findNavController(requireView()).navigate(action);
        homePresenter.onCategoryClick(category);
    }

    @Override
    public void setCategoryList(List<Category> categoryList) {
        if (categoryAdapter != null) {
            categoryAdapter.setCategories(categoryList);
        }
    }



    @Override
    public void showError(String errorMessage) {
        safeShowToast(errorMessage);
    }

    @Override
    public void displayMeal(RandomMeal meal) {
        currentMeal = meal;
        mealTitle.setText(meal.getMealName());

        if (meal.getMealCategory() != null) {
            tag1.setText(meal.getMealCategory().toUpperCase());
            tag1.setVisibility(VISIBLE);
        } else {
            tag1.setVisibility(GONE);
        }

        if (meal.getStrArea() != null) {
            tag2.setText(meal.getStrArea().toUpperCase());
            tag2.setVisibility(VISIBLE);
        } else {
            tag2.setVisibility(GONE);
        }

        if (meal.getMealThumbnail() != null && !meal.getMealThumbnail().isEmpty()) {
            if (isAdded() && getContext() != null) {
                Glide.with(this)
                        .load(meal.getMealThumbnail())
                        .placeholder(R.drawable.splash_bg)
                        .error(R.drawable.splash_bg)
                        .centerCrop()
                        .into(mealImage);
            }
        }

        Log.d(TAG, "Meal displayed: " + meal.getMealName());
    }

    @Override
    public void OnCategoryClickSuccess(Category category) {
        safeShowToast("Selected: " + category.getCategoryName());
    }

    @Override
    public void onMealPlanAddedSuccess() {
        safeShowToast("Meal added to your plan! 📅");
    }

    @Override
    public void showLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showLoading();
        }
    }

    @Override
    public void hideLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideLoading();
        }
    }

    @Override
    public void onMealPlanAddedFailure(String error) {
        safeShowToast("Failed to add meal: " + error);
    }

    @Override
    public void onFavAddedSuccess() {
        safeShowToast("Added to favorites! ❤️");
    }

    @Override
    public void onFavRemovedSuccess() {
        safeShowToast("Removed from favorites");
    }

    @Override
    public void onFavAddedFailure(String error) {
        safeShowToast("Failed to add to favorites: " + error);
    }

    @Override
    public void updateFavoriteIcon(boolean isFavorite) {
        if (favoriteIcon != null) {
            if (isFavorite) {
                favoriteIcon.setImageResource(R.drawable.icon_heart_filled);
            } else {
                favoriteIcon.setImageResource(R.drawable.icon_heart);
            }
        }
    }

    @Override
    public void showSignInPrompt(String featureName, String message) {
        if (!isAdded()) return;

        SignInPromptDialog dialog = SignInPromptDialog.newInstance(featureName, message);
        dialog.setListener(new SignInPromptDialog.SignInPromptListener() {
            @Override
            public void onSignInClicked() {
                if (!isAdded()) return;

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
                safeShowToast("Continuing as guest. Your data won't be saved.");
            }
        });
        dialog.show(getParentFragmentManager(), "SignInPromptDialog");
    }

    @Override
    public void setArea(List<Area> areaList) {
        if (countryAdapter != null) {
            countryAdapter.setCountries(areaList);
        }
    }

    @Override
    public void OnAreaClickSuccess(Area area) {
        safeShowToast("Selected: " + area.getStrArea());
    }

    @Override
    public void showOfflineBanner() {
        Log.d(TAG, "Showing offline state");
        if (mainContent != null) {
            mainContent.setVisibility(GONE);
        }
        if (offlineStateContainer != null) {
            offlineStateContainer.setVisibility(VISIBLE);
        }
    }

    @Override
    public void hideOfflineBanner() {
        Log.d(TAG, "Hiding offline state");
        if (mainContent != null) {
            mainContent.setVisibility(VISIBLE);
        }
        if (offlineStateContainer != null) {
            offlineStateContainer.setVisibility(GONE);
        }
    }

    @Override
    public void showOfflineMessage(String message) {
        safeShowToast(message);
    }

    /**
     * Safely show toast - only if fragment is attached to context
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

    private void animateRetryButton() {
        if (retryButton != null) {
            retryButton.setEnabled(false);

            ObjectAnimator rotation = ObjectAnimator.ofFloat(retryButton, "rotation", 0f, 360f);
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

    @Override
    public void onCountryClick(Area area) {
        HomeFragmentDirections.ActionHomeFragmentToMealsByCountryFragment action =
                HomeFragmentDirections.actionHomeFragmentToMealsByCountryFragment(area.getStrArea());
        Navigation.findNavController(requireView()).navigate(action);
        homePresenter.onAreaClick(area);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (homePresenter != null) {
            homePresenter.onDestroy();
        }
    }
}