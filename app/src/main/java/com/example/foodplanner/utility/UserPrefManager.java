package com.example.foodplanner.utility;



import android.content.Context;
import android.content.SharedPreferences;
import com.example.foodplanner.data.model.random_meals.RandomMeal;
import com.google.gson.Gson;

public class UserPrefManager {

    private static final String PREF_NAME = "FoodPlannerPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_FIRST_LAUNCH = "is_first_launch";

    // Meal of the Day cache keys
    private static final String KEY_MEAL_OF_THE_DAY = "meal_of_the_day";
    private static final String KEY_MEAL_CACHE_TIMESTAMP = "meal_cache_timestamp";
    private static final long CACHE_DURATION_MS = 24 * 60 * 60 * 1000; // 24 hours

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public UserPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    // Existing methods...
    public void setUserLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void saveUserData(String userId, String email, String userName) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, userName);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public void clearUserData() {
        editor.clear();
        editor.apply();
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_LAUNCH, true);
    }

    public void setFirstLaunchComplete() {
        editor.putBoolean(KEY_IS_FIRST_LAUNCH, false);
        editor.apply();
    }

    // ========== Meal of the Day Methods ==========

    /**
     * Save the meal of the day with current timestamp
     */
    public void saveMealOfTheDay(RandomMeal meal) {
        String mealJson = gson.toJson(meal);
        long currentTime = System.currentTimeMillis();

        editor.putString(KEY_MEAL_OF_THE_DAY, mealJson);
        editor.putLong(KEY_MEAL_CACHE_TIMESTAMP, currentTime);
        editor.apply();
    }

    /**
     * Get cached meal if it's still valid (less than 24 hours old)
     * Returns null if cache is expired or doesn't exist
     */
    public RandomMeal getCachedMealOfTheDay() {
        long cacheTimestamp = sharedPreferences.getLong(KEY_MEAL_CACHE_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();

        // Check if cache is still valid
        if (cacheTimestamp == 0 || (currentTime - cacheTimestamp) > CACHE_DURATION_MS) {
            return null; // Cache expired or doesn't exist
        }

        String mealJson = sharedPreferences.getString(KEY_MEAL_OF_THE_DAY, null);
        if (mealJson == null) {
            return null;
        }

        return gson.fromJson(mealJson, RandomMeal.class);
    }

    /**
     * Check if the cached meal is still valid
     */
    public boolean isMealCacheValid() {
        long cacheTimestamp = sharedPreferences.getLong(KEY_MEAL_CACHE_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();

        return cacheTimestamp != 0 && (currentTime - cacheTimestamp) <= CACHE_DURATION_MS;
    }

    /**
     * Clear the meal cache
     */
    public void clearMealCache() {
        editor.remove(KEY_MEAL_OF_THE_DAY);
        editor.remove(KEY_MEAL_CACHE_TIMESTAMP);
        editor.apply();
    }

    /**
     * Get time remaining until cache expires (in milliseconds)
     */
    public long getTimeUntilCacheExpires() {
        long cacheTimestamp = sharedPreferences.getLong(KEY_MEAL_CACHE_TIMESTAMP, 0);
        if (cacheTimestamp == 0) {
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - cacheTimestamp;
        long remaining = CACHE_DURATION_MS - elapsed;

        return Math.max(0, remaining);
    }
}