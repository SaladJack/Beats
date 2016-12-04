package com.saladjack.moemusic.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.saladjack.moemusic.MoeApplication;
import com.saladjack.moemusic.R;
import com.saladjack.moemusic.music.MusicPlayerManager;
import com.saladjack.moemusic.ui.music.SongPlayerActivity;

/**
 * @author: saladjack
 * @Date: 2016/6/25.
 * @description: 所有Activity的父类
 */
public abstract class AbstractAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //启动音乐服务
        MusicPlayerManager.startServiceIfNecessary(getApplicationContext());
        MoeApplication.getInstance().addActivity(this);
    }

    /**
     * snackbar的显示
     *
     * @param toast
     */
    public void showSnackBar(View view, String toast) {
        Snackbar.make(view, toast, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnackBar(View view, @StringRes int toast) {
        Snackbar.make(view, getString(toast), Snackbar.LENGTH_SHORT).show();
    }

    public void showToast(int toastRes) {
        Toast.makeText(this, getString(toastRes), Toast.LENGTH_SHORT).show();
    }

    /**
     * activity的跳转
     *
     * @param activity
     */
    public void startActivity(Class activity) {
        Intent i = new Intent();
        i.setClass(this, activity);
        startActivity(i);
    }

    public boolean gotoSongPlayerActivity() {
        if (MusicPlayerManager.get().getPlayingSong() == null) {
            showToast(R.string.music_playing_none);
            return false;
        }
        SongPlayerActivity.open(this);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MoeApplication.getInstance().removeActivity(this);
    }
}
