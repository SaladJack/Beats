package com.saladjack.moemusic.ui.beats;

import com.saladjack.core.action.AccountDetailAction;
import com.saladjack.core.action.PlayListAction;
import com.saladjack.core.bean.AccountBean;
import com.saladjack.core.bean.Song;
import com.saladjack.core.cache.SettingManager;
import com.saladjack.core.db.dao.AccountDao;
import com.saladjack.core.http.HttpUtil;
import com.saladjack.core.http.RetrofitManager;
import com.saladjack.core.mvp.presenters.BeatsIPresenter;
import com.saladjack.core.mvp.presenters.PlaylistIPresenter;
import com.saladjack.core.mvp.views.BeatsIView;
import com.saladjack.moemusic.MoeApplication;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/7/7
 * @desciption: 主页逻辑处理
 */
public class BeatsPresenter implements BeatsIPresenter, PlaylistIPresenter {

    private static final int AUTH_LOGIN_COUNT = 3;

    private BeatsIView beatsIView;
    private AccountDetailAction detailAction;
    private PlayListAction playListAction;

    private int tryLogin = 0;

    public BeatsPresenter(BeatsIView beatsIView) {
        this.beatsIView = beatsIView;
        detailAction = new AccountDetailAction(this);
    }

    public void getAccountDetail() {
        detailAction.getAccount();
    }

    public void requestSongs() {
        playListAction = new PlayListAction(this);
        playListAction.getPlayList();
    }

    @Override
    public void setUserDetail(AccountBean accountBean) {
        beatsIView.setUserDetail(accountBean);
        SettingManager.getInstance().setSetting(SettingManager.ACCOUNT_ID, accountBean.getUid());
        MoeApplication.getInstance().setAccountBean(accountBean);
        AccountDao accountDao = new AccountDao();
        accountDao.updateAccount(accountBean);
        accountDao.close();
    }

    @Override
    public void getUserFail(String msg) {
        if (msg.equals(HttpUtil.UNAUTHORIZED)) {
            //退出登录
            SettingManager.getInstance().clearAccount();
            RetrofitManager.getInstance().clear();
        } else {
            beatsIView.getUserFail(msg);
        }
    }

    @Override
    public void getPlayList(List<Song> songs) {
        beatsIView.getRandomSongs(songs);
    }

    @Override
    public void getPlaylistFail(String msg) {
        beatsIView.getSongsFail(msg);
    }
}
