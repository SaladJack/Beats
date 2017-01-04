package com.saladjack.core;

import android.app.Application;

import com.saladjack.core.utils.CrashHandler;
import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.leakcanary.LeakCanary;


/**
 * Global application
 *
 * @author: saladjack
 * @Date: 2015/10/24 0024-上午 9:59
 */
public class CoreApplication extends Application {

    /**
     * application singleton
     */
    private static CoreApplication instance;

    public static CoreApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if(LeakCanary.isInAnalyzerProcess(this)) return;
        LeakCanary.install(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        
        instance = this;
        FileDownloader.init(getApplicationContext());
    }

}
