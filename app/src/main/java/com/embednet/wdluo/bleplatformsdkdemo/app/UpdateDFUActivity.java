package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.embednet.wdluo.bleplatformsdkdemo.BuildConfig;
import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.ble.BleAPI;
import com.embednet.wdluo.bleplatformsdkdemo.ble.BleTools;
import com.embednet.wdluo.bleplatformsdkdemo.dfu.DfuService;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.pitt.library.fresh.FreshDownloadView;

import java.util.List;

import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

import static com.embednet.wdluo.bleplatformsdkdemo.MyApplication.bleManager;

public class UpdateDFUActivity extends BaseAvtivity {

    FreshDownloadView mFreshDownloadView;
    TextView mStatusTextView;
    private TextView text_title;
    private String OTAFilePath = "";


    private final DfuProgressListenerAdapter listenerAdapter = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnected(String deviceAddress) {
            super.onDeviceConnected(deviceAddress);
            L.d("onDeviceConnected：" + deviceAddress);

        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            super.onDeviceDisconnected(deviceAddress);
            L.d("onDeviceDisconnected：" + deviceAddress);
//            startMyDFU();
            text_title.setText("升级过程中请勿让设备远离手机");
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            super.onDfuAborted(deviceAddress);
            L.d("onDfuAborted：" + deviceAddress);
            text_title.setText("正在升级，请稍后");
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            super.onDfuProcessStarted(deviceAddress);
            L.d("onDfuProcessStarted");
            text_title.setText("正在升级，请稍等片刻");
            mFreshDownloadView.startDownload();
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            super.onDfuProcessStarting(deviceAddress);
            L.d("onDfuProcessStarting");
            text_title.setText("文件校对成功，正在升级");
        }

        @Override
        public void onDeviceConnecting(String deviceAddress) {
            super.onDeviceConnecting(deviceAddress);
            L.d("onDeviceConnecting");

        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            super.onDeviceDisconnecting(deviceAddress);
            L.d("onDeviceDisconnecting");
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            super.onDfuCompleted(deviceAddress);
            L.d("onDfuCompleted");
            text_title.setText("升级完成");
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            super.onEnablingDfuMode(deviceAddress);
            L.d("onEnablingDfuMode");
            text_title.setText("升级过程中请勿让设备远离手机");
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            super.onError(deviceAddress, error, errorType, message);
            L.e("onError:" + message);
            startMyDFU();

        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            super.onFirmwareValidating(deviceAddress);
            L.d("onFirmwareValidating");
            text_title.setText("正在校验文件");

        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            super.onProgressChanged(deviceAddress, percent, speed, avgSpeed, currentPart, partsTotal);
            L.d("onProgressChanged:" + "percent:" + percent + "----" + speed + "----avgSpeed:" + avgSpeed + "-----currentPart:" + currentPart + "------prtsTotal:" + partsTotal);
            mFreshDownloadView.upDateProgress(percent);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dfu);

        setTitleText("空中升级");
        setBack();


        mStatusTextView = (TextView) findViewById(R.id.statusTextView);
        text_title = (TextView) findViewById(R.id.text_title);
        if (BuildConfig.DEBUG)
            text_title.setText("正在获取固件请稍后");
        else text_title.setText("点击右上角获取文件");

        text_title.postDelayed(new Runnable() {
            @Override
            public void run() {
                text_title.setText("升级过程中请勿让设备远离手机");
            }
        }, 1500);

        mFreshDownloadView = (FreshDownloadView) findViewById(R.id.mFreshDownloadView);


        mFreshDownloadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFreshDownloadView.reset();

//                BleTools.getInstance().writeBle(BleAPI.startDFU());

                // UpdateFirmwareThread mUpdateFirmwareThread = new UpdateFirmwareThread();
//                mUpdateFirmwareThread.start();
                startMyDFU();
            }
        });

        if (BuildConfig.DEBUG) {
            ImageView filePicker = findViewById(R.id.img_right);
            filePicker.setVisibility(View.VISIBLE);
            filePicker.setBackgroundResource(R.mipmap.icon_file_picker);
            filePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateFilePicker();
                }
            });
        }


    }

    private void startMyDFU() {
        if (BleTools.bleDevice == null) {
            mFreshDownloadView.showDownloadError();
            mStatusTextView.setText("未连接设备");

            return;
        }
        if (OTAFilePath == null || OTAFilePath.equals("")) {
            mFreshDownloadView.showDownloadError();
            mStatusTextView.setText("未获取升级文件");
            return;
        }

        if(!OTAFilePath.endsWith(".zip")){
            mFreshDownloadView.showDownloadError();
            mStatusTextView.setText("文件格式不对，请添加正确格式文件");
            return;
        }

        final DfuServiceInitiator starter = new DfuServiceInitiator(BleTools.bleDevice.getMac())
                .setDeviceName(BleTools.bleDevice.getName());
//                .setKeepBond(keepBond);
// If you want to have experimental buttonless DFU feature supported call additionally:
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
// but be aware of this: https://devzone.nordicsemi.com/question/100609/sdk-12-bootloader-erased-after-programming/
// and other issues related to this experimental service.

// Init packet is required by Bootloader/DFU from SDK 7.0+ if HEX or BIN file is given above.
// In case of a ZIP file, the init packet (a DAT file) must be included inside the ZIP file.

//        if (mFileType == DfuService.TYPE_AUTO)
        starter.setZip(null, OTAFilePath);
//        else {
////            starter.setBinOrHex(mFileType, mFileStreamUri, mFilePath).setInitFile(mInitFileStreamUri, mInitFilePath);
//        }
        final DfuServiceController controller = starter.start(this, DfuService.class);
// You may use the controller to pause, resume or abort the DFU process.
    }


    private void CreateFilePicker() {
        //Create a DialogProperties object.
        final DialogProperties properties = new DialogProperties();

        //Instantiate FilePickerDialog with Context and DialogProperties.
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.extensions = Constants.FILE_Extensions;
        FilePickerDialog dialog = new FilePickerDialog(this, properties);

        dialog.setTitle("选择文件");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    L.d("onSelectedFilePaths: " + files[0].getBytes().length);
                    L.d("onSelectedFilePaths: " + files[0]);
                    mStatusTextView.setText(files[0]);
                    OTAFilePath = files[0];
                }
            }
        });
        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();


        DfuServiceListenerHelper.registerProgressListener(this, listenerAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();

        DfuServiceListenerHelper.unregisterProgressListener(this, listenerAdapter);

    }

}
