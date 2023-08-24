package com.example.scannerr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {
    public static int BAR_LOADING_TIME;

    private ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        SecureRandom random = new SecureRandom();
        BAR_LOADING_TIME = random.nextInt(2000) + 3000;

        // Changing the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_blue));

        loadingBar = findViewById(R.id.loading_bar);

        showBarLoading();
    }

    private void showBarLoading() {
        ObjectAnimator progressBarAnimator = ObjectAnimator.ofInt(loadingBar, "progress", 0, 100);
        progressBarAnimator.setDuration(BAR_LOADING_TIME);
        progressBarAnimator.setInterpolator(new LinearInterpolator());
        progressBarAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent);
                finish();
            }
        });

        progressBarAnimator.start();
    }
}

