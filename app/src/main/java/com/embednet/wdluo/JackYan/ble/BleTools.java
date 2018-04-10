package com.embednet.wdluo.JackYan.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.utils.HexUtil;
import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.ble.listener.BleChartChangeCallBack;
import com.embednet.wdluo.JackYan.util.L;

import java.util.UUID;

import laboratory.dxy.jack.com.jackupdate.ui.RxToast;

import static com.embednet.wdluo.JackYan.MyApplication.bleManager;
import static com.embednet.wdluo.JackYan.MyApplication.getApplication;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/24
 */
public class BleTools {
    private static BleTools bleTools;

    public static BleDevice bleDevice;
    private Handler TimeOut = new Handler();

    public static final UUID CHARACTERISTIC_CHANDEG = UUID.fromString("00001523-1212-efde-1523-785feabcd123");
    private static final String CHARACTERISTIC = "00001523-1212-efde-1523-785feabcd123";


    public static synchronized BleTools getInstance() {
        if (bleTools == null) {
            bleTools = new BleTools();
        }
        return bleTools;
    }

    private BleChartChangeCallBack bleChartChange;
    private byte[] bytes;
    private final int reWriteCount = 3;    //重连次数
    private int currentCount = 0;          //当前次数
    private final int timeOut = 3000;          //当前次数

    private Runnable reWrite = new Runnable() {
        @Override
        public void run() {
            L.d("重新写");
            currentCount++;
            if (currentCount > reWriteCount) {
                RxToast.error(getApplication().getString(R.string.doFail));
                currentCount = 0;
            } else
                writeBle(bytes, bleChartChange);
        }
    };

    public void writeBle(final byte[] bytes, final BleChartChangeCallBack bleChartChange) {
        if (bleDevice == null || !bleManager.isConnected(bleDevice)) {
            RxToast.warning(getApplication().getString(R.string.disconnect));
            return;
        }
        this.bleChartChange = bleChartChange;
        this.bytes = bytes;
        TimeOut.postDelayed(reWrite, timeOut);

        bleManager.write(bleDevice, CHARACTERISTIC, CHARACTERISTIC, bytes, new BleWriteCallback() {
            @Override
            public void onWriteSuccess() {
                L.e("写成功");
            }

            @Override
            public void onWriteFailure(BleException exception) {
                L.e("写失败");
            }
        });
    }


    public void readBle() {
        if (bleDevice == null || !bleManager.isConnected(bleDevice)) {
            RxToast.warning(getApplication().getString(R.string.disconnect));
            return;
        }
        bleManager.read(bleDevice, CHARACTERISTIC, CHARACTERISTIC, new BleReadCallback() {
            @Override
            public void onReadSuccess(byte[] data) {

            }

            @Override
            public void onReadFailure(BleException exception) {

            }
        });
    }


    public void openNotify() {
        if (bleDevice == null || !bleManager.isConnected(bleDevice)) {
            RxToast.warning(getApplication().getString(R.string.disconnect));
            return;
        }
        bleManager.notify(bleDevice, CHARACTERISTIC, CHARACTERISTIC, new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {
                L.e("打开通知成功");
            }

            @Override
            public void onNotifyFailure(BleException exception) {
                L.e("打开通知失败:" + exception.toString());
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                L.e("蓝牙数据更新:" + HexUtil.encodeHexStr(data));
                TimeOut.removeCallbacks(reWrite);
                currentCount = 0;
                if (bleChartChange != null)
                    bleChartChange.callBack(data);
            }
        });
    }

    public void openIndicate() {
        if (bleDevice == null || !bleManager.isConnected(bleDevice)) {
            RxToast.warning(getApplication().getString(R.string.disconnect));
            return;
        }
        bleManager.indicate(bleDevice, CHARACTERISTIC, CHARACTERISTIC, new BleIndicateCallback() {
            @Override
            public void onIndicateSuccess() {
                L.e("打开indicate成功");
            }

            @Override
            public void onIndicateFailure(BleException exception) {
                L.e("打开indicate失败:" + exception.toString());
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                L.e("蓝牙数据更新:" + HexUtil.encodeHexStr(data));
                TimeOut.removeCallbacks(reWrite);
                currentCount = 0;
                if (bleChartChange != null)
                    bleChartChange.callBack(data);
            }
        });
    }


    public void readRssi() {
        if (bleDevice == null || !bleManager.isConnected(bleDevice)) {
            RxToast.warning(getApplication().getString(R.string.disconnect));
            return;
        }
        bleManager.readRssi(bleDevice, new BleRssiCallback() {
            @Override
            public void onRssiFailure(BleException exception) {
                L.e("读取蓝牙信号失败:" + exception.toString());
            }

            @Override
            public void onRssiSuccess(int rssi) {
                L.e("读取蓝牙信号成功: 【" + rssi + "】");
            }
        });
    }


    public void ondestroy() {
        if (bleDevice == null || !bleManager.isConnected(bleDevice)) {
            RxToast.warning(getApplication().getString(R.string.disconnect));
            return;
        }
        bleManager.destroy();
    }

    public BleDevice setMAC(String MAC) throws Throwable {
        BleDevice bleDevice = null;
        if (!BluetoothAdapter.checkBluetoothAddress(MAC)) {
            return null;
        }
        BluetoothAdapter bluetoothAdapter = BleManager.getInstance().getBluetoothAdapter();
        if (bluetoothAdapter != null) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(MAC);
            if (device != null) {
                bleDevice = BleManager.getInstance().convertBleDevice(device);
            } else {
                return null;
            }
        } else {
            return null;
        }
        return bleDevice;
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
