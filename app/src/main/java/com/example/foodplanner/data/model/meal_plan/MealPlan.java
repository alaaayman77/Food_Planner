package com.example.foodplanner.data.model.meal_plan;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

    @Entity(tableName = "meal_plan")
    public class MealPlan {
        @PrimaryKey(autoGenerate = true)
        private int id;

        private String mealId;
        private String mealType;
        private String dayOfWeek;
        private long timestamp;


        private String mealName;
        private String mealThumbnail;
        private String mealCategory;
        private String mealArea;
        private String mealInstructions;

        public MealPlan() {
        }

        public MealPlan(String mealId, String mealType, String dayOfWeek,
                                   String mealName, String mealThumbnail, String mealCategory,
                                   String mealArea, String mealInstructions) {
            this.mealId = mealId;
            this.mealType = mealType;
            this.dayOfWeek = dayOfWeek;
            this.mealName = mealName;
            this.mealThumbnail = mealThumbnail;
            this.mealCategory = mealCategory;
            this.mealArea = mealArea;
            this.mealInstructions = mealInstructions;
            this.timestamp = System.currentTimeMillis();
        }


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getMealName() {
            return mealName;
        }

        public void setMealName(String mealName) {
            this.mealName = mealName;
        }

        public String getMealThumbnail() {
            return mealThumbnail;
        }

        public void setMealThumbnail(String mealThumbnail) {
            this.mealThumbnail = mealThumbnail;
        }

        public String getMealCategory() {
            return mealCategory;
        }

        public void setMealCategory(String mealCategory) {
            this.mealCategory = mealCategory;
        }

        public String getMealArea() {
            return mealArea;
        }

        public void setMealArea(String mealArea) {
            this.mealArea = mealArea;
        }

        public String getMealInstructions() {
            return mealInstructions;
        }

        public void setMealInstructions(String mealInstructions) {
            this.mealInstructions = mealInstructions;
        }

}
