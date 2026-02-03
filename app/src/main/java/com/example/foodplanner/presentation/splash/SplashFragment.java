package com.example.foodplanner.presentation.splash;

import android.animation.ObjectAnimator;
import android.graphics.Typeface;
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
import android.widget.TextView;

import com.example.foodplanner.R;
import com.google.firebase.auth.FirebaseAuth;


public class SplashFragment extends Fragment {
    private Typeface tf;
    private TextView slogan;
    private FirebaseAuth mAuth;
    private static final long SPLASH_DELAY = 3000;
    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();



        View logo = view.findViewById(R.id.logo);

        logo.setScaleX(0.3f);
        logo.setScaleY(0.3f);
        logo.setAlpha(0f);

        startLogoAnimation(logo);
    }
    private void startLogoAnimation(View logo) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0.3f, 1.25f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0.3f, 1.25f, 0.95f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
        ObjectAnimator drop = ObjectAnimator.ofFloat(logo, "translationY", -50f, 20f, 0f);

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

    private void navigateToNextScreen() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (getView() != null) {
                NavController navController = Navigation.findNavController(getView());
                navController.navigate(R.id.action_splashFragment_to_authFragment);

            }
        }, SPLASH_DELAY);
    }
}