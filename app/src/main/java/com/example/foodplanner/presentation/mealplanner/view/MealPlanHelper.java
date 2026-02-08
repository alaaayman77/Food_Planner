package com.example.foodplanner.presentation.mealplanner.view;



import androidx.fragment.app.FragmentManager;

import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.presentation.home.view.MealPlanBottomSheet;

public class MealPlanHelper {

    public interface MealPlanCallback {
        void onMealPlanCreated(MealPlan mealPlan);
    }

    public static void showAddMealPlanDialog(
            FragmentManager fragmentManager,
            String mealId,
            String mealName,
            String mealThumbnail,
            String mealCategory,
            String mealArea,
            String mealInstructions,
            MealPlanCallback callback) {

        MealPlanBottomSheet bottomSheet = MealPlanBottomSheet.newInstance(mealId);
        bottomSheet.setOnMealPlanSelectedListener((selectedMealId, day, mealType) -> {
            MealPlan mealPlan = new MealPlan(
                    selectedMealId,
                    mealType,
                    day,
                    mealName,
                    mealThumbnail,
                    mealCategory,
                    mealArea,
                    mealInstructions
            );
            callback.onMealPlanCreated(mealPlan);
        });
        bottomSheet.show(fragmentManager, "AddMealPlanBottomSheet");
    }


    public static void showAddMealPlanDialog(
            FragmentManager fragmentManager,
            String mealId,
            String mealName,
            String mealThumbnail,
            MealPlanCallback callback) {

        showAddMealPlanDialog(
                fragmentManager,
                mealId,
                mealName,
                mealThumbnail,
                null,
                null,
                null,
                callback
        );
    }
}