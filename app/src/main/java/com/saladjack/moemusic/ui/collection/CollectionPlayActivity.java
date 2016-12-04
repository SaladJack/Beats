package com.saladjack.moemusic.ui.collection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.saladjack.core.bean.CollectionBean;
import com.saladjack.core.bean.Song;
import com.saladjack.core.db.CollectionManager;
import com.saladjack.core.mvp.views.CollectionPlayIView;
import com.saladjack.moemusic.R;
import com.saladjack.moemusic.music.MusicPlayerManager;
import com.saladjack.moemusic.ui.music.MusicDetailActivity;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/9/30
 * @desciption: 收藏夹播放列表
 */
public class CollectionPlayActivity extends MusicDetailActivity implements CollectionPlayIView {

    public static void open(Context context, CollectionBean collection) {
        context.startActivity(getIntent(context, collection));
    }

    public static Intent getIntent(Context context, CollectionBean collection) {
        Intent intent = new Intent();
        intent.setClass(context, CollectionPlayActivity.class);
        intent.putExtra("collection", collection);
        return intent;
    }

    private CollectionBean collection;
    private CollectionPlayPresenter cpPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //移除收藏按钮
        removeFav();

        collection = (CollectionBean) getIntent().getSerializableExtra("collection");
        if (collection == null) {
            showSnackBar(refreshView,R.string.music_message_error);
            finish();
        }

        cpPresenter = new CollectionPlayPresenter(this, collection);
    }


    @Override
    public void collectionDetail(int collectionId, Spanned title, Spanned description) {
        getSupportActionBar().setTitle(title);
        setMusicDetail(title, description);
        if (MusicPlayerManager.get().getMusicPlaylist() != null && MusicPlayerManager.get().getMusicPlaylist().getAlbumId() == collectionId) {
            isPlayingAlbum = true;
        }
    }

    @Override
    public void collectionCover(Bitmap coverBitmap) {
        setMusicCover(coverBitmap);
    }

    @Override
    public void getSongs(List<Song> songs) {
        musicPlaylist.setAlbumId(collection.getId());
        musicPlaylist.setTitle(collection.getTitle());
        setSongList(songs);
    }

    @Override
    public void fail() {
        refreshView.notifySwipeFinish();
    }

    @Override
    public void onSwipeRefresh() {
        cpPresenter.init();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onSongChanged(Song song) {
        super.onSongChanged(song);
        isPlayingAlbum = MusicPlayerManager.get().getMusicPlaylist() != null && MusicPlayerManager.get().getMusicPlaylist().getAlbumId() == collection.getId();
    }

    @Override
    protected void showPopupMenu(final View v, final Song song, final int position) {
        final PopupMenu menu = new PopupMenu(this, v);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_song_play:
                        playSong(position);
                        break;
                    case R.id.popup_song_addto_playlist:
                        addToPlaylist(song);
                        break;
                    case R.id.popup_song_delete:
                        CollectionManager.getInstance().deleteCollectionShip(collection.getId(), (int) song.getId());
                        refreshView.notifySwipeFinish();
                        cpPresenter.refresh();
                        break;
                    case R.id.popup_song_download:
                        downloadSong(v,song);
                        break;
                }
                return false;
            }
        });
        menu.inflate(R.menu.popup_collection_song_setting);
        menu.show();
    }
}
