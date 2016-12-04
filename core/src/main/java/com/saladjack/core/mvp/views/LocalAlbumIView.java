package com.saladjack.core.mvp.views;

import android.graphics.Bitmap;
import android.text.Spanned;

import com.saladjack.core.bean.Song;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/10/20
 * @desciption: 本地专辑详情信息
 */

public interface LocalAlbumIView {

    void localAlbumDetail(long albumId, Spanned title, Spanned description);

    void albumCover(Bitmap cover);

    void songs(List<Song> songs);

    void fail(String msg);
}
