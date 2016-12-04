package com.saladjack.moemusic.ui.beats;

import com.saladjack.core.action.WikiAction;
import com.saladjack.core.bean.Album;
import com.saladjack.core.bean.Song;
import com.saladjack.core.bean.WikiBean;
import com.saladjack.core.cache.LocalMusicLibrary;
import com.saladjack.core.mvp.presenters.WikiIPresenter;
import com.saladjack.core.mvp.views.PlayListIView;
import com.saladjack.moemusic.MoeApplication;

import java.util.List;

/**
 * @author: saladjack
 * @Date: 2016/10/28.
 * @description:
 */

public class PlayListPresenter implements WikiIPresenter {

    private PlayListIView playListView;
    private WikiAction wikiAction;

    public PlayListPresenter(PlayListIView playListView) {
        this.playListView = playListView;
        wikiAction = new WikiAction(this);
    }

    public void requestAlbum(Song song) {
        if (song.getId() > 0) {
            String wikiType = WikiBean.WIKI_MUSIC + "," + WikiBean.WIKI_RADIO;
            wikiAction.getWikiById(wikiType, song.getAlbumId());
        } else {
            Album album = LocalMusicLibrary.getAlbum(MoeApplication.getInstance(), song.getAlbumId());
            playListView.getAlbum(false, null, album);
        }
    }

    @Override
    public void getWikis(List<WikiBean> wikiBeanList) {
        if (wikiBeanList != null && wikiBeanList.size() > 0) {
            playListView.getAlbum(true, wikiBeanList.get(0), null);
        }
    }

    @Override
    public void wikiFail(String msg) {
        playListView.fail(msg);
    }

    @Override
    public void updateCount(int curPage, int perpage, int total) {

    }
}
