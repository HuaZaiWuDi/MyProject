package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.ble.BleAPI;
import com.embednet.wdluo.bleplatformsdkdemo.ble.BleTools;
import com.embednet.wdluo.bleplatformsdkdemo.ble.listener.BleCallBack;
import com.embednet.wdluo.bleplatformsdkdemo.service.BleService;
import com.embednet.wdluo.bleplatformsdkdemo.ui.RoundView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class Main2Activity extends BaseAvtivity {

    RoundView mRoundDisPlayView;
    ColumnChartView mColumnChartView;
    TextView battery, resistance;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTIVE_CONNECT_STATUE)) {
                boolean connected = intent.getBooleanExtra(Constants.EXTRA_CONNECT_STATUE, false);
                if (connected) {
                    mRoundDisPlayView.setCentreText(0 / 60 + "", getString(R.string.SyncSteps));
                    syncData();
                } else {
                    mRoundDisPlayView.setCentreText(0 / 60 + "", getString(R.string.connectFail));
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
                                        mRoundDisPlayView.setCentreText(0 / 60 + "", getString(R.string.SyncComplete));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTIVE_CONNECT_STATUE);
        registerReceiver(receiver, filter);


        setTitleText(R.string.FirstPage);
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setImageResource(R.mipmap.icon_scan);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(Main2Activity.this,
                        ScnnerActivity.class);
                startActivity(mainIntent);
            }
        });

        mRoundDisPlayView = findViewById(R.id.mRoundDisPlayView);
        mRoundDisPlayView.setCentreText(0 / 60 + "", getString(R.string.stepsTarget, 5000));

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


        String mac = sharedPreferences.getString("MAC", "");
        try {
            BleTools.bleDevice = BleTools.getInstance().setMAC(mac);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        startService(new Intent(Main2Activity.this, BleService.class));
        mRoundDisPlayView.setCentreText(0 / 60 + "", getString(R.string.connecting));

    }

    ColumnChartData data;

    private void setmColumnChartView() {
        mColumnChartView = (ColumnChartView) findViewById(R.id.mColumnChartView);
        List<Column> columns = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        List<AxisValue> axisYValues = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            Random random = new Random();
            int steps = random.nextInt(100);
            List<SubcolumnValue> values = new ArrayList<>();
            values.add(new SubcolumnValue(steps, Color.parseColor("#70BF52")).setLabel(steps + ""));

            Column column = new Column(values);
            column.setHasLabels(true);//标签
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);

            if (i % 6 == 0)
                axisValues.add(new AxisValue(i).setLabel(setFormat(i, "00") + ":00"));
        }
        axisValues.add(new AxisValue(23).setLabel(setFormat(23, "00") + ":00"));
        axisYValues.add(new AxisValue(50).setLabel(50 + ""));

        data = new ColumnChartData(columns);
        // Set stacked flag.叠加
        data.setStacked(false);
        mColumnChartView.setValueSelectionEnabled(true);//选中突出
        data.setAxisXBottom(new Axis(axisValues).setTextColor(Color.GRAY));
        data.setAxisYRight(new Axis(axisYValues).setHasLines(true).setInside(true).setTextColor(Color.parseColor("#ffffff")));

        mColumnChartView.setZoomEnabled(false);
        mColumnChartView.setColumnChartData(data);

        mColumnChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {

            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    private void setUpdateValue() {
        mColumnChartView.cancelDataAnimation();
        for (Column column : data.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                int v = (int) (Math.random() * 100);
                value.setTarget(v).setLabel(v + "");
            }
        }
        mColumnChartView.startDataAnimation();
    }


    public static String setFormat(long value, String format) {

        return new DecimalFormat(format).format(value);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    //分享
    public void share(View v) {

        startActivity(new Intent(this, ShareActivity.class));
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
