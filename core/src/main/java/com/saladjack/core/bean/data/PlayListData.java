package com.saladjack.core.bean.data;

import com.saladjack.core.bean.SongBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/8/31
 * @desciption: 播放列表
 */
public class PlayListData extends ResponseData<Object, Object> {
    private List<SongBean> playlist;

    public List<SongBean> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<SongBean> playlist) {
        this.playlist = playlist;
    }
}
