package com.example.foodplanner.presentation.mealplanner.view;

import android.app.DatePickerDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodplanner.MainActivity;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.meal_plan.DateItem;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.presentation.mealplanner.presenter.MealPlannerPresenter;
import com.example.foodplanner.presentation.mealplanner.presenter.MealPlannerPresenterImp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MealPlannerFragment extends Fragment implements MealPlannerView,
        DateSelectorAdapter.OnDateSelectedListener, MealItemAdapter.OnMealItemClickListener {

    private RecyclerView rvDateSelector;
    private DateSelectorAdapter dateSelectorAdapter;
    private TextView tvCurrentDay;
    private TextView tvCurrentDate;
    private TextView tvEmptyState;
    private ImageView btnCalendarPicker;

    // Breakfast
    private LinearLayout breakfastSection;
    private RecyclerView rvBreakfast;
    private MealItemAdapter breakfastAdapter;

    // Lunch
    private LinearLayout lunchSection;
    private RecyclerView rvLunch;
    private MealItemAdapter lunchAdapter;

    // Dinner
    private LinearLayout dinnerSection;
    private RecyclerView rvDinner;
    private MealItemAdapter dinnerAdapter;

    private MealPlannerPresenter presenter;
    private String currentSelectedDay;
    private Calendar currentSelectedCalendar;
    private List<DateItem> dateItemList;

    public MealPlannerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupPresenter();
        setupAdapters();
        setupDateSelector();
        setupCalendarPickerButton();
        loadTodaysMeals();
    }

    private void initializeViews(View view) {
        rvDateSelector = view.findViewById(R.id.rvDateSelector);
        tvCurrentDay = view.findViewById(R.id.tvCurrentDay);
        tvCurrentDate = view.findViewById(R.id.tvCurrentDate);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        btnCalendarPicker = view.findViewById(R.id.btnCalendarPicker);

        breakfastSection = view.findViewById(R.id.breakfastSection);
        rvBreakfast = view.findViewById(R.id.rvBreakfast);

        lunchSection = view.findViewById(R.id.lunchSection);
        rvLunch = view.findViewById(R.id.rvLunch);

        dinnerSection = view.findViewById(R.id.dinnerSection);
        rvDinner = view.findViewById(R.id.rvDinner);
    }

    private void setupPresenter() {
        presenter = new MealPlannerPresenterImp(this, requireContext(),getViewLifecycleOwner());
    }

    private void setupAdapters() {
        breakfastAdapter = new MealItemAdapter(this);
        rvBreakfast.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBreakfast.setNestedScrollingEnabled(false);
        rvBreakfast.setAdapter(breakfastAdapter);

        lunchAdapter = new MealItemAdapter(this);
        rvLunch.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvLunch.setNestedScrollingEnabled(false);
        rvLunch.setAdapter(lunchAdapter);

        dinnerAdapter = new MealItemAdapter(this);
        rvDinner.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDinner.setNestedScrollingEnabled(false);
        rvDinner.setAdapter(dinnerAdapter);
    }

    private void setupDateSelector() {
        dateSelectorAdapter = new DateSelectorAdapter(this);
        rvDateSelector.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvDateSelector.setAdapter(dateSelectorAdapter);

        dateItemList = generateNext14Days();
        dateSelectorAdapter.setDates(dateItemList);
    }

    private void setupCalendarPickerButton() {
        if (btnCalendarPicker != null) {
            btnCalendarPicker.setOnClickListener(v -> showDatePickerDialog());
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    jumpToDate(selected);
                },
                currentSelectedCalendar.get(Calendar.YEAR),
                currentSelectedCalendar.get(Calendar.MONTH),
                currentSelectedCalendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void jumpToDate(Calendar targetDate) {
        Calendar firstDate = dateItemList.get(0).getCalendar();
        Calendar lastDate = dateItemList.get(dateItemList.size() - 1).getCalendar();

        if (isDateInRange(targetDate, firstDate, lastDate)) {
            // Date is within current range - just select it
            for (int i = 0; i < dateItemList.size(); i++) {
                if (isSameDay(dateItemList.get(i).getCalendar(), targetDate)) {
                    dateSelectorAdapter.setSelectedPosition(i);
                    rvDateSelector.smoothScrollToPosition(i);
                    onDateSelected(dateItemList.get(i), i);
                    return;
                }
            }
        } else {
            // Date is outside current range - regenerate dates
            dateItemList = generateDatesFromDate(targetDate);
            dateSelectorAdapter.setDates(dateItemList);
            dateSelectorAdapter.setSelectedPosition(0);
            rvDateSelector.scrollToPosition(0);

            currentSelectedCalendar = (Calendar) targetDate.clone();
            currentSelectedDay = getDayOfWeekString(currentSelectedCalendar);
            updateCurrentDateDisplay(currentSelectedCalendar);

            hideBreakfastMeal();
            hideLunchMeal();
            hideDinnerMeal();

            presenter.loadMealPlansForDay(currentSelectedDay);
        }
    }

    private boolean isDateInRange(Calendar date, Calendar start, Calendar end) {
        Calendar dateOnly = (Calendar) date.clone();
        dateOnly.set(Calendar.HOUR_OF_DAY, 0);
        dateOnly.set(Calendar.MINUTE, 0);
        dateOnly.set(Calendar.SECOND, 0);
        dateOnly.set(Calendar.MILLISECOND, 0);

        Calendar startOnly = (Calendar) start.clone();
        startOnly.set(Calendar.HOUR_OF_DAY, 0);
        startOnly.set(Calendar.MINUTE, 0);
        startOnly.set(Calendar.SECOND, 0);
        startOnly.set(Calendar.MILLISECOND, 0);

        Calendar endOnly = (Calendar) end.clone();
        endOnly.set(Calendar.HOUR_OF_DAY, 0);
        endOnly.set(Calendar.MINUTE, 0);
        endOnly.set(Calendar.SECOND, 0);
        endOnly.set(Calendar.MILLISECOND, 0);

        return !dateOnly.before(startOnly) && !dateOnly.after(endOnly);
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private List<DateItem> generateNext14Days() {
        return generateDatesFromDate(Calendar.getInstance());
    }

    private List<DateItem> generateDatesFromDate(Calendar startDate) {
        List<DateItem> dateList = new ArrayList<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());

        for (int i = 0; i < 14; i++) {
            Calendar dayCalendar = (Calendar) startDate.clone();
            dayCalendar.add(Calendar.DAY_OF_YEAR, i);

            String dayOfWeek = dayFormat.format(dayCalendar.getTime());
            String dayNumber = dateFormat.format(dayCalendar.getTime());
            String month = monthFormat.format(dayCalendar.getTime());

            DateItem dateItem = new DateItem(
                    dayOfWeek,
                    dayNumber,
                    month,
                    i == 0,
                    dayCalendar
            );

            dateList.add(dateItem);
        }

        return dateList;
    }

    private void loadTodaysMeals() {
        Calendar today = Calendar.getInstance();
        updateCurrentDateDisplay(today);
        String dayOfWeek = getDayOfWeekString(today);
        currentSelectedDay = dayOfWeek;
        currentSelectedCalendar = today;

        hideBreakfastMeal();
        hideLunchMeal();
        hideDinnerMeal();

        presenter.loadMealPlansForDay(dayOfWeek);
    }

    private String getDayOfWeekString(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "MONDAY";
            case Calendar.TUESDAY:
                return "TUESDAY";
            case Calendar.WEDNESDAY:
                return "WEDNESDAY";
            case Calendar.THURSDAY:
                return "THURSDAY";
            case Calendar.FRIDAY:
                return "FRIDAY";
            case Calendar.SATURDAY:
                return "SATURDAY";
            case Calendar.SUNDAY:
                return "SUNDAY";
            default:
                return "MONDAY";
        }
    }

    private void updateCurrentDateDisplay(Calendar calendar) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

        tvCurrentDay.setText(dayFormat.format(calendar.getTime()) + " •");
        tvCurrentDate.setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void onDateSelected(DateItem dateItem, int position) {
        currentSelectedCalendar = dateItem.getCalendar();
        currentSelectedDay = getDayOfWeekString(currentSelectedCalendar);
        updateCurrentDateDisplay(currentSelectedCalendar);

        hideBreakfastMeal();
        hideLunchMeal();
        hideDinnerMeal();

        presenter.loadMealPlansForDay(currentSelectedDay);
    }

    // ============ MealPlannerView Implementation ============

    @Override
    public void displayMealPlans(List<MealPlan> mealPlans) {
        if (breakfastSection.getVisibility() == View.GONE &&
                lunchSection.getVisibility() == View.GONE &&
                dinnerSection.getVisibility() == View.GONE) {
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void showBreakfastMeals(List<MealPlan> meals) {
        if (meals != null && !meals.isEmpty()) {
            breakfastSection.setVisibility(View.VISIBLE);
            breakfastAdapter.setMeals(meals);
            tvEmptyState.setVisibility(View.GONE);
        } else {
            hideBreakfastMeal();
        }
    }

    @Override
    public void showLunchMeals(List<MealPlan> meals) {
        if (meals != null && !meals.isEmpty()) {
            lunchSection.setVisibility(View.VISIBLE);
            lunchAdapter.setMeals(meals);
            tvEmptyState.setVisibility(View.GONE);
        } else {
            hideLunchMeal();
        }
    }

    @Override
    public void showDinnerMeals(List<MealPlan> meals) {
        if (meals != null && !meals.isEmpty()) {
            dinnerSection.setVisibility(View.VISIBLE);
            dinnerAdapter.setMeals(meals);
            tvEmptyState.setVisibility(View.GONE);
        } else {
            hideDinnerMeal();
        }
    }

    @Override
    public void hideBreakfastMeal() {
        breakfastSection.setVisibility(View.GONE);
        breakfastAdapter.setMeals(new ArrayList<>());
        checkAndShowEmptyState();
    }

    @Override
    public void hideLunchMeal() {
        lunchSection.setVisibility(View.GONE);
        lunchAdapter.setMeals(new ArrayList<>());
        checkAndShowEmptyState();
    }

    @Override
    public void hideDinnerMeal() {
        dinnerSection.setVisibility(View.GONE);
        dinnerAdapter.setMeals(new ArrayList<>());
        checkAndShowEmptyState();
    }

    private void checkAndShowEmptyState() {
        if (breakfastSection.getVisibility() == View.GONE &&
                lunchSection.getVisibility() == View.GONE &&
                dinnerSection.getVisibility() == View.GONE) {
            tvEmptyState.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
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
    public void onMealPlanDeletedSuccess() {
        Toast.makeText(requireContext(), "Meal removed ✓", Toast.LENGTH_SHORT).show();
        // Reload meals for current day to refresh the UI
        presenter.loadMealPlansForDay(currentSelectedDay);
    }

    @Override
    public void onMealPlanDeletedFailure(String errorMessage) {
        Toast.makeText(requireContext(),
                "Failed to remove meal: " + errorMessage,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSyncSuccess() {
        Toast.makeText(requireContext(),
                "Meal plans synced successfully!",
                Toast.LENGTH_SHORT).show();
        // Reload current day's meals after sync
        presenter.loadMealPlansForDay(currentSelectedDay);
    }

    // ============ MealItemAdapter.OnMealItemClickListener Implementation ============

    @Override
    public void onMealClick(MealPlan mealPlan) {
        navigateToRecipeDetails(mealPlan.getMealId());
    }

    @Override
    public void onDeleteClick(MealPlan mealPlan) {
        showDeleteConfirmationDialog(mealPlan);
    }

    private void showDeleteConfirmationDialog(MealPlan mealPlan) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Remove Meal")
                .setMessage("Do you want to remove " + mealPlan.getMealName() + " from your plan?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    presenter.deleteMealPlanById(mealPlan);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void navigateToRecipeDetails(String mealId) {
        // Navigate to recipe details using Navigation component
        // Assuming you have a navigation action setup

    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload meals when returning to this fragment
        if (currentSelectedDay != null) {
            presenter.loadMealPlansForDay(currentSelectedDay);
        }
    }
}