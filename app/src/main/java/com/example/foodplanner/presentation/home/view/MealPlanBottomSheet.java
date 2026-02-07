package com.example.foodplanner.presentation.home.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foodplanner.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MealPlanBottomSheet extends BottomSheetDialogFragment {

    private TextView tvSelectedDate;
    private RadioGroup mealTypeRadioGroup;
    private MaterialButton btnSelectDate;
    private MaterialButton btnConfirm;
    private MaterialButton btnCancel;

    private OnMealPlanSelectedListener listener;
    private String mealId;
    private Calendar selectedCalendar;
    private SimpleDateFormat displayDateFormat;
    private SimpleDateFormat dayOfWeekFormat;

    public static MealPlanBottomSheet newInstance(String mealId) {
        MealPlanBottomSheet fragment = new MealPlanBottomSheet();
        Bundle args = new Bundle();
        args.putString("mealId", mealId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mealId = getArguments().getString("mealId");
        }
        selectedCalendar = Calendar.getInstance();
        displayDateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault()); // For display
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        mealTypeRadioGroup = view.findViewById(R.id.mealTypeRadioGroup);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnCancel = view.findViewById(R.id.btnCancel);

        updateDateDisplay();
        ((RadioButton) mealTypeRadioGroup.findViewById(R.id.rbBreakfast)).setChecked(true);

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnConfirm.setOnClickListener(v -> {
            int selectedTypeId = mealTypeRadioGroup.getCheckedRadioButtonId();

            if (selectedTypeId == -1) {
                Toast.makeText(getContext(), "Please select a meal type", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedTypeButton = view.findViewById(selectedTypeId);
            String mealType = selectedTypeButton.getText().toString().toUpperCase();

            // Get day of week in uppercase (MONDAY, TUESDAY, etc.) to match database format
            String dayOfWeek = getDayOfWeekString(selectedCalendar);

            if (listener != null) {
                listener.onMealPlanSelected(mealId, dayOfWeek, mealType);
            }

            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void showDatePicker() {
        Calendar today = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);

                    Calendar todayStart = Calendar.getInstance();
                    todayStart.set(Calendar.HOUR_OF_DAY, 0);
                    todayStart.set(Calendar.MINUTE, 0);
                    todayStart.set(Calendar.SECOND, 0);
                    todayStart.set(Calendar.MILLISECOND, 0);

                    selected.set(Calendar.HOUR_OF_DAY, 0);
                    selected.set(Calendar.MINUTE, 0);
                    selected.set(Calendar.SECOND, 0);
                    selected.set(Calendar.MILLISECOND, 0);

                    if (selected.before(todayStart)) {
                        Toast.makeText(getContext(),
                                "Sorry, can't pick a past date",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedCalendar.set(year, month, dayOfMonth);
                    updateDateDisplay();
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        tvSelectedDate.setText(displayDateFormat.format(selectedCalendar.getTime()));
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

    public void setOnMealPlanSelectedListener(OnMealPlanSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnMealPlanSelectedListener {
        void onMealPlanSelected(String mealId, String dayOfWeek, String mealType);
    }
}