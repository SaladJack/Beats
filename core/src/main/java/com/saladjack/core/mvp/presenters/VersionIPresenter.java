package com.saladjack.core.mvp.presenters;

import com.saladjack.core.bean.VersionBean;

/**
 * @author: saladjack
 * @date: 2016/10/24
 * @desciption: 版本更新
 */

public interface VersionIPresenter {

    void getVersion(VersionBean versionBean);

    void fail(String msg);
}
