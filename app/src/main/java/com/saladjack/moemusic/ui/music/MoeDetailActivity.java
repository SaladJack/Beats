package com.saladjack.moemusic.ui.music;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.saladjack.core.bean.FavBean;
import com.saladjack.core.bean.Song;
import com.saladjack.core.bean.WikiBean;
import com.saladjack.core.bean.event.FavEvent;
import com.saladjack.core.http.RxBus;
import com.saladjack.core.mvp.views.MusicPlayIView;
import com.saladjack.core.utils.MoeLogger;
import com.saladjack.moemusic.R;
import com.saladjack.moemusic.music.MusicPlayerManager;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/7/20
 * @desciption: 萌否信息详情页
 */
public class MoeDetailActivity extends MusicDetailActivity implements MusicPlayIView {

    public static void open(Context context, WikiBean wiki) {
        context.startActivity(getIntent(context, wiki));
    }

    public static Intent getIntent(Context context, WikiBean wiki) {
        Intent intent = new Intent();
        intent.setClass(context, MoeDetailActivity.class);
        intent.putExtra("wiki", wiki);
        return intent;
    }

    private WikiBean wikiBean;
    private MusicPlayPresenter mpPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wikiBean = (WikiBean) getIntent().getSerializableExtra("wiki");
        if (wikiBean == null) {
            showSnackBar(refreshView, R.string.music_message_error);
            finish();
        }
        mpPresenter = new MusicPlayPresenter(this, wikiBean.getWiki_type());
        mpPresenter.parseWiki(wikiBean);
    }


    @Override
    public void wikiDetail(long wikiId, Spanned title, Spanned description, boolean fav) {
        MoeLogger.d(wikiId + "");
        setMusicDetail(title, description, fav);
        if (MusicPlayerManager.get().getMusicPlaylist() != null && MusicPlayerManager.get().getMusicPlaylist().getAlbumId() == wikiId) {
            isPlayingAlbum = true;
        }
    }

    @Override
    public void favMusic(boolean fav) {
        RxBus.getDefault().post(new FavEvent(wikiBean.getWiki_id(), fav));
        if (fav) {
            wikiBean.setWiki_user_fav(new FavBean());
        } else {
            wikiBean.setWiki_user_fav(null);
        }
        super.favMusic(fav);
    }

    @Override
    public void wikiCover(Bitmap coverBitmap) {
        setMusicCover(coverBitmap);
    }

    @Override
    public void songs(List<Song> songs) {
        musicPlaylist.setAlbumId(wikiBean.getWiki_id());
        musicPlaylist.setTitle(wikiBean.getWiki_title());
        setSongList(songs);
    }

    @Override
    public void onSongChanged(Song song) {
        super.onSongChanged(song);
        isPlayingAlbum = MusicPlayerManager.get().getMusicPlaylist() != null && MusicPlayerManager.get().getMusicPlaylist().getAlbumId() == wikiBean.getWiki_id();
    }

    @Override
    public void fail(String msg) {
        refreshView.notifySwipeFinish();
    }


    @Override
    public void onSwipeRefresh() {
        mpPresenter.getSongs(wikiBean.getWiki_id());
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_fav:
                if (wikiBean.getWiki_user_fav() == null) {
                    showFavDialog();
                } else {
                    showUnfavDialog();
                }
                break;
        }
        super.onClick(v);
    }

    public void showFavDialog() {
        new MaterialDialog.Builder(this)
                .title(wikiBean.getWiki_title())
                .content(R.string.music_fav)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(0, 50)
                .positiveText(R.string.fav)
                .negativeText(R.string.cancel)
                .input(getString(R.string.evaluation), "", true, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        mpPresenter.favMusic(wikiBean.getWiki_id(), input.toString());
                    }
                }).show();
    }

    public void showUnfavDialog() {
        new MaterialDialog.Builder(this)
                .title(wikiBean.getWiki_title())
                .content(R.string.music_unfav)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mpPresenter.unFavMusic(wikiBean.getWiki_id());
                    }
                })
                .show();
    }
}