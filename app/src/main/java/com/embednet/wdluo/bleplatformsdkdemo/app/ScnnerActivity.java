package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleConnectState;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.data.BleScanState;
import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.ble.BleTools;
import com.embednet.wdluo.bleplatformsdkdemo.scanner.DeviceListAdapter;
import com.embednet.wdluo.bleplatformsdkdemo.scanner.ExtendedBluetoothDevice;
import com.embednet.wdluo.bleplatformsdkdemo.scanner.ScannerServiceParser;
import com.embednet.wdluo.bleplatformsdkdemo.service.BleService;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;
import java.util.Set;

import laboratory.dxy.jack.com.jackupdate.ui.RxToast;
import rx.functions.Action1;

import static com.embednet.wdluo.bleplatformsdkdemo.MyApplication.bleManager;

public class ScnnerActivity extends BaseAvtivity {
    ListView mListView;
    DeviceListAdapter mAdapter;
    BluetoothAdapter mBluetoothAdapter;
    AnimatedCircleLoadingView circle_loading_view;
    private TextView bleStatus;
    private BluetoothGattCharacteristic characteristic;


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTIVE_CONNECT_STATUE)) {
                boolean connected = intent.getBooleanExtra(Constants.EXTRA_CONNECT_STATUE, false);
                if (connected) {

                    bleStatus.setText(R.string.connectSuccess);
                    sharedPreferences.edit().putString("MAC", BleTools.bleDevice.getMac()).apply();
                    circle_loading_view.stopOk();


                } else {
                    circle_loading_view.stopFailure();
                    bleStatus.setText(R.string.connectFail);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scnner);

        setTitleText(R.string.searchPage);
        setBack();


        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTIVE_CONNECT_STATUE);
        registerReceiver(receiver, filter);

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

        if (Build.VERSION.SDK_INT >= 21) {
            circle_loading_view.setBackgroundResource(R.drawable.connecting_svg);
        } else {
            circle_loading_view.setBackgroundResource(R.drawable.icon_connecting);
        }


        bleStatus = (TextView) findViewById(R.id.bleStatus);
        mListView = (ListView) findViewById(R.id.mListView);
        mListView.setAdapter(mAdapter = new DeviceListAdapter(this));


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final ExtendedBluetoothDevice d = (ExtendedBluetoothDevice) mAdapter.getItem(position);

                BleTools.bleDevice = bleManager.convertBleDevice(d.device);
                if (bleManager.getConnectState(BleTools.bleDevice) != BleConnectState.CONNECT_CONNECTING || bleManager.getConnectState(BleTools.bleDevice) != BleConnectState.CONNECT_CONNECTED) {
                    circle_loading_view.resetLoading();
                    circle_loading_view.startIndeterminate();
                    circle_loading_view.setBackgroundResource(0);
                    bleStatus.setText(R.string.connecting);
                    bleManager.cancelScan();
                }

                startService(new Intent(ScnnerActivity.this, BleService.class));
            }
        });
        mListView.setDivider(new ColorDrawable(Color.WHITE));
        mListView.setDividerHeight(1);
        mBluetoothAdapter = bleManager.getBluetoothAdapter();
        addBondedDevices();
    }


    private void sheckPromission() {
        //定位权限
        if (Build.VERSION.SDK_INT >= 23)
            new RxPermissions(this)
                    .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                L.d("权限请求成功");
                                scan();
                            } else {
                                L.d("权限请求失败");
                                RxToast.error(getString(R.string.premissBle));
                            }
                        }
                    });
        else scan();
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

        BleTools.getInstance().configScan();

        bleManager.scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                L.d("扫描开始:" + success);
                bleStatus.setText(R.string.searching);
                circle_loading_view.setAnimation(AnimationUtils.loadAnimation(ScnnerActivity.this, R.anim.rotate));
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
                bleStatus.setText(R.string.selectAndReSearch);
                circle_loading_view.clearAnimation();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        sheckPromission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bleManager.cancelScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
