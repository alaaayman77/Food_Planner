package com.example.foodplanner.presentation.recipe_details.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.recipe_details.IngredientWithMeasure;
import com.example.foodplanner.data.model.recipe_details.InstructionStep;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.auth.SignInPromptDialog;
import com.example.foodplanner.presentation.home.view.MealPlanBottomSheet;
import com.example.foodplanner.presentation.recipe_details.presenter.RecipeDetailsPresenter;
import com.example.foodplanner.presentation.recipe_details.presenter.RecipeDetailsPresenterImp;

import com.example.foodplanner.utility.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailsFragment extends Fragment implements RecipeDetailsView {

    private String idMeal;
    private ImageView mealImage;
    private TextView mealTitle;
    private TextView mealArea;
    private TextView mealType;
    private TextView itemCountTextView;
    private RecyclerView ingredientsRecyclerView;
    private RecyclerView instructionsRecyclerView;
    private MaterialCardView cachedDataBanner;
    private TextView cachedDataText;

    private IngredientsAdapter ingredientsAdapter;
    private InstructionsAdapter instructionsAdapter;
    private RecipeDetailsPresenterImp recipeDetailsPresenter;
    private MaterialButton backBtn;
    private MaterialButton addToMealPlanButton;
    private YouTubePlayerView youTubePlayerView;

    // Offline state views
    private ConstraintLayout offlineStateContainer;
    private MaterialButton retryButton;
    private TextView offlineMessage;

    private RecipeDetails currentRecipeDetails;
    private static final String TAG = "RecipeDetailsFragment";

    public RecipeDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            RecipeDetailsFragmentArgs args = RecipeDetailsFragmentArgs.fromBundle(getArguments());
            idMeal = args.getIdMeal();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerViews();

        recipeDetailsPresenter = new RecipeDetailsPresenterImp(this, requireContext(), getViewLifecycleOwner());
        recipeDetailsPresenter.getRecipeDetails(idMeal);

        backBtn.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        addToMealPlanButton.setOnClickListener(v -> {
            if (currentRecipeDetails != null) {
                showAddMealPlanDialog(currentRecipeDetails);
            } else {
                safeShowToast("No meal available");
            }
        });

        // Retry button for offline state
        if (retryButton != null) {
            retryButton.setOnClickListener(v -> {
                animateRetryButton();
                retryConnection();
            });
        }
    }

    private void initializeViews(View view) {
        mealImage = view.findViewById(R.id.mealImage_Details);
        mealTitle = view.findViewById(R.id.mealTitle_Details);
        mealArea = view.findViewById(R.id.tag1);
        mealType = view.findViewById(R.id.tag2);
        backBtn = view.findViewById(R.id.back_button);
        addToMealPlanButton = view.findViewById(R.id.addToMealPlanButton);
        itemCountTextView = view.findViewById(R.id.itemCountTextView);
        ingredientsRecyclerView = view.findViewById(R.id.ingredientsRecyclerView);
        instructionsRecyclerView = view.findViewById(R.id.instructionsRecyclerView);
        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        cachedDataBanner = view.findViewById(R.id.cachedDataBanner);
        cachedDataText = view.findViewById(R.id.cachedDataText);

        // Offline state views
        offlineStateContainer = view.findViewById(R.id.offlineStateContainer);
        retryButton = view.findViewById(R.id.retryButton);
        offlineMessage = view.findViewById(R.id.offlineMessage);

        getLifecycle().addObserver(youTubePlayerView);
    }

    private void setupRecyclerViews() {
        ingredientsAdapter = new IngredientsAdapter(requireContext());
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        LinearLayoutManager instructionsLayoutManager = new LinearLayoutManager(requireContext());
        instructionsRecyclerView.setLayoutManager(instructionsLayoutManager);
        instructionsAdapter = new InstructionsAdapter();
        instructionsRecyclerView.setAdapter(instructionsAdapter);
    }

    private void retryConnection() {
        boolean hasNetwork = NetworkUtils.isNetworkAvailable(requireContext());
        Log.d(TAG, "Retry - Network available: " + hasNetwork);

        if (hasNetwork) {
            hideOfflineState();
            showLoading();
            recipeDetailsPresenter.getRecipeDetails(idMeal);
        } else {
            safeShowToast("Still offline. Showing cached data if available.");
        }
    }

    private void hideOfflineState() {
        if (offlineStateContainer != null) {
            offlineStateContainer.setVisibility(GONE);
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

    @Override
    public void setRecipeDetails(RecipeDetails recipeDetails) {
        this.currentRecipeDetails = recipeDetails;

        mealTitle.setText(recipeDetails.getMealName());

        if (recipeDetails.getMealArea() != null) {
            mealArea.setText(recipeDetails.getMealArea().toUpperCase());
            mealArea.setVisibility(VISIBLE);
        } else {
            mealArea.setVisibility(GONE);
        }

        if (recipeDetails.getMealCategory() != null) {
            mealType.setText(recipeDetails.getMealCategory().toUpperCase());
            mealType.setVisibility(VISIBLE);
        } else {
            mealType.setVisibility(GONE);
        }

        if (recipeDetails.getStrMealThumbnail() != null && !recipeDetails.getStrMealThumbnail().isEmpty()) {
            if (isAdded() && getContext() != null) {
                Glide.with(this)
                        .load(recipeDetails.getStrMealThumbnail())
                        .placeholder(R.drawable.splash_bg)
                        .error(R.drawable.splash_bg)
                        .centerCrop()
                        .into(mealImage);
            }
        }

        List<IngredientWithMeasure> ingredients = recipeDetails.getIngredientsWithMeasures();
        if (ingredients != null && !ingredients.isEmpty()) {
            itemCountTextView.setText(ingredients.size() + " Items");
            itemCountTextView.setVisibility(VISIBLE);
            ingredientsAdapter.setIngredients(ingredients);
        } else {
            // No ingredients available (from meal plan cache)
            itemCountTextView.setVisibility(GONE);
        }

        List<InstructionStep> instructions = parseInstructions(recipeDetails.getMealInstructions());
        if (instructions != null && !instructions.isEmpty()) {
            instructionsAdapter.setInstructions(instructions);
        }

        setupYouTubePlayer(recipeDetails.getStrYoutube());
    }

    @Override
    public void showCachedDataNotice() {
        Log.d(TAG, "Showing cached data notice");
        if (cachedDataBanner != null && cachedDataText != null) {
            cachedDataBanner.setVisibility(VISIBLE);
            cachedDataText.setText("📡 Offline - Showing saved data");
        }
    }

    @Override
    public void showOfflineNoCache() {
        Log.d(TAG, "Showing offline - no cache available");
        hideLoading();

        if (offlineStateContainer != null && offlineMessage != null) {
            offlineStateContainer.setVisibility(VISIBLE);
            offlineMessage.setText("This recipe is not available offline");
        }

        safeShowToast("No internet connection and recipe not cached");
    }

    private void setupYouTubePlayer(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.isEmpty()) {
            youTubePlayerView.setVisibility(GONE);
            return;
        }

        String videoId = extractVideoIdFromUrl(youtubeUrl);

        if (videoId != null && !videoId.isEmpty()) {
            youTubePlayerView.setVisibility(VISIBLE);
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.cueVideo(videoId, 0);
                }
            });
        } else {
            youTubePlayerView.setVisibility(GONE);
        }
    }

    private String extractVideoIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        String videoId = null;

        try {
            if (url.contains("youtube.com/watch?v=")) {
                int startIndex = url.indexOf("v=") + 2;
                int endIndex = url.indexOf("&", startIndex);
                videoId = (endIndex == -1) ? url.substring(startIndex) : url.substring(startIndex, endIndex);
            } else if (url.contains("youtu.be/")) {
                int startIndex = url.indexOf("youtu.be/") + 9;
                int endIndex = url.indexOf("?", startIndex);
                videoId = (endIndex == -1) ? url.substring(startIndex) : url.substring(startIndex, endIndex);
            } else if (url.contains("m.youtube.com/watch?v=")) {
                int startIndex = url.indexOf("v=") + 2;
                int endIndex = url.indexOf("&", startIndex);
                videoId = (endIndex == -1) ? url.substring(startIndex) : url.substring(startIndex, endIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting video ID", e);
            return null;
        }

        return videoId;
    }

    private List<InstructionStep> parseInstructions(String instructionsText) {
        List<InstructionStep> steps = new ArrayList<>();

        if (instructionsText == null || instructionsText.isEmpty()) {
            return steps;
        }

        String[] sentences = instructionsText.split("(?<=\\.) (?=[A-Z])|\r?\n");

        int stepNumber = 1;
        for (String sentence : sentences) {
            sentence = sentence.trim();

            if (sentence.endsWith(".")) {
                sentence = sentence.substring(0, sentence.length() - 1).trim();
            }

            if (!sentence.isEmpty() && sentence.length() > 15) {
                steps.add(new InstructionStep(stepNumber, sentence + "."));
                stepNumber++;
            }
        }

        return steps;
    }

    @Override
    public void showError(String errorMessage) {
        safeShowToast(errorMessage);
    }

    @Override
    public void onMealPlanAddedSuccess() {
        safeShowToast("Meal added to your plan! 📅");
    }

    @Override
    public void onMealPlanAddedFailure(String error) {
        safeShowToast("Failed to add meal: " + error);
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
    public void hideLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideLoading();
        }
        mealImage.setVisibility(VISIBLE);
        mealTitle.setVisibility(VISIBLE);
    }

    @Override
    public void showLoading() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showLoading();
        }
        mealImage.setVisibility(GONE);
        mealTitle.setVisibility(GONE);

        // Hide cached data banner while loading
        if (cachedDataBanner != null) {
            cachedDataBanner.setVisibility(GONE);
        }
    }

    private void showAddMealPlanDialog(RecipeDetails recipeDetails) {
        MealPlanBottomSheet bottomSheet = MealPlanBottomSheet.newInstance(recipeDetails.getIdMeal());
        bottomSheet.setOnMealPlanSelectedListener((selectedMealId, day, mealType) -> {
            MealPlan mealPlan = new MealPlan(
                    recipeDetails.getIdMeal(),
                    mealType,
                    day,
                    recipeDetails.getMealName(),
                    recipeDetails.getStrMealThumbnail(),
                    recipeDetails.getMealCategory(),
                    recipeDetails.getMealArea(),
                    recipeDetails.getMealInstructions()
            );
            recipeDetailsPresenter.addMealToPlan(mealPlan);
        });
        bottomSheet.show(getParentFragmentManager(), "AddMealPlanBottomSheet");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recipeDetailsPresenter != null) {
            recipeDetailsPresenter.onDestroy();
        }
    }
}