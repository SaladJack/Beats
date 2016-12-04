package com.saladjack.core.mvp.presenters;

import com.saladjack.core.bean.RelationshipBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/8/23
 * @desciption: 电台列表接口
 */
public interface RadioSubIPresenter {

    void getRadioSubs(List<RelationshipBean> subs);

    void fail(String msg);
}
