package com.saladjack.core.mvp.presenters;

import com.saladjack.core.bean.AccountBean;

/**
 * @author: saladjack
 * @date: 2016/7/7
 * @desciption: 首页p接口
 */
public interface BeatsIPresenter {
    void setUserDetail(AccountBean accountBean);
    void getUserFail(String msg);
}
