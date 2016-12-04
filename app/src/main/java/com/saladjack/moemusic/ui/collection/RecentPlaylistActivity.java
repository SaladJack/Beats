package com.saladjack.moemusic.ui.collection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.afollestad.materialdialogs.MaterialDialog;
import com.saladjack.core.bean.CollectionBean;
import com.saladjack.core.bean.Song;
import com.saladjack.core.db.CollectionManager;
import com.saladjack.core.http.RxBus;
import com.saladjack.moemusic.R;
import com.saladjack.core.bean.event.CollectionUpdateEvent;
import com.saladjack.moemusic.music.MusicPlayerManager;
import com.saladjack.moemusic.music.MusicPlaylist;
import com.saladjack.moemusic.music.MusicRecentPlaylist;
import com.saladjack.moemusic.music.OnSongChangedListener;
import com.saladjack.moemusic.ui.PermissionActivity;
import com.saladjack.moemusic.ui.adapters.CollectionAdapter;
import com.saladjack.moemusic.ui.adapters.OnItemClickListener;
import com.saladjack.moemusic.ui.adapters.RecentPlayAdapter;

import rx.functions.Action1;

/**
 * @author: saladjack
 * @date: 2016/9/26
 * @desciption: 最近播放界面
 */

public class RecentPlaylistActivity extends PermissionActivity implements OnSongChangedListener {

    public static void open(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, RecentPlaylistActivity.class);
        context.startActivity(intent);
    }

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecentPlayAdapter recentAdapter;

    private MusicPlaylist musicPlaylist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recently_playlist);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MusicPlayerManager.get().registerListener(this);

        initRecyclerView();

    }

    private void initRecyclerView() {
        recentAdapter = new RecentPlayAdapter(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recentAdapter);
        recentAdapter.setData(MusicRecentPlaylist.getInstance().getQueue());
        musicPlaylist = new MusicPlaylist(MusicRecentPlaylist.getInstance().getQueue());
        recentAdapter.setSongClickListener(new OnItemClickListener<Song>() {
            @Override
            public void onItemClick(Song song, int position) {
                MusicPlayerManager.get().playQueue(musicPlaylist, position);
                gotoSongPlayerActivity();
            }

            @Override
            public void onItemSettingClick(View v, Song song, int position) {
                showPopupMenu(v, song, position);
            }
        });
    }

    private void showPopupMenu(final View v, final Song song, final int position) {

        final PopupMenu menu = new PopupMenu(this, v);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_song_play:
                        MusicPlayerManager.get().playQueue(musicPlaylist, position);
                        gotoSongPlayerActivity();
                        break;
                    case R.id.popup_song_addto_playlist:
                        MusicPlaylist mp = MusicPlayerManager.get().getMusicPlaylist();
                        if (mp == null) {
                            mp = new MusicPlaylist();
                            MusicPlayerManager.get().setMusicPlaylist(mp);
                        }
                        mp.addSong(song);
                        break;
                    case R.id.popup_song_fav:
                        showCollectionDialog(song);
                        break;
                    case R.id.popup_song_download:
                        downloadSong(v,song);
                        break;
                }
                return false;
            }
        });
        menu.inflate(R.menu.popup_recently_playlist_setting);
        menu.show();
    }

    /**
     * 显示选择收藏夹列表的弹窗
     *
     * @param song
     */
    public void showCollectionDialog(final Song song) {
        CollectionAdapter collectionAdapter = new CollectionAdapter(this, true);
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.collection_dialog_selection_title)
                .adapter(collectionAdapter, new LinearLayoutManager(this))
                .build();
        collectionAdapter.setItemClickListener(new OnItemClickListener<CollectionBean>() {
            @Override
            public void onItemClick(CollectionBean item, int position) {
                CollectionManager.getInstance().insertCollectionShipAsync(item, song, new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        dialog.dismiss();
                        showToast(aBoolean ? R.string.collect_song_success : R.string.collect_song_fail);
                        RxBus.getDefault().post(new CollectionUpdateEvent(aBoolean));//通知首页收藏夹数据变化
                    }
                });

            }

            @Override
            public void onItemSettingClick(View v, CollectionBean item, int position) {

            }
        });
        dialog.show();
    }

    @Override
    public void onSongChanged(Song song) {
        recentAdapter.setData(MusicRecentPlaylist.getInstance().getQueue());
        musicPlaylist = new MusicPlaylist(MusicRecentPlaylist.getInstance().getQueue());
    }

    @Override
    public void onPlayBackStateChanged(PlaybackStateCompat state) {

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
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayerManager.get().unregisterListener(this);
    }

}
