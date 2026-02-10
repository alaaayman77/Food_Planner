package com.example.foodplanner.presentation.auth.login;


import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import com.example.foodplanner.data.MealsRepository;
import com.example.foodplanner.data.datasource.remote.MealPlanFirestoreNetworkResponse;
import com.example.foodplanner.data.model.user.User;
import com.example.foodplanner.data.model.meal_plan.MealPlan;
import com.example.foodplanner.data.model.meal_plan.MealPlanFirestore;
import com.example.foodplanner.data.model.recipe_details.RecipeDetails;
import com.example.foodplanner.utility.UserPrefManager;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginPresenterImp implements LoginPresenter {
    private static final String TAG = "LoginPresenterImpl";

    private LoginView view;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private MealsRepository mealsRepository;
    private UserPrefManager preferencesManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LoginPresenterImp(LoginView view, MealsRepository mealsRepository, Context context) {
        this.view = view;
        this.mealsRepository = mealsRepository;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.preferencesManager = new UserPrefManager(context);
    }

    @Override
    public void handleLogin() {
        String input = view.getUsername().trim();
        String password = view.getPassword().trim();

        view.clearErrors();

        if (input.isEmpty()) {
            view.setUsernameError("Username is required");
            return;
        }

        if (password.isEmpty()) {
            view.setPasswordError("Password is required");
            return;
        }

        view.showLoading();

        if (isEmail(input)) {
            loginWithEmail(input, password);
        } else {
            findUserByUsername(input, password);
        }
    }

    @Override
    public void handleGoogleSignIn() {
        Log.d(TAG, "Google Sign-In initiated by presenter");
        view.showLoading();

    }

    @Override
    public void handleTwitterSignIn() {
        Log.d(TAG, "Twitter Sign-In initiated by presenter");
        view.showLoading();

    }

    @Override
    public void handleGuestMode() {
        view.navigateToHome();
    }

    @Override
    public void onGoogleSignInResult(String idToken) {
        Log.d(TAG, "Authenticating with Firebase using Google token");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase authentication successful");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            checkAndCreateUserInFirestore(firebaseUser);
                            syncMealPlans();
                        }
                    } else {
                        view.hideLoading();
                        Log.e(TAG, "Firebase authentication failed", task.getException());
                        view.showError("Authentication failed");
                    }
                });
    }

    @Override
    public void onTwitterSignInResult(AuthResult authResult) {
        FirebaseUser firebaseUser = authResult.getUser();

        if (firebaseUser != null) {
            Log.d(TAG, "Twitter user authenticated: " + firebaseUser.getEmail());
            checkAndCreateUserInFirestore(firebaseUser);
            syncMealPlans();
        } else {
            view.hideLoading();
            view.showError("Authentication failed");
        }
    }

    @Override
    public void onGoogleSignInError(int statusCode) {
        view.hideLoading();
        Log.e(TAG, "Google sign in failed. Status code: " + statusCode);

        String errorMessage;
        switch (statusCode) {
            case 7:
                errorMessage = "Network error. Please check your internet connection.";
                break;
            case 10:
                errorMessage = "Developer error. Please add SHA-1 certificate to Firebase Console.";
                break;
            case 12500:
                errorMessage = "Google Play Services not available. Please update.";
                break;
            case 12501:
                errorMessage = "Sign-in cancelled.";
                break;
            default:
                errorMessage = "Google sign-in failed. Error code: " + statusCode;
        }

        view.showError(errorMessage);
    }

    @Override
    public void onTwitterSignInError(Exception e) {
        view.hideLoading();
        Log.e(TAG, "Twitter sign-in failed", e);

        String errorMessage;
        if (e instanceof FirebaseAuthException) {
            FirebaseAuthException authException = (FirebaseAuthException) e;
            String errorCode = authException.getErrorCode();

            switch (errorCode) {
                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                    errorMessage = "An account already exists with the same email. Try a different sign-in method.";
                    break;
                case "ERROR_WEB_CONTEXT_CANCELED":
                    return; // User cancelled
                default:
                    errorMessage = "Twitter sign-in failed: " + authException.getMessage();
            }
        } else {
            errorMessage = "Twitter sign-in failed. Please try again.";
        }

        view.showError(errorMessage);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        view = null;
    }

    private boolean isEmail(String input) {
        return Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    private void findUserByUsername(String username, String password) {
        CollectionReference usersRef = db.collection("users");
        Query usernameQuery = usersRef.whereEqualTo("username", username).limit(1);

        usernameQuery.get().addOnSuccessListener(query -> {
            if (!query.isEmpty()) {
                User user = query.getDocuments().get(0).toObject(User.class);
                if (user != null) {
                    loginWithEmail(user.getEmail(), password);
                }
            } else {
                view.hideLoading();
                view.showError("Username not found");
            }
        }).addOnFailureListener(e -> {
            view.hideLoading();
            view.showError("Error fetching user: " + e.getMessage());
        });
    }

    private void loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {

                            db.collection("users")
                                    .document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        String username = documentSnapshot.getString("username");
                                        saveUserLoginState(user, username);
                                        syncMealPlans();
                                    })
                                    .addOnFailureListener(e -> {
                                        saveUserLoginState(user, null);
                                        syncMealPlans();
                                    });
                        }
                    } else {
                        view.hideLoading();
                        view.showError("Wrong password");
                    }
                });
    }

    private void checkAndCreateUserInFirestore(FirebaseUser firebaseUser) {
        db.collection("users")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        String email = firebaseUser.getEmail();
                        String username = firebaseUser.getDisplayName() != null
                                ? firebaseUser.getDisplayName()
                                : email.split("@")[0];

                        User user = new User(username, email);
                        db.collection("users")
                                .document(firebaseUser.getUid())
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    // Save user login state
                                    saveUserLoginState(firebaseUser, username);
                                });
                    } else {
                        // user exists
                        String username = documentSnapshot.getString("username");
                        saveUserLoginState(firebaseUser, username);
                    }
                });
    }

    private void saveUserLoginState(FirebaseUser firebaseUser, String username) {
        String email = firebaseUser.getEmail();
        String userId = firebaseUser.getUid();

        if (username == null) {
            username = email != null ? email.split("@")[0] : "User";
        }

        // save to SharedPreferences
        preferencesManager.saveUserData(userId, email, username);
        Log.d(TAG, "User login state saved: " + email);
    }

    private void syncMealPlans() {
        mealsRepository.getAllMealPlansFromFirestore(new MealPlanFirestoreNetworkResponse() {
            @Override
            public void onSaveSuccess() {
            }

            @Override
            public void onFetchSuccess(List<MealPlanFirestore> firestorePlans) {
                if (firestorePlans.isEmpty()) {
                    // No meal plans to sync, navigate immediately
                    view.hideLoading();
                    view.showSuccess("Login successful");
                    view.navigateToHome();
                    return;
                }

                Log.d(TAG, "Starting sync of " + firestorePlans.size() + " meal plans");

                // Use AtomicInteger to track completed syncs
                AtomicInteger completedCount = new AtomicInteger(0);
                AtomicInteger errorCount = new AtomicInteger(0);
                int totalPlans = firestorePlans.size();

                for (MealPlanFirestore firestorePlan : firestorePlans) {
                    fetchAndSaveMealPlan(firestorePlan, new SyncCallback() {
                        @Override
                        public void onComplete(boolean success) {
                            int completed = completedCount.incrementAndGet();

                            if (!success) {
                                errorCount.incrementAndGet();
                            }

                            Log.d(TAG, "Sync progress: " + completed + "/" + totalPlans);

                            // Check if all syncs are complete
                            if (completed == totalPlans) {
                                view.hideLoading();

                                if (errorCount.get() > 0) {
                                    view.showSuccess("Login successful! " +
                                            (totalPlans - errorCount.get()) + " of " + totalPlans +
                                            " meals synced");
                                } else {
                                    view.showSuccess("Login successful! All meals synced");
                                }

                                view.navigateToHome();
                            }
                        }
                    });
                }
            }

            @Override
            public void onDeleteSuccess() {
            }

            @Override
            public void onFailure(String error) {
                view.hideLoading();
                view.showSuccess("Login successful");
                view.navigateToHome();
                Log.e(TAG, "Sync failed: " + error);
            }
        });
    }
    private void fetchAndSaveMealPlan(MealPlanFirestore firestorePlan, SyncCallback callback) {
        compositeDisposable.add(
                mealsRepository.getRecipeDetails(firestorePlan.getMealId())
                        .subscribeOn(Schedulers.io())
                        .map(response -> {
                            List<RecipeDetails> recipeDetails = response.getRecipeDetails();
                            if (recipeDetails == null || recipeDetails.isEmpty()) {
                                throw new Exception("No meal found for ID: " + firestorePlan.getMealId());
                            }
                            return recipeDetails.get(0);
                        })
                        .map(mealDetails -> {
                            // Map RecipeDetails to MealPlan
                            MealPlan mealPlan = new MealPlan(
                                    mealDetails.getIdMeal(),
                                    firestorePlan.getMealType(),
                                    firestorePlan.getDayOfWeek(),
                                    mealDetails.getMealName(),
                                    mealDetails.getStrMealThumbnail(),
                                    mealDetails.getMealCategory(),
                                    mealDetails.getMealArea(),
                                    mealDetails.getMealInstructions()
                            );
                            mealPlan.setTimestamp(firestorePlan.getTimestamp());
                            return mealPlan;
                        })
                        .flatMapCompletable(mealPlan ->
                                mealsRepository.insertMealToMealPlan(mealPlan)
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    // onComplete - successfully fetched and saved
                                    Log.d(TAG, "Successfully synced meal: " + firestorePlan.getMealId());
                                    callback.onComplete(true);
                                },
                                error -> {
                                    // onError
                                    Log.e(TAG, "Error syncing meal: " + firestorePlan.getMealId(), error);

                                    callback.onComplete(false);
                                }
                        )
        );
    }
