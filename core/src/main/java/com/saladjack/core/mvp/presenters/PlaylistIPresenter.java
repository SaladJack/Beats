package com.saladjack.core.mvp.presenters;

import com.saladjack.core.bean.Song;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/8/31
 * @desciption: 播放列表
 */
public interface PlaylistIPresenter {

    void getPlayList(List<Song> songs);

    void getPlaylistFail(String msg);

}
