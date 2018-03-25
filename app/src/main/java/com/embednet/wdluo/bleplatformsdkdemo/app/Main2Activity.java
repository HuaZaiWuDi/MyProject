package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.ui.RoundDisPlayView;
import com.embednet.wdluo.bleplatformsdkdemo.util.ScreenUtil;
import com.embednet.wdluo.bleplatformsdkdemo.util.ShareUtlis;
import com.embednet.wdluo.bleplatformsdkdemo.util.Utils;

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

    RoundDisPlayView mRoundDisPlayView;
    ColumnChartView mColumnChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        setTitleText("功能界面");
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

        mRoundDisPlayView = (RoundDisPlayView) findViewById(R.id.mRoundDisPlayView);
        mRoundDisPlayView.startAnimation();
        mRoundDisPlayView.setCentreText(0 / 60 + "", "", "今日数据")
                .setBackground(Color.parseColor("#333333")).submit();

        mRoundDisPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpdateValue();
            }
        });


        setmColumnChartView();

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
            values.add(new SubcolumnValue(steps, Color.parseColor("#F0CF17")).setLabel(steps + ""));

            Column column = new Column(values);
            column.setHasLabels(true);//标签
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);

            if (i % 6 == 0)
                axisValues.add(new AxisValue(i).setLabel(setFormat(i, "00")));
        }
        axisValues.add(new AxisValue(23).setLabel(setFormat(23, "00")));
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


    //分享
    public void share(View v) {
        Bitmap bitmap = ScreenUtil.snapShotWithoutStatusBar(this);
        ShareUtlis.smpleShareImage(this, bitmap);
    }

    //个人中心
    public void personal(View v) {
        startActivity(new Intent(this, PersonalActivity.class));
//        Utils.navigateWithRippleCompat(this, new Intent(this, PersonalActivity.class), v, R.color.white);
    }

    //统计
    public void statistics(View v) {
        startActivity(new Intent(this, MainActivity.class));
//        Utils.navigateWithRippleCompat(this, new Intent(this, MainActivity.class), v, R.color.orange);
    }

}
