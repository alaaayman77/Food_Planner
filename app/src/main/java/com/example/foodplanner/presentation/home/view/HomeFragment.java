package com.example.foodplanner.presentation.home.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodplanner.MainActivity;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.category.Category;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.example.foodplanner.presentation.auth.SignInPromptDialog;
import com.example.foodplanner.presentation.home.presenter.HomePresenter;
import com.example.foodplanner.presentation.home.presenter.HomePresenterImp;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnCategoryClick, HomeView {
    private ImageView mealImage;
    private TextView mealTitle;
    private TextView tag1;
    private TextView tag2;
    private MaterialCardView randomMealCard;

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    private RandomMeal currentMeal;
    private HomePresenter homePresenter;
    private ImageView btnAddToPlan;
    private TextView categoriesHeader;
    private TextView categoriesSubtitle;
    private MaterialCardView favoriteCard;
    private ImageView favoriteIcon;

    private static final String TAG = "HomeFragment";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initViews(view);

        // Initialize presenter with LifecycleOwner
        homePresenter = new HomePresenterImp(this, requireContext(), getViewLifecycleOwner());

        // Setup click listeners
        setupClickListeners();

        // Setup RecyclerView
        setupRecyclerView();

        // Load data
        homePresenter.getRandomMeal();
        homePresenter.getCategory();
    }

    private void initViews(View view) {
        mealImage = view.findViewById(R.id.mealImage);
        mealTitle = view.findViewById(R.id.mealTitle);
        tag1 = view.findViewById(R.id.tag1);
        tag2 = view.findViewById(R.id.tag2);
        randomMealCard = view.findViewById(R.id.random_meal);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        btnAddToPlan = view.findViewById(R.id.btnAddToPlan);
        categoriesHeader = view.findViewById(R.id.categoriesHeader);
        categoriesSubtitle = view.findViewById(R.id.categoriesSubtitle);
        favoriteCard = view.findViewById(R.id.favoriteCard);
        favoriteIcon = view.findViewById(R.id.favoriteIcon);
    }

    private void setupRecyclerView() {
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this);
        categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupClickListeners() {
        // Random meal card click
        randomMealCard.setOnClickListener(v -> {
            if (currentMeal != null) {
                HomeFragmentDirections.ActionHomeFragmentToRecipeDetailsFragment action =
                        HomeFragmentDirections.actionHomeFragmentToRecipeDetailsFragment(currentMeal.getMealId());
                Navigation.findNavController(requireView()).navigate(action);
            }
        });

        // Add to plan button click
        btnAddToPlan.setOnClickListener(v -> {
            if (currentMeal != null) {
                showAddMealPlanDialog(currentMeal);
            } else {
                Toast.makeText(getContext(), "No meal available", Toast.LENGTH_SHORT).show();
            }
        });

        // Favorite button click
        favoriteCard.setOnClickListener(v -> {
            if (currentMeal != null) {
                homePresenter.onFavoriteClick(currentMeal);
            } else {
                Toast.makeText(getContext(), "No meal available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddMealPlanDialog(RandomMeal meal) {
        MealPlanBottomSheet bottomSheet = MealPlanBottomSheet.newInstance(meal.getMealId());
        bottomSheet.setOnMealPlanSelectedListener((selectedMealId, day, mealType) -> {
            // Create MealPlan with cached meal details
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
        categoryAdapter.setCategories(categoryList);
    }

    @Override
    public void showError(String errorMessage) {
        if (getContext() != null) {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
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
            Glide.with(this)
                    .load(meal.getMealThumbnail())
                    .placeholder(R.drawable.splash_bg)
                    .error(R.drawable.splash_bg)
                    .centerCrop()
                    .into(mealImage);
        }

        Log.d(TAG, "Meal displayed: " + meal.getMealName());
    }

    @Override
    public void OnCategoryClickSuccess(Category category) {
        Toast.makeText(getContext(),
                "Selected: " + category.getCategoryName(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMealPlanAddedSuccess() {
        Toast.makeText(getContext(),
                "Meal added to your plan! 📅",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showLoading();
        }
        randomMealCard.setVisibility(GONE);
        categoriesHeader.setVisibility(GONE);
        categoriesSubtitle.setVisibility(GONE);
        categoriesRecyclerView.setVisibility(GONE);
    }

    @Override
    public void hideLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideLoading();
        }
        randomMealCard.setVisibility(VISIBLE);
        categoriesHeader.setVisibility(VISIBLE);
        categoriesSubtitle.setVisibility(VISIBLE);
        categoriesRecyclerView.setVisibility(VISIBLE);
    }

    @Override
    public void onMealPlanAddedFailure(String error) {
        Toast.makeText(getContext(),
                "Failed to add meal: " + error,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavAddedSuccess() {
        Toast.makeText(getContext(),
                "Added to favorites! ❤️",
                Toast.LENGTH_SHORT).show();
        // Animate the heart
        animateHeart(favoriteIcon);
    }

    @Override
    public void onFavRemovedSuccess() {
        Toast.makeText(getContext(),
                "Removed from favorites",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavAddedFailure(String error) {
        Toast.makeText(getContext(),
                "Failed to add to favorites: " + error,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateFavoriteIcon(boolean isFavorite) {
        if (favoriteIcon != null) {
            if (isFavorite) {
                // Show filled heart
                favoriteIcon.setImageResource(R.drawable.icon_heart_filled);
            } else {
                // Show outline heart
                favoriteIcon.setImageResource(R.drawable.icon_heart);
            }
        }
    }

    @Override
    public void showSignInPrompt(String featureName, String message) {
        SignInPromptDialog dialog = SignInPromptDialog.newInstance(featureName, message);
        dialog.setListener(new SignInPromptDialog.SignInPromptListener() {
            @Override
            public void onSignInClicked() {
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

                Toast.makeText(getContext(),
                        "Continuing as guest. Your data won't be saved.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getParentFragmentManager(), "SignInPromptDialog");
    }

    private void animateHeart(ImageView heartIcon) {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(heartIcon, "scaleX", 1f, 1.3f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(heartIcon, "scaleY", 1f, 1.3f);

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartIcon, "scaleX", 1.3f, 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartIcon, "scaleY", 1.3f, 1f);

        scaleUpX.setDuration(150);
        scaleUpY.setDuration(150);
        scaleDownX.setDuration(150);
        scaleDownY.setDuration(150);

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(scaleUpX, scaleUpY);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.playTogether(scaleDownX, scaleDownY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleUp).before(scaleDown);
        animatorSet.start();
    }
}