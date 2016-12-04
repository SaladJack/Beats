package com.saladjack.core.mvp.presenters;

import com.saladjack.core.bean.WikiBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/9/27
 * @desciption: 收藏的action接口
 */

public interface FavouriteIPresenter {

    void getWikis(List<WikiBean> wikiBeanList);

    void wikiFail(String msg);

    void updateCount(int curPage, int perpage, int total);
}
