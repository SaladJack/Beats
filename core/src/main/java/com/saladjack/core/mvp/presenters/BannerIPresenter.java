package com.saladjack.core.mvp.presenters;

import com.saladjack.core.bean.BannerBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/10/17
 * @desciption:
 */

public interface BannerIPresenter {

    void getBanners(List<BannerBean> been);

    void fail(String msg);
}
