package com.saladjack.core.mvp.views;

import com.saladjack.core.bean.PixivBean;

import java.util.List;

/**
 * @author: saladjack
 * @Date: 2016/10/28.
 * @description:
 */

public interface PixivIView {
    void getPixivPicture(List<PixivBean> pixivBeen);

    void fail(String msg);
}
