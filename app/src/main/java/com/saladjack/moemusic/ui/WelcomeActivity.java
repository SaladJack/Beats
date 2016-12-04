package com.saladjack.moemusic.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.saladjack.core.cache.SettingManager;
import com.saladjack.moemusic.R;
import com.saladjack.moemusic.ui.beats.BeatsActivity;
import com.saladjack.moemusic.ui.account.LoginActivity;

public class WelcomeActivity extends AbstractAppActivity {

    private ImageView welcomeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcomeView = (ImageView) findViewById(R.id.logo);
        startAnimation();
    }

    public void startAnimation() {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(welcomeView, "scaleX", 0f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(welcomeView, "scaleY", 0f, 1f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(welcomeView, "alpha", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(alphaAnimator).with(scaleXAnimator).with(scaleYAnimator);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hasLogin();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void hasLogin() {
        if (SettingManager.getInstance().getSetting(SettingManager.ACCOUNT_ID, -1) == -1)
            startActivity(LoginActivity.class);
        else startActivity(BeatsActivity.class);
        finish();
    }
}
