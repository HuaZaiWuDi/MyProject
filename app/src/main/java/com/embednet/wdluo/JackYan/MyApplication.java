package com.embednet.wdluo.JackYan;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.embednet.wdluo.JackYan.cache.ACache;
import com.tencent.bugly.Bugly;

import cn.bmob.sms.BmobSMS;
import lab.dxythch.com.netlib.rx.RxManager;
import laboratory.dxy.jack.com.jackupdate.ui.RxToast;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/11
 */
public class MyApplication extends MultiDexApplication {

    public static BleManager bleManager;
    public static ACache aCache;

    private static Application application;


    @Override
    public void onCreate() {
        super.onCreate();
        RxToast.init(this);
        application = this;
        initBLE();
        initBugly();
        initBmob();
        aCache = ACache.get(this);
        RxManager.getInstance().setAPPlication(this);
    }

    private void initBmob() {
        //第一：默认初始化
        BmobSMS.initialize(this, Constants.BMOB_APPID);
//        Bmob.initialize(this, Constants.BMOB_APPID);

    }

    static BmobSMS bmobUser;

    public static synchronized BmobSMS getBmobUser() {
        if (bmobUser == null) {
            bmobUser = new BmobSMS();
        }
        return bmobUser;
    }


    public static Application getApplication() {
        return application;
    }

    private void initBugly() {
        Bugly.init(getApplicationContext(), Constants.BUGLY_APP_ID, BuildConfig.DEBUG);
    }


    public static void initBLE() {
        bleManager = BleManager.getInstance();
        bleManager.init(application);

        if (!bleManager.isSupportBle()) {
            Toast.makeText(application, R.string.unSupportBle, Toast.LENGTH_SHORT).show();
            return;
        }
        bleManager.enableBluetooth();
//        bleManager.disableBluetooth();
        bleManager.enableLog(true);//是否开启蓝牙日志
        bleManager.setMaxConnectCount(1);
        bleManager.setOperateTimeout(5000);//设置超时时间
    }

}
