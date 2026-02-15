package com.example.foodplanner.data.model.onboarding;

public class OnBoarding {
    private final int    lottieRawRes;   // R.raw.xxx
    private final String title;
    private final String description;

    public OnBoarding(int lottieRawRes, String title, String description) {
        this.lottieRawRes = lottieRawRes;
        this.title        = title;
        this.description  = description;
    }

    public int    getLottieRawRes() { return lottieRawRes; }
    public String getTitle()        { return title; }
    public String getDescription()  { return description; }
}
