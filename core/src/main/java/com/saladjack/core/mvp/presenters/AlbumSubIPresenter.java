package com.saladjack.core.mvp.presenters;

import com.saladjack.core.bean.WikiSubBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/8/18
 * @desciption: 曲目列表
 */
public interface AlbumSubIPresenter {
    void getAlbumSubs(List<WikiSubBean> subs, int curPage, int count);

    void fail(String msg);
}
