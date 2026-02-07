package com.example.foodplanner.presentation.mealplanner.presenter;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.presentation.mealplanner.view.MealPlannerFragment;
import com.example.foodplanner.presentation.mealplanner.view.MealPlannerView;

import java.util.ArrayList;
import java.util.List;

public class MealPlannerPresenterImp implements MealPlannerPresenter {
    private MealPlannerView view;
    private MealsRepository repository;
    private LifecycleOwner lifecycleOwner;

    public MealPlannerPresenterImp(MealPlannerView view, Context context, LifecycleOwner lifecycleOwner) {
        this.view = view;
        this.repository = new MealsRepository(context);
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public void loadMealPlansForDay(String dayOfWeek) {
        repository.getMealPlansByDay(dayOfWeek).observe(lifecycleOwner, new Observer<List<MealPlan>>() {
            @Override
            public void onChanged(List<MealPlan> mealPlans) {
                // reset all meal displays first
                view.hideBreakfastMeal();
                view.hideLunchMeal();
                view.hideDinnerMeal();

                if (mealPlans != null && !mealPlans.isEmpty()) {
                    // group meals by type
                    List<MealPlan> breakfastMeals = new ArrayList<>();
                    List<MealPlan> lunchMeals = new ArrayList<>();
                    List<MealPlan> dinnerMeals = new ArrayList<>();

                    for (MealPlan mealPlan : mealPlans) {
                        String mealType = mealPlan.getMealType();
                        if (mealType != null) {
                            switch (mealType.toUpperCase()) {
                                case "BREAKFAST":
                                    breakfastMeals.add(mealPlan);
                                    break;
                                case "LUNCH":
                                    lunchMeals.add(mealPlan);
                                    break;
                                case "DINNER":
                                    dinnerMeals.add(mealPlan);
                                    break;
                            }
                        }
                    }

                    // show all meals of each type at once
                    if (!breakfastMeals.isEmpty() && view instanceof MealPlannerFragment) {
                        ((MealPlannerFragment) view).showBreakfastMeals(breakfastMeals);
                    }

                    if (!lunchMeals.isEmpty() && view instanceof MealPlannerFragment) {
                        ((MealPlannerFragment) view).showLunchMeals(lunchMeals);
                    }

                    if (!dinnerMeals.isEmpty() && view instanceof MealPlannerFragment) {
                        ((MealPlannerFragment) view).showDinnerMeals(dinnerMeals);
                    }

                    view.displayMealPlans(mealPlans);
                } else {
                    // empty state will be shown
                    view.displayMealPlans(new ArrayList<>());
                }
            }
        });
    }


    @Override
    public void deleteMealPlanById(int mealPlanId) {
        repository.deleteMealPlanById(mealPlanId);
    }
}