package com.saladjack.moemusic.ui.music;

import com.saladjack.core.action.ExploreAction;
import com.saladjack.core.action.WikiAction;
import com.saladjack.core.bean.WikiBean;
import com.saladjack.core.mvp.presenters.MusicIPresenter;
import com.saladjack.core.mvp.presenters.WikiIPresenter;
import com.saladjack.core.mvp.views.RadioIView;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/8/17
 * @desciption: 电台推荐
 */
public class RadioPresenter implements MusicIPresenter, WikiIPresenter {

    private RadioIView radioView;
    private ExploreAction exploreAction;
    private WikiAction wikiAction;

    public RadioPresenter(RadioIView radioView) {
        this.radioView = radioView;
        exploreAction = new ExploreAction(this);
        wikiAction = new WikiAction(this);
    }

    public void requestRadios() {
        exploreAction.getRadioIndex();
        wikiAction.getWikis(WikiBean.WIKI_RADIO, 1);
    }

    @Override
    public void getMusics(List<WikiBean> hotRadios, List<WikiBean> musics) {
        radioView.getMusics(hotRadios, musics);
    }

    @Override
    public void loadMusicFail(String msg) {
        radioView.loadMusicFail(msg);
    }

    @Override
    public void getWikis(List<WikiBean> wikiBeanList) {
        radioView.getMusics(null, wikiBeanList);
    }

    @Override
    public void wikiFail(String msg) {
        radioView.loadMusicFail(msg);
    }

    @Override
    public void updateCount(int curPage, int perpage, int total) {

    }
}
