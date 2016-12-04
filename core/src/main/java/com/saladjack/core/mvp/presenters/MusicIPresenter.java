package com.saladjack.core.mvp.presenters;

import com.saladjack.core.bean.WikiBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/8/1
 * @desciption: 专辑的presenter接口
 */
public interface MusicIPresenter {

    void getMusics(List<WikiBean> newMusics,List<WikiBean> hotMusics);

    void loadMusicFail(String msg);

}
