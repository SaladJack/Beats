package com.saladjack.moemusic.ui.collection;

import com.saladjack.core.action.FavouriteAction;
import com.saladjack.core.bean.FavBean;
import com.saladjack.core.bean.WikiBean;
import com.saladjack.core.mvp.presenters.FavouriteIPresenter;
import com.saladjack.core.mvp.views.FavouriteIView;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/9/27
 * @desciption: 收藏逻辑处理
 */

public class FavouritePresenter implements FavouriteIPresenter {

    private FavouriteIView favouriteView;
    private FavouriteAction favouriteAction;
    private int curPage;
    private boolean add, hasMore;


    public FavouritePresenter(FavouriteIView favouriteView) {
        this.favouriteView = favouriteView;
        favouriteAction = new FavouriteAction(this);
        add = false;
        hasMore = false;
        curPage = 0;
    }

    public void requestFavWikis() {
        requestFavWikis(1);
    }

    public void loadMore() {
        curPage++;
        requestFavWikis(curPage);
    }

    public void requestFavWikis(int page) {
        curPage = page;
        favouriteAction.getFavWikis(page);
    }

    @Override
    public void getWikis(List<WikiBean> wikiBeanList) {
        for (WikiBean bean : wikiBeanList) {
            if (bean.getWiki_user_fav() == null) {
                bean.setWiki_user_fav(new FavBean());
            }
        }
        favouriteView.getWikiBean(wikiBeanList, add, hasMore);
    }

    @Override
    public void wikiFail(String msg) {
        favouriteView.fail(msg);
    }

    @Override
    public void updateCount(int curPage, int perPage, int total) {
        this.curPage = curPage;
        add = curPage != 1;
        hasMore = curPage * perPage <= total;
    }
}
