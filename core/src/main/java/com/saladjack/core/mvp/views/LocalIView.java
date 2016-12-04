package com.saladjack.core.mvp.views;

import com.saladjack.core.bean.Album;
import com.saladjack.core.bean.Artist;
import com.saladjack.core.bean.Song;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/10/4
 * @desciption: 本地视图的接口
 */

public interface LocalIView {

    interface LocalMusic{
        void getLocalMusic(List<Song> songs);
    }

    interface LocalAlbum{
        void getLocalAlbum(List<Album> alba);
    }

    interface LocalArtist{
        void getLocalArtist(List<Artist> artists);
    }
}
