package com.saladjack.moemusic.music;

import android.support.v4.media.session.PlaybackStateCompat;

import com.saladjack.core.bean.Song;

/**
 * @author: saladjack
 * @date: 2016/7/19
 * @desciption: 歌曲变化监听器
 */
public interface OnSongChangedListener {
    void onSongChanged(Song song);

    void onPlayBackStateChanged(PlaybackStateCompat state);
}
