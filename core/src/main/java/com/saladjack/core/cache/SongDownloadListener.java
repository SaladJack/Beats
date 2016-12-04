package com.saladjack.core.cache;

import com.saladjack.core.bean.Song;

/**
 * @author: saladjack
 * @date: 2016/9/19
 * @desciption: 歌曲下载监听器
 */
public interface SongDownloadListener {
    void onDownloadProgress(Song song, int soFarBytes, int totalBytes);

    void onError(Song song, Throwable e);

    void onCompleted(Song song);

    void onWarn(Song song);
}
