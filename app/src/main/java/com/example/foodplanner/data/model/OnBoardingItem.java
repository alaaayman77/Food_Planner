package com.example.foodplanner.data.model;

public class OnBoardingItem {



        private int lottieRes;
        private String title;
        private String description;

        public OnBoardingItem(int lottieRes, String title, String description) {
            this.lottieRes = lottieRes;
            this.title = title;
            this.description = description;
        }

        public int getLottieRes() {
            return lottieRes;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

}
