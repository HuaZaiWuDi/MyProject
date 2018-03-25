package com.embednet.wdluo.bleplatformsdkdemo;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.clj.fastble.BleManager;
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

    private static Application application;


    @Override
    public void onCreate() {
        super.onCreate();
        RxToast.init(this);
        application = this;
        initBLE();
        initBugly();
        initBmob();


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


    private void initBLE() {
        bleManager = BleManager.getInstance();
        bleManager.init(this);

        if (!bleManager.isSupportBle()) {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        bleManager.enableBluetooth();
//        bleManager.disableBluetooth();
        bleManager.enableLog(true);//是否开启蓝牙日志
        bleManager.setMaxConnectCount(1);
        bleManager.setOperateTimeout(5000);//设置超时时间
    }

}
