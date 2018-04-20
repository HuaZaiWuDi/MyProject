package com.embednet.wdluo.JackYan.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.embednet.wdluo.JackYan.Constants;
import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.ble.BleAPI;
import com.embednet.wdluo.JackYan.ble.BleTools;
import com.embednet.wdluo.JackYan.ble.listener.BleCallBack;
import com.embednet.wdluo.JackYan.service.BleService;
import com.embednet.wdluo.JackYan.ui.RoundView;
import com.embednet.wdluo.JackYan.util.L;
import com.embednet.wdluo.JackYan.util.Utils;

import java.util.ArrayList;
import java.util.List;

import lab.dxythch.com.netlib.net.ServiceAPI;
import lab.dxythch.com.netlib.rx.RxNetSubscriber;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;

public class Main2Activity extends BaseLocationActivity {

    RoundView mRoundDisPlayView;
    ColumnChartView mColumnChartView;
    TextView battery, resistance;
    private int target = 1000;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTIVE_CONNECT_STATUE)) {
                boolean connected = intent.getBooleanExtra(Constants.EXTRA_CONNECT_STATUE, false);
                if (connected) {
                    mRoundDisPlayView.setCentreText(5000, getString(R.string.SyncSteps));
                    syncData();


                } else {
                    mRoundDisPlayView.setCentreText(5000, getString(R.string.connectFail));
                }
                mRoundDisPlayView.stopAnimation();
            }
        }
    };

    private void syncData() {
        BleAPI.getInstance().setDeviceTime(new BleCallBack() {
            @Override
            public void isSuccess(byte[] data) {
                BleAPI.getInstance().getDeviceStatus(new BleCallBack() {
                    @Override
                    public void isSuccess(byte[] data) {
                        BleAPI.getInstance().getWorkData(new BleCallBack() {
                            @Override
                            public void isSuccess(byte[] data) {
                                BleAPI.getInstance().getHistroyData(new BleCallBack() {
                                    @Override
                                    public void isSuccess(byte[] data) {
                                        mRoundDisPlayView.setCentreText(5000, getString(R.string.SyncComplete));

                                        ServiceAPI.getInstance().gainFirmwareConfig(BleTools.bleDevice.getMac(), "", new RxNetSubscriber<String>() {
                                            @Override
                                            protected void _onNext(String s) {
                                                L.d("获取设备信息：" + s);
                                            }
                                        });

                                        ServiceAPI.getInstance().refreshHistory(BleTools.bleDevice.getMac(), "", new RxNetSubscriber<String>() {
                                            @Override
                                            protected void _onNext(String s) {
                                                L.d("上传历史数据：" + s);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }


    @Override
    public void getGps(Location location) {
        Constants.Longitude = location.getLongitude();
        Constants.latitude = location.getLatitude();


        ServiceAPI.getInstance().gainAppConfig(location, Utils.getVersionName(Main2Activity.this), new RxNetSubscriber<String>() {
            @Override
            protected void _onNext(String s) {
                L.d("配置设备信息：" + s);
            }
        });

        ServiceAPI.getInstance().gainUserInfo(new RxNetSubscriber<String>() {
            @Override
            protected void _onNext(String s) {
                L.d("获取用户信息:" + s);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTIVE_CONNECT_STATUE);
        registerReceiver(receiver, filter);

        setTitleText(R.string.FirstPage);

        ImageView back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.GONE);
//        back.setImageResource(R.mipmap.icon_scan);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mainIntent = new Intent(Main2Activity.this,
//                        ScnnerActivity.class);
//                startActivity(mainIntent);
//            }
//        });

        mRoundDisPlayView = findViewById(R.id.mRoundDisPlayView);
        mRoundDisPlayView.setCentreText(5000, getString(R.string.stepsTarget, 5000));

        mRoundDisPlayView.setUnit(getString(R.string.step));
        mRoundDisPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpdateValue();
            }
        });

        battery = findViewById(R.id.battery);
        resistance = findViewById(R.id.resistance);

        setmColumnChartView();
        setUpdateValue();

        String mac = sharedPreferences.getString("MAC", "");
        try {
            BleTools.bleDevice = BleTools.getInstance().setMAC(mac);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        startService(new Intent(Main2Activity.this, BleService.class));
        mRoundDisPlayView.setCentreText(0 / 60, getString(R.string.connecting));

    }

    ColumnChartData data;

    private void setmColumnChartView() {
        mColumnChartView = findViewById(R.id.mColumnChartView);
        List<Column> columns = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        List<AxisValue> axisYValues = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            List<SubcolumnValue> values = new ArrayList<>();
            values.add(new SubcolumnValue(0, Color.parseColor("#70BF52")).setLabel(0 + ""));

            Column column = new Column(values);
//            column.setHasLabels(true);//标签
//            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);

            if (i == 0) {
                axisValues.add(new AxisValue(1).setLabel(setFormat(i, "00") + ":00"));
            } else if (i % 6 == 0)
                axisValues.add(new AxisValue(i).setLabel(setFormat(i, "00") + ":00"));
        }
        axisValues.add(new AxisValue(22).setLabel(setFormat(24, "00") + ":00"));
        axisYValues.add(new AxisValue(target / 2).setLabel((int) (target / 2) + ""));
        axisYValues.add(new AxisValue(target).setLabel((int) (target) + ""));

        data = new ColumnChartData(columns);
        mColumnChartView.setValueSelectionEnabled(true);//选中突出
        data.setAxisXBottom(new Axis(axisValues).setTextColor(Color.GRAY));
        data.setAxisYRight(new Axis(axisYValues).setHasLines(true).setInside(true).setTextColor(Color.GRAY));


        mColumnChartView.setZoomEnabled(false);
        mColumnChartView.setColumnChartData(data);

        Viewport v = new Viewport(0, 5000, (float) columns.size(), 0);

        mColumnChartView.setMaximumViewport(v);
        mColumnChartView.setCurrentViewport(v);
        mColumnChartView.setCurrentViewportWithAnimation(v);

        mColumnChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {

            }

            @Override
            public void onValueDeselected() {

            }
        });
    }


    int sum = 0;

    private void setUpdateValue() {
        sum = 0;
        mColumnChartView.cancelDataAnimation();
        for (Column column : data.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                int v = (int) (Math.random() * 1300);
                sum += v;
                value.setTarget(v).setLabel(v + "");
                if (sum > 5000) {
                    value.setColor(Color.RED);
                }
            }
        }
        mColumnChartView.startDataAnimation();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    //分享
    public void share(View v) {
        startActivity(new Intent(this, ShareActivity.class));
//        Utils.sendEmail(this);
    }

    //个人中心
    public void personal(View v) {
        startActivity(new Intent(this, PersonalActivity.class));
//        Utils.navigateWithRippleCompat(this, new Intent(this, PersonalActivity.class), v, R.color.white);
    }

    //统计
    public void statistics(View v) {
        startActivity(new Intent(this, HistoryDataActivity.class));
//        Utils.navigateWithRippleCompat(this, new Intent(this, MainActivity.class), v, R.color.orange);
    }

}
