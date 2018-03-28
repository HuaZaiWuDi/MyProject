package com.embednet.wdluo.bleplatformsdkdemo;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.embednet.wdluo.bleplatformsdkdemo.cache.ACache;
import com.tencent.bugly.Bugly;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
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

    }

    private void initBmob() {
        //第一：默认初始化
//        BmobSMS.initialize(this, BMOB_APPID);
        Bmob.initialize(this, Constants.BMOB_APPID);

    }

    static BmobUser bmobUser;

    public static synchronized BmobUser getBmobUser() {
        if (bmobUser == null) {
            bmobUser = new BmobUser();
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
            Toast.makeText(application, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        bleManager.enableBluetooth();
//        bleManager.disableBluetooth();
        bleManager.enableLog(true);//是否开启蓝牙日志
        bleManager.setMaxConnectCount(1);
        bleManager.setOperateTimeout(5000);//设置超时时间
    }

}
