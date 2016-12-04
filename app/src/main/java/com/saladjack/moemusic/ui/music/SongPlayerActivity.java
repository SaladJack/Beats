package com.saladjack.moemusic.ui.music;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;
import com.saladjack.core.bean.Song;
import com.saladjack.core.cache.SongDownloadListener;
import com.saladjack.core.cache.SongManager;
import com.saladjack.core.utils.DateUtils;
import com.saladjack.core.utils.MoeLogger;
import com.saladjack.moemusic.R;
import com.saladjack.moemusic.music.MusicPlayerManager;
import com.saladjack.moemusic.music.OnSongChangedListener;
import com.saladjack.moemusic.permission.OnPermissionsDeniedListener;
import com.saladjack.moemusic.permission.OnPermissionsGrantedListener;
import com.saladjack.moemusic.permission.PermissionBuilder;
import com.saladjack.moemusic.permission.PermissionManager;
import com.saladjack.moemusic.ui.PermissionActivity;
import com.saladjack.moemusic.ui.widgets.CircleImageView;
import com.saladjack.moemusic.ui.widgets.CircularSeekBar;
import com.saladjack.moemusic.ui.widgets.timely.TimelyView;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * @author: saladjack
 * @date: 2016/8/24
 * @desciption: 播放器界面
 */
public class SongPlayerActivity extends PermissionActivity implements OnSongChangedListener, View.OnClickListener, SongDownloadListener {

