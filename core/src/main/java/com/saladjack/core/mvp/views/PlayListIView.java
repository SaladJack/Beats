package com.saladjack.core.mvp.views;

import com.saladjack.core.bean.Album;
import com.saladjack.core.bean.WikiBean;

/**
 * @author: saladjack
 * @Date: 2016/10/28.
 * @description:
 */

public interface PlayListIView {
    void getAlbum(boolean moeAlbum, WikiBean wiki, Album album);

    void fail(String msg);
}
