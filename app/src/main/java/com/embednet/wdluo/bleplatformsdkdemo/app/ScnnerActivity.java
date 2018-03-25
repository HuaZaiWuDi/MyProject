package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.data.BleScanState;
import com.clj.fastble.exception.BleException;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.ble.BleTools;
import com.embednet.wdluo.bleplatformsdkdemo.scanner.DeviceListAdapter;
import com.embednet.wdluo.bleplatformsdkdemo.scanner.ExtendedBluetoothDevice;
import com.embednet.wdluo.bleplatformsdkdemo.scanner.ScannerServiceParser;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import laboratory.dxy.jack.com.jackupdate.util.AnimUtils;

import static com.embednet.wdluo.bleplatformsdkdemo.MyApplication.bleManager;

public class ScnnerActivity extends BaseAvtivity {
    ListView mListView;
    DeviceListAdapter mAdapter;
    BluetoothAdapter mBluetoothAdapter;
    AnimatedCircleLoadingView circle_loading_view;
    private TextView bleStatus;
    private BluetoothGattCharacteristic characteristic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scnner);

        setTitleText("搜索界面");
        setBack();


        circle_loading_view = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        circle_loading_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bleManager.getScanSate() == BleScanState.STATE_IDLE) {
                    scan();
                } else
                    bleManager.cancelScan();
            }
        });


        bleStatus = (TextView) findViewById(R.id.bleStatus);
        mListView = (ListView) findViewById(R.id.mListView);
        mListView.setAdapter(mAdapter = new DeviceListAdapter(this));


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final ExtendedBluetoothDevice d = (ExtendedBluetoothDevice) mAdapter.getItem(position);
                bleManager.connect(bleManager.convertBleDevice(d.device), connect);
            }
        });
        mListView.setDivider(new ColorDrawable(Color.WHITE));
        mListView.setDividerHeight(1);
        mBluetoothAdapter = bleManager.getBluetoothAdapter();
        addBondedDevices();
    }


    public void login(View v) {
        startActivity(new Intent(this, Main2Activity.class));
        finish();
    }

    private void addBondedDevices() {
        final Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            mAdapter.addBondedDevice(new ExtendedBluetoothDevice(device, device.getName(), -1000, true));
        }
    }

    /**
     * if scanned device already in the list then update it otherwise add as a new device
     */
    private void addScannedDevice(final BluetoothDevice device, final String name, final int rssi, final boolean isBonded) {
        mAdapter.addOrUpdateDevice(new ExtendedBluetoothDevice(device, name, rssi, isBonded));
    }

    /**
     * if scanned device already in the list then update it otherwise add as a new device.
     */
    private void updateScannedDevice(final BluetoothDevice device, final int rssi) {
        mAdapter.updateRssiOfBondedDevice(device.getAddress(), rssi);
    }

    private void scan() {

        if (sharedPreferences.getBoolean("AutoConnect", false)) {
            String mac = sharedPreferences.getString("MAC", "");
            if (BluetoothAdapter.checkBluetoothAddress(mac)) {
                BluetoothDevice remoteDevice = bleManager.getBluetoothAdapter().getRemoteDevice(mac);
                bleManager.connect(bleManager.convertBleDevice(remoteDevice), connect);
                return;
            }
        }

        BleTools.getInstance().configScan();

        bleManager.scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                L.d("扫描开始:" + success);
                bleStatus.setText("正在搜索设备...");
                circle_loading_view.setAnimation(AnimationUtils.loadAnimation(ScnnerActivity.this,R.anim.rotate));
            }

            @Override
            public void onScanning(BleDevice result) {
                L.d("扫描中");
                L.e("TAG" + result.getName());
                L.e("TAG" + result.getMac());
                L.e("TAG" + result.getRssi());
                L.e("TAG" + result.getKey());
                L.e("TAG" + result.getTimestampNanos());

                updateScannedDevice(result.getDevice(), result.getRssi());
                try {
                    if (ScannerServiceParser.decodeDeviceAdvData(result.getScanRecord(), null, false)) {
                        // On some devices device.getName() is always null. We have to parse the name manually :(
                        // This bug has been found on Sony Xperia Z1 (C6903) with Android 4.3.
                        // https://devzone.nordicsemi.com/index.php/cannot-see-device-name-in-sony-z1
                        addScannedDevice(result.getDevice(), ScannerServiceParser.decodeDeviceName(result.getScanRecord()), result.getRssi(), false);
                    }
                } catch (Exception e) {
                    //DebugLogger.e(TAG, "Invalid data in Advertisement packet " + e.toString());
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                L.d("扫描结束:" + scanResultList.size());
                bleStatus.setText("点击上面图标重新搜索");
                circle_loading_view.clearAnimation();
            }
        });

    }

    private void roateAnimation(){
//        AnimUtils.doRotateAnim(circle_loading_view,1000);

    }



    @Override
    protected void onStart() {
        super.onStart();
        scan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bleManager.cancelScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    BleGattCallback connect = new BleGattCallback() {
        @Override
        public void onStartConnect() {
            L.d("开始连接");
            circle_loading_view.resetLoading();
            circle_loading_view.startIndeterminate();
            circle_loading_view.setBackgroundResource(0);
            bleStatus.setText("开始连接");
            bleManager.cancelScan();
        }

        @Override
        public void onConnectFail(BleException exception) {
            bleManager.handleException(exception);
            circle_loading_view.stopFailure();
            L.e("连接失败：" + exception.toString());
            bleStatus.setText("连接失败");
            scan();
        }

        @Override
        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
            circle_loading_view.stopOk();
            L.d("连接成功");
            BleTools.bleDevice = bleDevice;
//            BluetoothGattService service = gatt.getService(CHARACTERISTIC_CHANDEG);

//            if (service != null)
//                characteristic = service.getCharacteristic(CHARACTERISTIC_CHANDEG);
//            if (characteristic != null) {
            bleStatus.setText("连接成功");
            sharedPreferences.edit().putString("MAC", bleDevice.getMac()).apply();

            bleManager.cancelScan();
//            BleTools.getInstance().openIndicate();
            BleTools.getInstance().openNotify();


//            }

        }

        @Override
        public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
            L.d(isActiveDisConnected ? "主动断开连接" : "自动断开连接");

//            if (!isActiveDisConnected) {
                bleManager.connect(device, this);
//            }
        }
    };

}
