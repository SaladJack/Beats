package com.saladjack.core.mvp.views;

/**
 * @author: saladjack
 * @date: 2016/7/5
 * @desciption: 登录的view接口
 */
public interface LoginIView {
    void OauthRedirect(String url);
    void LoginSuccess();
    void LoginFailed();
    void LoginFailed(String s);
}
