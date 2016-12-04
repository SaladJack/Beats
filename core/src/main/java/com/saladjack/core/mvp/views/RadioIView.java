package com.saladjack.core.mvp.views;

import com.saladjack.core.bean.WikiBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/8/17
 * @desciption: 发现电台
 */
public interface RadioIView {
    void getMusics(List<WikiBean> hotRadios,List<WikiBean> newRadios);

    void loadMusicFail(String msg);
}
