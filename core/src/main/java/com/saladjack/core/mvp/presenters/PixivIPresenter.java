package com.saladjack.core.mvp.presenters;

import com.saladjack.core.bean.PixivBean;

import java.util.List;

/**
 * @author: saladjack
 * @Date: 2016/10/28.
 * @description: p站接口
 */

public interface PixivIPresenter {

    void getPixivPics(List<PixivBean> pixivBeen);

    void fail(String msg);
}
