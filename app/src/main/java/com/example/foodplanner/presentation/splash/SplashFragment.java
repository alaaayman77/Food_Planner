package com.example.foodplanner.presentation.splash;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.example.foodplanner.R;
import com.example.foodplanner.utility.UserPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashFragment extends Fragment {

    private static final long   SPLASH_DELAY  = 3000;
    private static final String PREFS_NAME    = "app_prefs";
    private static final String KEY_ONBOARDED = "onboarding_complete";

    private FirebaseAuth    mAuth;
    private UserPrefManager preferencesManager;

    public SplashFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth              = FirebaseAuth.getInstance();
        preferencesManager = new UserPrefManager(requireContext());

        View logo = view.findViewById(R.id.logo);
        logo.setScaleX(0.3f);
        logo.setScaleY(0.3f);
        logo.setAlpha(0f);

        startLogoAnimation(logo);
    }

    private void startLogoAnimation(View logo) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0.3f, 1.25f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0.3f, 1.25f, 0.95f, 1f);
        ObjectAnimator alpha  = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
        ObjectAnimator drop   = ObjectAnimator.ofFloat(logo, "translationY", -50f, 20f, 0f);

        scaleX.setDuration(900);
        scaleY.setDuration(900);
        alpha.setDuration(900);
        drop.setDuration(900);

        scaleX.setInterpolator(new OvershootInterpolator(3f));
        scaleY.setInterpolator(new OvershootInterpolator(3f));
        alpha.setInterpolator(new AccelerateDecelerateInterpolator());
        drop.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleX.start();
        scaleY.start();
        alpha.start();
        drop.start();

        drop.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                startFloatingAnimation(logo);
                navigateToNextScreen();
            }
        });
    }

    private void startFloatingAnimation(View view) {
        ObjectAnimator animator =
                ObjectAnimator.ofFloat(view, "translationY", 0f, -15f, 0f);
        animator.setDuration(2000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    // ── Navigation logic (FIXED) ───────────────────────────────────────────────
    private void navigateToNextScreen() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (getView() == null || !isAdded()) return;

            NavController navController = Navigation.findNavController(getView());

            boolean      isLoggedInPref = preferencesManager.isUserLoggedIn();
            FirebaseUser currentUser    = mAuth.getCurrentUser();

            // ── CASE 1: Fully logged in (pref + Firebase both confirm) ─────────
            // Go directly to home, skip onboarding and auth entirely
            if (isLoggedInPref && currentUser != null) {
                navController.navigate(R.id.action_splashFragment_to_authFragment);
                return;
            }

            if (isLoggedInPref) {
                preferencesManager.clearUserData();
            }

            // ── CASE 3: Not logged in — first time ever or returning guest ──────
            SharedPreferences prefs = requireContext()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            boolean hasSeenOnboarding = prefs.getBoolean(KEY_ONBOARDED, false);

            if (hasSeenOnboarding) {

                navController.navigate(R.id.action_splashFragment_to_authFragment);
            } else {

                navController.navigate(R.id.action_splashFragment_to_onboardingFragment);
            }

        }, SPLASH_DELAY);
    }
}