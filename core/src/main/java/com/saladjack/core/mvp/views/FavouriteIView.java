package com.saladjack.core.mvp.views;

import com.saladjack.core.bean.WikiBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/9/27
 * @desciption: 专辑收藏的view接口
 */

public interface FavouriteIView {
    void getWikiBean(List<WikiBean> wikis, boolean add, boolean hasMore);

    void fail(String msg);
}