    public static void open(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SongPlayerActivity.class);
        context.startActivity(intent);
    }

    private GLAudioVisualizationView visualizationView;
    private Toolbar toolbar;
    private CircularSeekBar circularSeekBar;
    private CircleImageView circleCover;
    private TimelyView hourTv, min1Tv, min2Tv, sec1Tv, sec2Tv;
    private TextView hourColon;
    private TextView titleTv;
    private ImageView randomImg, previousImg, nextImg, downloadImg;
    private FloatingActionButton playBtn;
    private Song song;

    private boolean isPaused;
    private Subscription progressSub, timerSub;
    private int[] times = new int[]{-1, -1, -1, -1, -1};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MusicPlayerManager.get().registerListener(this);

        initView();
        initData();
        updateProgress();
        updateData();
        SongManager.getInstance().registerDownloadListener(this);
    }

    private void initData() {
        song = MusicPlayerManager.get().getPlayingSong();
        if (song == null) {
            MoeLogger.e("没有找到正在播放的歌曲");
            finish();
        }
        if (!TextUtils.isEmpty(song.getAlbumName())) {
            String title = song.getAlbumName();
            Spanned t = Html.fromHtml(title);
            getSupportActionBar().setTitle(t);
        }
        int playMode = MusicPlayerManager.get().getPlayMode();
        if (playMode == MusicPlayerManager.CYCLETYPE) {
            randomImg.setImageResource(R.drawable.ic_play_repeat);
        } else if (playMode == MusicPlayerManager.SINGLETYPE) {
            randomImg.setImageResource(R.drawable.ic_play_repeat_one);
        } else if (playMode == MusicPlayerManager.RANDOMTYPE) {
            randomImg.setImageResource(R.drawable.ic_play_shuffle);
        }
        if (song.getDownload() == Song.DOWNLOAD_COMPLETE) {
            downloadImg.setImageResource(R.drawable.ic_play_download_complete);
        }
    }

    private void initView() {
        circularSeekBar = (CircularSeekBar) findViewById(R.id.circle_seekbar);
        circleCover = (CircleImageView) findViewById(R.id.song_cover);
        hourTv = (TimelyView) findViewById(R.id.timely_hour);
        min1Tv = (TimelyView) findViewById(R.id.timely_min1);
        min2Tv = (TimelyView) findViewById(R.id.timely_min2);
        sec1Tv = (TimelyView) findViewById(R.id.timely_sec1);
        sec2Tv = (TimelyView) findViewById(R.id.timely_sec2);
        hourColon = (TextView) findViewById(R.id.hour_colon);
        titleTv = (TextView) findViewById(R.id.song_title);
        randomImg = (ImageView) findViewById(R.id.song_mode);
        previousImg = (ImageView) findViewById(R.id.song_previous);
        nextImg = (ImageView) findViewById(R.id.song_next);
        downloadImg = (ImageView) findViewById(R.id.song_download);
        playBtn = (FloatingActionButton) findViewById(R.id.song_play);

        //seekbar
        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MusicPlayerManager.get().seekTo(progress);
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });

        //背景频谱
        visualizationView = (GLAudioVisualizationView) findViewById(R.id.visualizer_view);

        randomImg.setOnClickListener(this);
        previousImg.setOnClickListener(this);
        nextImg.setOnClickListener(this);
        downloadImg.setOnClickListener(this);
        playBtn.setOnClickListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        createPermissionBuilderAndRequest(
                PermissionManager.PERMISSION_RECODE_CODE,
                R.string.permission_record_rationale,
                R.string.permission_record_rationale_again,
                new OnPermissionsGrantedListener() {
                    @Override
                    public void onPermissionsGranted(PermissionBuilder builder, List<String> perms) {
                        visualizationView.linkTo(DbmHandler.Factory.newVisualizerHandler(SongPlayerActivity.this, MusicPlayerManager.get().getMediaPlayer().getAudioSessionId()));
                    }
                }, new OnPermissionsDeniedListener() {
                    @Override
                    public void onPermissionsDenied(PermissionBuilder builder, List<String> perms) {
                        showSnackBar(circleCover, R.string.permission_record_denied);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, PermissionManager.PERMISSION_RECORD_AUDIO);
    }

    private void updateData() {
        if (!TextUtils.isEmpty(song.getAlbumName())) {
            String title = song.getAlbumName();
            Spanned t = Html.fromHtml(title);
            getSupportActionBar().setTitle(t);
        }
        titleTv.setText(song.getTitle());

        //歌曲封面
        String coverUrl = song.getCoverUrl();
        if (!isPaused) {
            Glide.with(this)
                    .load(coverUrl)
                    .placeholder(R.drawable.cover)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            circleCover.setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            circleCover.setImageDrawable(errorDrawable);
                            super.onLoadFailed(e, errorDrawable);
                        }
                    });
        }
    }

    private void updateProgress() {
        progressSub = Observable.interval(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        circularSeekBar.setMax(MusicPlayerManager.get().getCurrentMaxDuration());
                        circularSeekBar.setProgress(MusicPlayerManager.get().getCurrentPosition());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        MoeLogger.e(throwable.toString());
                    }
                });
        timerSub = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        setTime(MusicPlayerManager.get().getCurrentPosition());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        MoeLogger.e(throwable.toString());
                    }
                });
    }

    public void setTime(int progress) {
        String time = DateUtils.getTime(progress).toString();
        int length = time.length();
        if (time.length() == 5) {
            hourTv.setVisibility(View.GONE);
            hourColon.setVisibility(View.GONE);
            changeDigit(min1Tv, 1, time.charAt(0) - '0');
            changeDigit(min2Tv, 2, time.charAt(1) - '0');
            changeDigit(sec1Tv, 3, time.charAt(3) - '0');
            changeDigit(sec2Tv, 4, time.charAt(4) - '0');
        } else if (time.length() > 5) {
            hourColon.setVisibility(View.VISIBLE);
            hourTv.setVisibility(View.VISIBLE);
            changeDigit(sec2Tv, 4, time.charAt(length - 1) - '0');
            changeDigit(sec1Tv, 3, time.charAt(length - 2) - '0');
            changeDigit(min2Tv, 2, time.charAt(length - 4) - '0');
            changeDigit(min1Tv, 1, time.charAt(length - 5) - '0');
            changeDigit(hourTv, 0, time.charAt(length - 7) - '0');
        }
    }

    public void changeDigit(TimelyView tv, int position, int end) {
        int digit = times[position];
        if (digit == end)
            return;
        try {
            ObjectAnimator obja = tv.animate(digit, end);
            obja.setDuration(400);
            obja.start();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
        times[position] = end;
    }

    @Override
    public void onSongChanged(Song song) {
        this.song = song;
        updateData();
    }

    @Override
    public void onPlayBackStateChanged(PlaybackStateCompat state) {
        updatePlayStatus();
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.song_mode:
                int playMode = MusicPlayerManager.get().switchPlayMode();
                if (playMode == MusicPlayerManager.CYCLETYPE) {
                    showToast(R.string.music_mode_cycle);
                    randomImg.setImageResource(R.drawable.ic_play_repeat);
                } else if (playMode == MusicPlayerManager.SINGLETYPE) {
                    randomImg.setImageResource(R.drawable.ic_play_repeat_one);
                    showToast(R.string.music_mode_single);
                } else if (playMode == MusicPlayerManager.RANDOMTYPE) {
                    randomImg.setImageResource(R.drawable.ic_play_shuffle);
                    showToast(R.string.music_mode_random);
                }
                break;
            case R.id.song_previous:
                MusicPlayerManager.get().playPrev();
                break;
            case R.id.song_next:
                MusicPlayerManager.get().playNext();
                break;
            case R.id.song_download:
                createPermissionBuilderAndRequest(PermissionManager.PERMISSION_STORAGE_CODE,
                        R.string.permission_storage_rationale,
                        R.string.permission_storage_rationale_again,
                        new OnPermissionsGrantedListener() {
                            @Override
                            public void onPermissionsGranted(PermissionBuilder builder, List<String> perms) {
                                int status = SongManager.getInstance().download(song);
                                if (status == Song.DOWNLOAD_NONE) {
                                    showToast(R.string.song_download_fail);
                                } else if (status == Song.DOWNLOAD_COMPLETE) {
                                    showToast(R.string.song_download_complete);
                                } else if (status == Song.DOWNLOAD_ING) {
                                    showToast(R.string.song_downloading);
                                } else if (status == Song.DOWNLOAD_DISABLE) {
                                    showToast(R.string.song_download_disable);
                                } else if (status == Song.DOWNLOAD_WITH_WIFI) {
                                    showSnackBar(view, R.string.song_download_wifi);
                                }
                            }
                        },
                        new OnPermissionsDeniedListener() {
                            @Override
                            public void onPermissionsDenied(PermissionBuilder builder, List<String> perms) {
                                showSnackBar(view, R.string.permission_storage_denied);
                            }
                        },
                        null,
                        PermissionManager.PERMISSION_STORAGE
                );
                break;
            case R.id.song_play:
                if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PLAYING) {
                    MusicPlayerManager.get().pause();
                } else if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PAUSED) {
                    MusicPlayerManager.get().play();
                }
                break;
        }
    }

    private void updatePlayStatus() {
        if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PLAYING) {
            playBtn.setImageResource(R.drawable.ic_play);
        } else if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PAUSED) {
            playBtn.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isPaused = false;
        visualizationView.onResume();
        updateData();
    }

    @Override
    public void onPause() {
        isPaused = true;
        visualizationView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        visualizationView.release();
        super.onDestroy();
        MusicPlayerManager.get().unregisterListener(this);
        SongManager.getInstance().unRegisterDownloadListener(this);
        progressSub.unsubscribe();
        timerSub.unsubscribe();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDownloadProgress(Song song, int soFarBytes, int totalBytes) {

    }

    @Override
    public void onError(Song song, Throwable e) {
        if (song.getId() == this.song.getId()) {
            showToast(R.string.song_download_fail);
        }
    }

    @Override
    public void onCompleted(Song song) {
        if (song.getId() == this.song.getId()) {
            showToast(R.string.song_download_complete);
            downloadImg.setImageResource(R.drawable.ic_play_download_complete);
        }
    }

    @Override
    public void onWarn(Song song) {

    }
}
