package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.module.DayStepsTab;
import com.embednet.wdluo.bleplatformsdkdemo.module.StepsData;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;
import com.embednet.wdluo.bleplatformsdkdemo.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class HistoryDataActivity extends BaseAvtivity {
    TextView dateTitle;

    StepsData stepsData;


    Calendar calendarDay = Calendar.getInstance();


    Map<String, DayStepsTab> map;

    ColumnChartView mColumnChartView;


    String[] date = new String[90];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_data);

        dateTitle = findViewById(R.id.dateTitle);
        dateTitle.setText(Utils.formatData(calendarDay.getTime(), Constants.DATE_FORMAT));
        setTitleText("历史数据");
        setBack();

//        stepsData = (StepsData) MyApplication.getACache().getAsObject(Constants.SIGN_STEPS);
//        if (stepsData.stepData != null)
//            map = stepsData.stepData;
//        else
//            map = new HashMap<>();


//        DayStepsTab tab = new DayStepsTab();
//        tab.date
//        map.put("2018-03-19", );


        setRecyclerView();
        setmColumnChartView();
        generateInitialLineData();
        setUpdateValue();
        generateLineData(Color.parseColor("#F0CF17"));

        mColumnChartView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mColumnChartView.setZoomLevelWithAnimation(0, 0, 12f);
            }
        }, 1000);
    }


    ColumnChartData data;

    private void setmColumnChartView() {

        mColumnChartView = (ColumnChartView) findViewById(R.id.mColumnChartView);
        List<Column> columns = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        List<AxisValue> axisYValues = new ArrayList<>();
        for (int i = 0; i < 90; i++) {
            Random random = new Random();
            int steps = random.nextInt(100);
            List<SubcolumnValue> values = new ArrayList<>();
            values.add(new SubcolumnValue(steps, ChartUtils.pickColor()).setLabel(steps + ""));

            Column column = new Column(values);
            column.setHasLabels(true);//标签
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);

            if (i != 0)
                calendarDay.add(Calendar.DATE, -1);
            date[i] = Utils.formatData(calendarDay.getTime(), Constants.DATE_FORMAT);
            axisValues.add(new AxisValue(i).setLabel(Utils.formatData(calendarDay.getTime(), "MM/dd")));
        }
        axisYValues.add(new AxisValue(50).setLabel(50 + ""));

        data = new ColumnChartData(columns);
        // Set stacked flag.叠加
        data.setStacked(false);
        mColumnChartView.setValueSelectionEnabled(true);//选中突出
        data.setAxisXBottom(new Axis(axisValues).setTextColor(Color.GRAY));
        data.setAxisYRight(new Axis(axisYValues).setHasLines(true).setHasSeparationLine(true).setInside(true).setTextColor(Color.parseColor("#ffffff")));

        mColumnChartView.setZoomEnabled(true);
        mColumnChartView.setZoomType(ZoomType.HORIZONTAL);
//        mColumnChartView.setZoomLevelWithAnimation(0, 0, 12f);
        mColumnChartView.setZoomLevel(0, 0, 6f);
        mColumnChartView.setMaxZoom(6f);
        mColumnChartView.setColumnChartData(data);

        mColumnChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {

//                generateLineData(subcolumnValue.getColor(), map.get(date[i]) != null ? map.get(date[i]).dayData : null);
                generateLineData(subcolumnValue.getColor());
                L.d("点击的条目:" + i);
                L.d("点击的条目:" + mColumnChartView.getZoomLevel());
                dateTitle.setText(date[i]);
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    private void setUpdateValue(Map<String, DayStepsTab> map) {
        mColumnChartView.cancelDataAnimation();

        for (Column column : data.getColumns()) {
            List<SubcolumnValue> values = column.getValues();
            for (int i = 0; i < values.size(); i++) {
                if (map.get(date[i]) != null)
                    values.get(i).setTarget(map.get(date[i]).Total).setLabel(map.get(date[i]).Total + "");
                else values.get(i).setTarget(0);
            }
        }
        mColumnChartView.startDataAnimation();
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

    LineChartData lineData;
    LineChartView chartView;

    /**
     * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
     * will select value on column chart.
     */
    private void generateInitialLineData() {
        chartView = findViewById(R.id.mLineChartView);

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < 24; ++i) {
            values.add(new PointValue(i, 0));

            if (i % 6 == 0)
                axisValues.add(new AxisValue(i).setLabel(setFormat(i, "00")));
        }
        axisValues.add(new AxisValue(23).setLabel(setFormat(23, "00")));

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);
        line.setCubic(false);
        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues));
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(5));
        chartView.setLineChartData(lineData);

        chartView.setValueSelectionEnabled(true);
        chartView.setSelected(true);
        // For build-up animation you have to disable viewport recalculation.
        chartView.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, 100, 24, 0);
        chartView.setMaximumViewport(v);
        chartView.setCurrentViewport(v);

        chartView.setZoomEnabled(false);

    }

    public void generateLineData(int color, int[] dayValue) {
        chartView.cancelDataAnimation();

        Line line = lineData.getLines().get(0);// For this example there is always only one line.
        line.setColor(color);
        if (dayValue == null) {
            return;
        }
        List<PointValue> values = line.getValues();

        for (int i = 0; i < values.size(); i++) {
            PointValue value = values.get(i);
            value.setTarget(value.getX(), dayValue[i]);
            value.setLabel(dayValue[i] + "");
        }

        chartView.startDataAnimation(300);
    }

    public void generateLineData(int color) {
        chartView.cancelDataAnimation();

        Line line = lineData.getLines().get(0);// For this example there is always only one line.
        line.setColor(color);

        List<PointValue> values = line.getValues();

        for (int i = 0; i < values.size(); i++) {
            PointValue value = values.get(i);
            int data = (int) (Math.random() * 100);
            value.setTarget(value.getX(), (float) (Math.random() * 100)).setLabel(data + "");
        }

        chartView.startDataAnimation(300);
    }


    private void setRecyclerView() {

    }

    public static String setFormat(long value, String format) {

        return new DecimalFormat(format).format(value);
    }

}
