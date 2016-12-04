package com.saladjack.core.mvp.views;

import com.saladjack.core.bean.BannerBean;
import com.saladjack.core.bean.WikiBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/8/1
 * @desciption: 专辑的view接口
 */
public interface AlbumIView {

    void getMusics(List<WikiBean> newMusics,List<WikiBean> hotMusics);

    void getBanner(List<BannerBean> been);

    void loadFail(String msg);
}
