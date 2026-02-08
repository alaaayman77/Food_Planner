package com.example.foodplanner.data.model.meal_plan;
import com.google.firebase.firestore.PropertyName;

public class MealPlanFirestore {
        @PropertyName("meal_id")
        private String mealId;
        @PropertyName("meal_type")
        private String mealType;
        @PropertyName("day_of_week")
        private String dayOfWeek;
        @PropertyName("timestamp")
        private long timestamp;

        public MealPlanFirestore() {
            // Required empty constructor for Firestore
        }

        public MealPlanFirestore(String mealId, String mealType, String dayOfWeek, long timestamp) {
            this.mealId = mealId;
            this.mealType = mealType;
            this.dayOfWeek = dayOfWeek;
            this.timestamp = timestamp;
        }


        public static MealPlanFirestore fromMealPlan(MealPlan mealPlan) {
            return new MealPlanFirestore(
                    mealPlan.getMealId(),
                    mealPlan.getMealType(),
                    mealPlan.getDayOfWeek(),
                    mealPlan.getTimestamp()
            );
        }


        public String getMealId() {
            return mealId;
        }
        public void setMealId(String mealId) {
            this.mealId = mealId;
        }

        public String getMealType() {
            return mealType;
        }
        public void setMealType(String mealType) {
            this.mealType = mealType;
        }

        public String getDayOfWeek() {
            return dayOfWeek;
        }
        public void setDayOfWeek(String dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public long getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
}
