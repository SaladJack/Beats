package com.saladjack.moemusic.ui.setting;

import com.saladjack.core.CoreApplication;
import com.saladjack.core.action.VersionAction;
import com.saladjack.core.bean.VersionBean;
import com.saladjack.core.cache.SettingManager;
import com.saladjack.core.http.RetrofitManager;
import com.saladjack.core.mvp.presenters.VersionIPresenter;
import com.saladjack.core.mvp.views.SettingIView;
import com.saladjack.core.utils.FileUtils;
import com.saladjack.core.utils.MoeLogger;
import com.saladjack.core.utils.SystemParamsUtils;
import com.saladjack.moemusic.MoeApplication;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

/**
 * @author: saladjack
 * @date: 2016/10/24
 * @desciption: 设置界面逻辑
 */

public class SettingPresenter implements VersionIPresenter {

    private SettingIView settingView;
    private VersionAction versionAction;

    public SettingPresenter(SettingIView settingIView) {
        this.settingView = settingIView;
        versionAction = new VersionAction(this);
    }

    public void initSetting() {
        boolean notify = SettingManager.getInstance().getSetting(SettingManager.SETTING_NOTIFY, true);
        boolean wifi = SettingManager.getInstance().getSetting(SettingManager.SETTING_WIFI, false);
        settingView.setting(notify, wifi);
    }

    public void setNotify(boolean notify) {
        SettingManager.getInstance().setSetting(SettingManager.SETTING_NOTIFY, notify);
    }

    public void setWifi(boolean wifi) {
        SettingManager.getInstance().setSetting(SettingManager.SETTING_WIFI, wifi);
    }

    public void update() {
        versionAction.getVersion();
    }

    public void logout(){
        //退出登录
        SettingManager.getInstance().clearAccount();
        RetrofitManager.getInstance().clear();
        MoeApplication.getInstance().closeAllActivity();
    }

    @Override
    public void getVersion(VersionBean versionBean) {
        int code = versionBean.getVersion_code();
        int curVersion = SystemParamsUtils.getAppVersionCode(CoreApplication.getInstance());
        if (code > curVersion) {
            settingView.updateApk(true, versionBean.getApk(), versionBean.getDescription());
        } else {
            settingView.updateApk(false, null, null);
        }
    }

    /**
     * 下载apk
     * @param apkUrl
     */
    public void apkDownload(String apkUrl) {
        BaseDownloadTask task = FileDownloader.getImpl().create(apkUrl);
        task.setPath(FileUtils.getApkPath());
        task.setListener(new FileDownloadSampleListener() {
            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                MoeLogger.d("apk download:" + soFarBytes * 100 / totalBytes);
                int progress = soFarBytes * 100 / totalBytes;
                settingView.downloadProgress(progress);
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                super.completed(task);
                settingView.downloadProgress(100);
                File file = new File(task.getPath());
                FileUtils.openApk(CoreApplication.getInstance(), file);
            }
        });
        task.start();

    }

    @Override
    public void fail(String msg) {
        settingView.updateApk(false, null, null);
    }
}
