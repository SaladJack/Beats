package com.saladjack.core.mvp.presenters;

/**
 * @author: saladjack
 * @date: 2016/8/22
 * @desciption: 收藏数据接口
 */
public interface FavIPresenter {
    void favSuccess();

    void unFavSuccess();

    void fail( String msg);
}
