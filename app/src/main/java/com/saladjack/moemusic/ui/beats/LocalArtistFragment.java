package com.saladjack.moemusic.ui.beats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.saladjack.core.bean.Song;
import com.saladjack.moemusic.MoeApplication;
import com.saladjack.moemusic.R;
import com.saladjack.moemusic.music.MusicPlayerManager;
import com.saladjack.moemusic.music.MusicPlaylist;
import com.saladjack.moemusic.ui.BaseFragment;

/**
 * @author: saladjack
 * @Date: 2016/9/21.
 * @description: 本地艺术家界面，由于缺少头像数据，暂时空缺
 */

public class LocalArtistFragment extends BaseFragment {

    public static final String TITLE = MoeApplication.getInstance().getString(R.string.local_artist_fragment_title);

    private RecyclerView recyclerView;

    public static LocalArtistFragment newInstance() {
        LocalArtistFragment fragment = new LocalArtistFragment();
        return fragment;
    }

    public LocalArtistFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View parentView = inflater.inflate(R.layout.fragment_local_music, container, false);
        initRecyclerView(parentView);
        return parentView;
    }

    private void initRecyclerView(View rootView) {
        //downloadAdapter = new DownloadCompleteAdapter(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recyclerView.setAdapter(downloadAdapter);
        recyclerView.setItemAnimator(null);

        /*downloadAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Song song, int position) {
                MusicPlayerManager.get().playQueueItem(position);
            }

            @Override
            public void onItemSettingClick(View v, Song song, int position) {
                showPopupMenu(v, song, position);
            }
        });*/
    }

    private void showPopupMenu(View v, final Song song, final int position) {

        final PopupMenu menu = new PopupMenu(getActivity(), v);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_song_addto_playlist:
                        MusicPlaylist mp = MusicPlayerManager.get().getMusicPlaylist();
                        if (mp == null) {
                            mp = new MusicPlaylist();
                            MusicPlayerManager.get().setMusicPlaylist(mp);
                        }
                        mp.addSong(song);
                        break;
                    case R.id.popup_song_fav:
                        break;
                }
                return false;
            }
        });
        menu.inflate(R.menu.popup_downloaded_setting);
        menu.show();
    }


}
