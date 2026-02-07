package com.example.foodplanner.data.model.meal_plan;
import java.util.Calendar;
public class DateItem {
        private String dayOfWeek;
        private String dayNumber;
        private String monthYear;
        private boolean isSelected;
        private Calendar calendar;

        public DateItem(String dayOfWeek, String dayNumber, String monthYear, boolean isSelected, Calendar calendar) {
            this.dayOfWeek = dayOfWeek;
            this.dayNumber = dayNumber;
            this.monthYear = monthYear;
            this.isSelected = isSelected;
            this.calendar = calendar;
        }

        public String getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(String dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public String getDayNumber() {
            return dayNumber;
        }

        public void setDayNumber(String dayNumber) {
            this.dayNumber = dayNumber;
        }

        public String getMonthYear() {
            return monthYear;
        }

        public void setMonthYear(String monthYear) {
            this.monthYear = monthYear;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public Calendar getCalendar() {
            return calendar;
        }

        public void setCalendar(Calendar calendar) {
            this.calendar = calendar;
        }

        public String getFormattedDayOfWeek() {
            // Returns day name for database queries (e.g., "MONDAY", "TUESDAY")
            return dayOfWeek.toUpperCase();
        }

}
