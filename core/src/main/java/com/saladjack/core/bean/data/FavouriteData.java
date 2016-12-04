package com.saladjack.core.bean.data;

import com.saladjack.core.bean.FavBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/9/27
 * @desciption: 收藏数据
 */

public class FavouriteData extends ResponseData<Object, Object> {

    private List<FavBean> favs;

    public List<FavBean> getFavs() {
        return favs;
    }

    public void setFavs(List<FavBean> favs) {
        this.favs = favs;
    }
}
