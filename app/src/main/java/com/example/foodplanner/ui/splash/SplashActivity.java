package com.example.foodplanner.ui.splash;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.foodplanner.R;

public class SplashActivity extends AppCompatActivity {
Typeface tf;
TextView slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


         tf = ResourcesCompat.getFont(this, R.font.poppins_bold);
         slogan = findViewById(R.id.tv_slogan);
        if (tf != null) slogan.setTypeface(tf);
        View logo = findViewById(R.id.logo);

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
}