//    private void fetchAndSaveMealPlan(MealPlanFirestore firestorePlan, SyncCallback callback) {
//        compositeDisposable.add(
//                mealsRepository.getRecipeDetails(firestorePlan.getMealId())
//                        .subscribeOn(Schedulers.io())
//                        .map(response -> {
//                            List<RecipeDetails> recipeDetails = response.getRecipeDetails();
//                            if (recipeDetails == null || recipeDetails.isEmpty()) {
//                                throw new Exception("No meal found for ID: " + firestorePlan.getMealId());
//                            }
//                            return recipeDetails.get(0);
//                        })
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                mealDetails -> {
//                                    MealPlan mealPlan = new MealPlan(
//                                            mealDetails.getIdMeal(),
//
//                                            firestorePlan.getMealType(),
//                                            firestorePlan.getDayOfWeek(),
//                                            mealDetails.getMealName(),
//                                            mealDetails.getStrMealThumbnail(),
//                                            mealDetails.getMealCategory(),
//                                            mealDetails.getMealArea(),
//                                            mealDetails.getMealInstructions()
//                                    );
//                                    mealPlan.setTimestamp(firestorePlan.getTimestamp());
//                                    mealsRepository.insertMealToMealPlan(mealPlan);
//
//                                    Log.d(TAG, "Successfully synced meal: " + mealDetails.getMealName());
//                                    callback.onComplete(true);
//                                },
//                                error -> {
//                                    Log.e(TAG, "Error syncing meal: " + firestorePlan.getMealId(), error);
//
//                                    // Don't show individual errors to avoid spamming the user
//                                    // Just log them and mark as complete so we can continue
//                                    callback.onComplete(false);
//                                }
//                        )
//        );
//    }

    private interface SyncCallback {
        void onComplete(boolean success);
    }
}