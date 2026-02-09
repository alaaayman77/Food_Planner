package com.example.foodplanner.presentation.recipe_details.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.recipe_details.IngredientWithMeasure;
import com.example.foodplanner.data.model.recipe_details.InstructionStep;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.presentation.home.view.MealPlanBottomSheet;
import com.example.foodplanner.presentation.recipe_details.presenter.RecipeDetailsPresenter;
import com.example.foodplanner.presentation.recipe_details.presenter.RecipeDetailsPresenterImp;
import com.google.android.material.button.MaterialButton;


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

    private IngredientsAdapter ingredientsAdapter;
    private InstructionsAdapter instructionsAdapter;
    private RecipeDetailsPresenter recipeDetailsPresenter;
    private MaterialButton backBtn;
    private MaterialButton addToMealPlanButton;

    // Store the current recipe details
    private RecipeDetails currentRecipeDetails;

    public RecipeDetailsFragment() {
        // Required empty public constructor
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

        recipeDetailsPresenter = new RecipeDetailsPresenterImp(this, requireContext());
        recipeDetailsPresenter.getRecipeDetails(idMeal);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        addToMealPlanButton.setOnClickListener(v -> {
            if (currentRecipeDetails != null) {
                showAddMealPlanDialog(currentRecipeDetails);
            } else {
                Toast.makeText(getContext(), "No meal available", Toast.LENGTH_SHORT).show();
            }
        });
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
    }

    private void setupRecyclerViews() {
        ingredientsAdapter = new IngredientsAdapter(requireContext());
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);
        LinearLayoutManager instructionsLayoutManager = new LinearLayoutManager(requireContext());
        instructionsRecyclerView.setLayoutManager(instructionsLayoutManager);
        instructionsAdapter = new InstructionsAdapter();
        instructionsRecyclerView.setAdapter(instructionsAdapter);
    }

    @Override
    public void setRecipeDetails(RecipeDetails recipeDetails) {
        // Store the current recipe details
        this.currentRecipeDetails = recipeDetails;

        mealTitle.setText(recipeDetails.getMealName());

        if (recipeDetails.getMealArea() != null) {
            mealArea.setText(recipeDetails.getMealArea().toUpperCase());
        }
        if (recipeDetails.getMealCategory() != null) {
            mealType.setText(recipeDetails.getMealCategory().toUpperCase());
        }

        if (recipeDetails.getStrMealThumbnail() != null && !recipeDetails.getStrMealThumbnail().isEmpty()) {
            Glide.with(this)
                    .load(recipeDetails.getStrMealThumbnail())
                    .placeholder(R.drawable.splash_bg)
                    .error(R.drawable.splash_bg)
                    .centerCrop()
                    .into(mealImage);
        }

        List<IngredientWithMeasure> ingredients = recipeDetails.getIngredientsWithMeasures();
        if (ingredients != null && !ingredients.isEmpty()) {
            itemCountTextView.setText(ingredients.size() + " Items");
            ingredientsAdapter.setIngredients(ingredients);
        }

        List<InstructionStep> instructions = parseInstructions(recipeDetails.getMealInstructions());
        instructionsAdapter.setInstructions(instructions);
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
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMealPlanAddedSuccess() {
        Toast.makeText(getContext(),
                "Meal added to your plan! 📅",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMealPlanAddedFailure(String error) {
        Toast.makeText(getContext(),
                "Failed to add meal: " + error,
                Toast.LENGTH_SHORT).show();
    }

    private void showAddMealPlanDialog(RecipeDetails recipeDetails) {
        MealPlanBottomSheet bottomSheet = MealPlanBottomSheet.newInstance(recipeDetails.getIdMeal());
        bottomSheet.setOnMealPlanSelectedListener((selectedMealId, day, mealType) -> {
            // Create MealPlan from RecipeDetails
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
}