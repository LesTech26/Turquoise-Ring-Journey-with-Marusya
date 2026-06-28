package com.example.biruse_kolco.ui.splash;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.biruse_kolco.R;
import com.example.biruse_kolco.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3000;
    private Handler handler = new Handler();
    private TextView tvTitle, tvTitleSecond, tvTitleThird;
    private TextView tvSubtitle, tvSubtitleSecond, tvVersion;
    private View dot1, dot2, dot3, loadingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvTitle = findViewById(R.id.tv_splash_title);
        tvTitleSecond = findViewById(R.id.tv_splash_title_second);
        tvTitleThird = findViewById(R.id.tv_splash_title_third);
        tvSubtitle = findViewById(R.id.tv_splash_subtitle);
        tvSubtitleSecond = findViewById(R.id.tv_splash_subtitle_second);
        tvVersion = findViewById(R.id.tv_splash_version);
        loadingContainer = findViewById(R.id.loading_container);
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        startAnimations();

        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DELAY);
    }

    private void startAnimations() {
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());

        // Анимация заголовка
        ObjectAnimator titleTransY = ObjectAnimator.ofFloat(tvTitle, "translationY", 40f, 0f);
        ObjectAnimator titleAlpha = ObjectAnimator.ofFloat(tvTitle, "alpha", 0f, 1f);
        AnimatorSet titleAnim = new AnimatorSet();
        titleAnim.playTogether(titleTransY, titleAlpha);
        titleAnim.setDuration(700);
        titleAnim.setStartDelay(200);

        ObjectAnimator title2TransY = ObjectAnimator.ofFloat(tvTitleSecond, "translationY", 40f, 0f);
        ObjectAnimator title2Alpha = ObjectAnimator.ofFloat(tvTitleSecond, "alpha", 0f, 1f);
        AnimatorSet title2Anim = new AnimatorSet();
        title2Anim.playTogether(title2TransY, title2Alpha);
        title2Anim.setDuration(700);
        title2Anim.setStartDelay(300);

        ObjectAnimator title3TransY = ObjectAnimator.ofFloat(tvTitleThird, "translationY", 40f, 0f);
        ObjectAnimator title3Alpha = ObjectAnimator.ofFloat(tvTitleThird, "alpha", 0f, 1f);
        AnimatorSet title3Anim = new AnimatorSet();
        title3Anim.playTogether(title3TransY, title3Alpha);
        title3Anim.setDuration(700);
        title3Anim.setStartDelay(400);

        // Анимация слогана
        ObjectAnimator subTransY = ObjectAnimator.ofFloat(tvSubtitle, "translationY", 30f, 0f);
        ObjectAnimator subAlpha = ObjectAnimator.ofFloat(tvSubtitle, "alpha", 0f, 1f);
        AnimatorSet subAnim = new AnimatorSet();
        subAnim.playTogether(subTransY, subAlpha);
        subAnim.setDuration(600);
        subAnim.setStartDelay(500);

        ObjectAnimator sub2TransY = ObjectAnimator.ofFloat(tvSubtitleSecond, "translationY", 30f, 0f);
        ObjectAnimator sub2Alpha = ObjectAnimator.ofFloat(tvSubtitleSecond, "alpha", 0f, 1f);
        AnimatorSet sub2Anim = new AnimatorSet();
        sub2Anim.playTogether(sub2TransY, sub2Alpha);
        sub2Anim.setDuration(600);
        sub2Anim.setStartDelay(600);

        // Анимация точек загрузки
        ObjectAnimator loadingAlpha = ObjectAnimator.ofFloat(loadingContainer, "alpha", 0f, 1f);
        loadingAlpha.setDuration(400);
        loadingAlpha.setStartDelay(700);

        // Анимация версии
        ObjectAnimator versionAlpha = ObjectAnimator.ofFloat(tvVersion, "alpha", 0f, 0.5f);
        versionAlpha.setDuration(400);
        versionAlpha.setStartDelay(800);

        animSet.play(titleAnim)
                .with(title2Anim)
                .with(title3Anim)
                .with(subAnim)
                .with(sub2Anim)
                .with(loadingAlpha)
                .with(versionAlpha);
        animSet.start();

        startDotAnimation();
    }

    private void startDotAnimation() {
        if (dot1 == null || dot2 == null || dot3 == null) return;

        dot1.animate()
                .alpha(1f)
                .setDuration(400)
                .withEndAction(() -> dot1.animate().alpha(0.2f).setDuration(400).start());

        dot2.animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(200)
                .withEndAction(() -> dot2.animate().alpha(0.2f).setDuration(400).start());

        dot3.animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(400)
                .withEndAction(() -> dot3.animate().alpha(0.2f).setDuration(400).start());

        handler.postDelayed(this::startDotAnimation, 1200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}