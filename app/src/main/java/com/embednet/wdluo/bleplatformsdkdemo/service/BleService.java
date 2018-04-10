package com.embednet.wdluo.bleplatformsdkdemo.service;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.IBinder;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.MyApplication;
import com.embednet.wdluo.bleplatformsdkdemo.ble.BleTools;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;

import laboratory.dxy.jack.com.jackupdate.util.B;

import static com.embednet.wdluo.bleplatformsdkdemo.MyApplication.bleManager;

public class BleService extends Service {
    public BleService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d("onStartCommand");


        if (!bleManager.isConnected(BleTools.bleDevice))
            setAutoConnect();
        return START_STICKY;
    }

    int TotalCount = 3;
    int currentCount = 0;


    private void setAutoConnect() {
        if (BleTools.bleDevice != null)
            BleManager.getInstance().connect(BleTools.bleDevice, new BleGattCallback() {
                @Override
                public void onStartConnect() {
                    L.d("开始连接");
                }

                @Override
                public void onConnectFail(BleException exception) {

                    L.d("连接失败");
                    currentCount++;
                    if (currentCount > TotalCount) {
                        currentCount = 0;
                        bleManager.destroy();
                        bleManager = null;
                        MyApplication.initBLE();
                        B.broadUpdate(BleService.this, Constants.ACTIVE_CONNECT_STATUE, Constants.EXTRA_CONNECT_STATUE, false);
                        L.d("连接关闭，回收资源");
                    } else
                        bleManager.connect(BleTools.bleDevice, this);
                }

                @Override
                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    L.d("连接成功");
                    currentCount = 0;
                    B.broadUpdate(BleService.this, Constants.ACTIVE_CONNECT_STATUE, Constants.EXTRA_CONNECT_STATUE, true);
                    BleTools.getInstance().openNotify();
                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                    L.d(isActiveDisConnected ? "主动断开连接" : "自动断开连接");

                    if (!isActiveDisConnected) {
                        bleManager.connect(device, this);
                    }
                }
            });
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        L.d("onDestroy");
//        重启自己----应用保活
        L.d("重启自己  BleService");
        Intent intent = new Intent(getApplicationContext(), BleService.class);
        startService(intent);
        bleManager.removeConnectGattCallback(BleTools.bleDevice);
        bleManager.destroy();
        bleManager = null;

        super.onDestroy();
    }
}
