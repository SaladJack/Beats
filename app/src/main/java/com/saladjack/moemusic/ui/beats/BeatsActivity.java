package com.saladjack.moemusic.ui.beats;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.saladjack.core.bean.AccountBean;
import com.saladjack.core.bean.Song;
import com.saladjack.core.http.HttpUtil;
import com.saladjack.core.mvp.views.BeatsIView;
import com.saladjack.core.utils.MoeLogger;
import com.saladjack.moemusic.MoeApplication;
import com.saladjack.moemusic.R;
import com.saladjack.moemusic.music.MusicPlayerManager;
import com.saladjack.moemusic.music.MusicPlaylist;
import com.saladjack.moemusic.music.OnSongChangedListener;
import com.saladjack.moemusic.ui.account.LoginActivity;
import com.saladjack.moemusic.ui.adapters.BeatsFragmentAdapter;
import com.saladjack.moemusic.ui.setting.AboutActivity;
import com.saladjack.moemusic.ui.setting.SettingActivity;
import com.saladjack.moemusic.ui.widgets.CircleImageView;
import com.saladjack.moemusic.ui.widgets.floatingmusicmenu.FloatingMusicMenu;
import com.lapism.searchview.SearchView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class BeatsActivity extends SearchActivity implements NavigationView.OnNavigationItemSelectedListener, BeatsIView, OnSongChangedListener {

    private DrawerLayout drawerLayout;
    private BeatsPresenter beatsPresenter;
    private NavigationView navigationView;
    private CircleImageView avatar;
    private CircleImageView userImg;
    private TextView nicknameTv, aboutTv;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    //FloatingMusicButton
    private FloatingMusicMenu musicMenu;
    private FloatingActionButton playingBtn, modeBtn, detailBtn, nextBtn;
    private Subscription progressSub;
    private Song curSong;
    private boolean isPause;

    private BeatsFragmentAdapter beatsFragmentAdapter;
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beats);
        beatsPresenter = new BeatsPresenter(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer_home);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        View shadowView = findViewById(R.id.toolbar_shadow);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shadowView.setVisibility(View.GONE);
        }

        View iconLayout = findViewById(R.id.icon_layout);
        iconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        beatsFragmentAdapter = new BeatsFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(beatsFragmentAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(beatsFragmentAdapter.getCount());
        tabLayout.setupWithViewPager(viewPager);

        initDrawer();
        tryGetData();

        initFloatingMusicButton();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initSearch();
    }

    private void initSearch() {
        setSearchView();
        customSearchView(true);
        searchView.setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
            @Override
            public void onOpen() {
                if (musicMenu != null) {
                    musicMenu.hide();
                }
            }

            @Override
            public void onClose() {
                if (musicMenu != null) {
                    musicMenu.show();
                }
            }
        });
    }

    private void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_string, R.string.close_string) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (searchView != null && searchView.isSearchOpen()) {
                    searchView.close(true);
                }
                if (musicMenu != null) {
                    musicMenu.hide();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (musicMenu != null) {
                    musicMenu.show();
                }
            }
        };
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        userImg = (CircleImageView) findViewById(R.id.user_icon);

        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        avatar = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.drawer_avatar);
        nicknameTv = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_nickname);
        aboutTv = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_about);
    }

    private void initData(AccountBean accountBean) {
        if (accountBean == null) return;
        Glide.with(this)
                .load(accountBean.getUser_avatar().getMedium())
                .placeholder(R.drawable.ic_navi_user)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        userImg.setImageDrawable(resource);
                    }
                });
        Glide.with(this)
                .load(accountBean.getUser_avatar().getLarge())
                .placeholder(R.drawable.ic_navi_user)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        avatar.setImageDrawable(resource);
                    }
                });
        String nickname = accountBean.getUser_nickname();
        if (TextUtils.isEmpty(nickname)) {
            nickname = accountBean.getUser_name();
        }
        nicknameTv.setText(nickname);
        if (TextUtils.isEmpty(accountBean.getAbout())) {
            aboutTv.setVisibility(View.GONE);
        } else {
            aboutTv.setVisibility(View.VISIBLE);
            aboutTv.setText(accountBean.getAbout());
        }
    }

    private void tryGetData() {
        beatsPresenter.getAccountDetail();
    }

    private void initFloatingMusicButton() {
        MusicPlayerManager.get().registerListener(this);
        musicMenu = (FloatingMusicMenu) findViewById(R.id.fmm);
        playingBtn = (FloatingActionButton) findViewById(R.id.fab_play);
        playingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MusicPlayerManager.get().getPlayingSong() == null) {
                    beatsPresenter.requestSongs();
                    showIndeterminateProgressDialog(true);
                } else if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PLAYING) {
                    MusicPlayerManager.get().pause();
                } else {
                    MusicPlayerManager.get().play();
                }
            }
        });
        modeBtn = (FloatingActionButton) findViewById(R.id.fab_mode);
        setPlayMode(MusicPlayerManager.get().getPlayMode());
        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int playMode = MusicPlayerManager.get().switchPlayMode();
                setPlayMode(playMode);
            }
        });
        nextBtn = (FloatingActionButton) findViewById(R.id.fab_next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MusicPlayerManager.get().getMusicPlaylist() != null)
                    MusicPlayerManager.get().playNext();
                else {
                    showToast(R.string.music_playlist_next_null);
                }
            }
        });
        detailBtn = (FloatingActionButton) findViewById(R.id.fab_player);
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MusicPlayerManager.get().getPlayingSong() != null) {
                    gotoSongPlayerActivity();
                    musicMenu.collapse();
                } else {
                    showToast(R.string.music_playing_none);
                }
            }
        });
        updateProgress();
        updatePlayStatus();
        updateSong(MusicPlayerManager.get().getPlayingSong());
    }

    private void showIndeterminateProgressDialog(boolean horizontal) {
        dialog = new MaterialDialog.Builder(this)
                .title(R.string.music_explore_title)
                .content(R.string.music_explore_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(horizontal)
                .show();
    }

    private void updateProgress() {
        progressSub = Observable.interval(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        float progress = MusicPlayerManager.get().getCurrentPosition() * 1.0f / MusicPlayerManager.get().getCurrentMaxDuration() * 100;
                        musicMenu.setProgress(progress);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        MoeLogger.e(throwable.toString());
                    }
                });
    }

    public void setPlayMode(int playMode) {
        if (playMode == MusicPlayerManager.CYCLETYPE) {
            //showToast(R.string.music_mode_cycle);
            modeBtn.setImageResource(R.drawable.ic_play_repeat);
        } else if (playMode == MusicPlayerManager.SINGLETYPE) {
            modeBtn.setImageResource(R.drawable.ic_play_repeat_one);
            //showToast(R.string.music_mode_single);
        } else if (playMode == MusicPlayerManager.RANDOMTYPE) {
            modeBtn.setImageResource(R.drawable.ic_play_shuffle);
            //showToast(R.string.music_mode_random);
        }
    }

    @Override
    public void onSongChanged(Song song) {
        updateSong(song);
    }

    @Override
    public void onPlayBackStateChanged(PlaybackStateCompat state) {
        updatePlayStatus();
    }

    private void updatePlayStatus() {
        if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PLAYING) {
            playingBtn.setImageResource(R.drawable.ic_play);
            musicMenu.rotateStart();
        } else if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PAUSED) {
            playingBtn.setImageResource(R.drawable.ic_pause);
            musicMenu.rotateStop();
        }
    }

    private void updateSong(final Song song) {
        if (song == null) {
            musicMenu.rotateStop();
            musicMenu.setMusicCover(getResources().getDrawable(R.drawable.moefou));
            curSong = null;
            playingBtn.setImageResource(R.drawable.ic_pause);
            return;
        }
        if (!TextUtils.isEmpty(song.getCoverUrl()) && !isPause) {
            Glide.with(this)
                    .load(song.getCoverUrl())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            musicMenu.setMusicCover(resource);
                            curSong = song;
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_beats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search) {
            searchView.open(true, item);
            return true;
        }
        if (id == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_playing) {
            if (!gotoSongPlayerActivity())
                return true;
        } else if (id == R.id.nav_playlist) {
            PlayListActivity.open(this);
        } else if (id == R.id.nav_download) {
            DownloadActivity.open(this);
        } else if (id == R.id.nav_library) {
            LocalMusicActivity.open(this);
        } else if (id == R.id.nav_setting) {
            SettingActivity.open(this);
        } else if (id == R.id.nav_about) {
            AboutActivity.open(this);
        } else if (id == R.id.nav_feedback) {
            sendFeedbackMail();
        }
        //关闭侧滑栏
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendFeedbackMail() {
        try {
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse(getString(R.string.feedback_sendto_mail)));
            data.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title));
            data.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_question));
            startActivity(data);
        } catch (Exception e) {
            MoeLogger.e(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        isPause = false;
        if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PLAYING) {
            if (curSong != MusicPlayerManager.get().getPlayingSong()) {
                updateSong(MusicPlayerManager.get().getPlayingSong());
            }
            musicMenu.rotateStart();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        isPause = true;
        musicMenu.rotateStop();
        super.onPause();
    }

    @Override
    public void getRandomSongs(List<Song> songs) {
        if (dialog != null) {
            dialog.dismiss();
        }
        MusicPlaylist musicPlaylist = new MusicPlaylist(songs);
        musicPlaylist.setTitle(getString(R.string.music_find));
        MusicPlayerManager.get().playQueue(musicPlaylist, 0);
        gotoSongPlayerActivity();
    }

    @Override
    public void getSongsFail(String msg) {
        if (dialog != null) {
            dialog.dismiss();
        }
        showToast(R.string.music_explore_fail);
    }

    @Override
    public void setUserDetail(AccountBean accountBean) {
        initData(accountBean);
    }

    @Override
    public void getUserFail(String msg) {
        // 用户验证失败时需要重新登录
        if (msg.equals(HttpUtil.UNAUTHORIZED)) {
            showSnackBar(viewPager,msg);
            LoginActivity.open(this);
            finish();
        } else {
            initData(MoeApplication.getInstance().getAccountBean());
            showSnackBar(viewPager,msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayerManager.get().unregisterListener(this);
        progressSub.unsubscribe();
    }
}
