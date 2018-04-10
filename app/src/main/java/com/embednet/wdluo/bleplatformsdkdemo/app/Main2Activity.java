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

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;

public class Main2Activity extends BaseAvtivity {




    /*
    * 1.圆圈半径减小
    * 2.历史数据白色
    * 3.点击面积变大
    * 4.搜索放到设备中心
    * 5.左上角显示文字"计步器"
    * 6.超出不显示，颜色改为红色。
    * 7.欧姆改为英文
    * 8.不要底部出处。
    * 9.增加一个Y 轴
    * 10.文字"首页"改为"计步器"放在左上角
    * 11.23：00改为24：00
    * 12.超过目标的柱状图都变成红色
    * 13.个人中心和历史数据兑换
    * 14.增强 picker 的滑动速度，修改下尺寸
    * 15.开关按钮调整大一点
    * 16.负载电阻改为输出功率。改为全功率、高、中、低
    * 17.控制中心改为设备中心，增加固件升级，关于设备、搜索设备。
    * 18.关于设备是设备信息。设备名、SN 序列号、固件版本号。
    * 19.去掉图片，标题放在返回后面
    * 20.副标题表按一点。
    * 21.分享图标变大，改为滑动，分享改为首页。
    *22.历史数据方向相反
    * 23.去掉上面的日期。
    * 24.上面改为不滑动。
    * 25.修改为细线。
    * 25.超过目标为红色，没有超过为绿色。
    * 26.点击不显示数据。
    * 27.周、月改为平均数。
    * 28.日周月不咬图标，改为选择吊钩
    * 29.分享加先，加上累计运动多少天。
    * 30.距离调到12f
    * 31.升级状态，放在下面
    * **/




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
