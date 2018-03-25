package com.embednet.wdluo.bleplatformsdkdemo.ble;

import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;

import java.util.UUID;

import laboratory.dxy.jack.com.jackupdate.ui.RxToast;
import laboratory.dxy.jack.com.jackupdate.util.HexUtil;

import static com.embednet.wdluo.bleplatformsdkdemo.MyApplication.bleManager;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/24
 */
public class BleTools {
    private static BleTools bleTools;

    public static BleDevice bleDevice;

//    public static final UUID CHARACTERISTIC_CHANDEG = UUID.fromString("00001523-1212-efde-1523-785feabcd123");
//    private static final String CHARACTERISTIC = "00001532-1212-efde-1523-785feabcd123";


//    public static final String SERVICE_DFU="00001530-1212-efde-1523-785feabcd123";


    public static final String SERVICE_RW = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String CHAR_R = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String CHAR_W = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    public static final String SERVICE_BATTERY = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_BATTERY = "00002a19-0000-1000-8000-00805f9b34fb";


    public static synchronized BleTools getInstance() {
        if (bleTools == null) {
            bleTools = new BleTools();
        }
        return bleTools;
    }


    public void writeBle(byte[] bytes) {
        if (bleDevice == null) return;
        bleManager.write(bleDevice, SERVICE_RW, CHAR_W, bytes, new BleWriteCallback() {
            @Override
            public void onWriteSuccess() {
                L.d("写成功");
//                readBle();
            }

            @Override
            public void onWriteFailure(BleException exception) {
                RxToast.error("发送失败");
                L.d("发送失败：" + exception.toString());
            }
        });


    }


    public void readBle() {
        if (bleDevice == null) return;
        bleManager.read(bleDevice, SERVICE_RW, CHAR_R, new BleReadCallback() {
            @Override
            public void onReadSuccess(byte[] data) {
                L.d("读成功：" + HexUtil.encodeHexStr(data));
            }

            @Override
            public void onReadFailure(BleException exception) {
                L.e("读 失败：" + exception.toString());
            }
        });
    }


    public void openNotify() {
        if (bleDevice == null) return;
        bleManager.notify(bleDevice, SERVICE_RW, CHAR_R, new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {
                L.d("打开 openNotify 成功");
            }

            @Override
            public void onNotifyFailure(BleException exception) {
                L.e("打开 openNotify 失败：" + exception.toString());
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                L.d("特征值改变：" + HexUtil.encodeHexStr(data));
            }
        });
    }

    public void openIndicate() {
        if (bleDevice == null) return;
        bleManager.indicate(bleDevice, SERVICE_BATTERY, CHAR_BATTERY, new BleIndicateCallback() {
            @Override
            public void onIndicateSuccess() {
                L.d("打开 indicate 成功");
            }

            @Override
            public void onIndicateFailure(BleException exception) {
                L.e("打开 indicate 失败:" + exception.toString());
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                L.d("特征值改变：" + HexUtil.encodeHexStr(data));
            }
        });
    }


    public void readRssi() {
        if (bleDevice == null) return;
        bleManager.readRssi(bleDevice, new BleRssiCallback() {
            @Override
            public void onRssiFailure(BleException exception) {

            }

            @Override
            public void onRssiSuccess(int rssi) {

            }
        });
    }


    public void configScan() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(new UUID[]{CHARACTERISTIC_CHANDEG})      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, names)   		// 只扫描指定广播名的设备，可选
//                .setDeviceMac()                  // 只扫描指定mac的设备，可选
//                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(-1)
                // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();

        bleManager.initScanRule(scanRuleConfig);

    }

}
