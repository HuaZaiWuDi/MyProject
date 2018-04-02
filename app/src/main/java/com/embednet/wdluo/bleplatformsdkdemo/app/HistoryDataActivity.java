package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.module.DayStepsTab;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;
import com.embednet.wdluo.bleplatformsdkdemo.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class HistoryDataActivity extends BaseAvtivity {
    TextView dateTitle;


    List<DayStepsTab> lists = new ArrayList<>();

    ColumnChartView mColumnChartView;

    RecyclerView mRecyclerView;

    Calendar calendarDay = Calendar.getInstance();


    String[] dates = new String[90];

    View heardView;




    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_data);

        setTitleText(R.string.historyData);
        setBack();

//        stepsData = (StepsData) MyApplication.getACache().getAsObject(Constants.SIGN_STEPS);
//        if (stepsData.stepData != null)
//            map = stepsData.stepData;
//        else
//            map = new HashMap<>();


//        DayStepsTab tab = new DayStepsTab();
//        tab.date
//        map.put("2018-03-19", );

        lists.clear();


        for (int i = 0; i < 90; i++) {
            if (i != 0)
                calendarDay.add(Calendar.DATE, -1);
            String date = Utils.formatData(calendarDay.getTime(), Constants.DATE_FORMAT);

            Random random = new Random();
            int steps = random.nextInt(30000);

            DayStepsTab tab = new DayStepsTab();
            tab.Total = steps;
            tab.date = date;

            dates[i] = Utils.formatData(calendarDay.getTime(), "MM/dd");
            lists.add(tab);
        }
        L.d("list：" + lists);


        heardView = LayoutInflater.from(this).inflate(R.layout.item_heard_recycler, null);
        dateTitle = heardView.findViewById(R.id.dateTitle);
        dateTitle.setText(Utils.formatData(calendarDay.getTime(), Constants.DATE_FORMAT));

        setRecyclerView();
        setmColumnChartView();
        setUpdateValue(lists);

        mColumnChartView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mColumnChartView.setVisibility(View.VISIBLE);
                mColumnChartView.setZoomLevelWithAnimation(0, 0, 12f);

                adapter.notifyDataSetChanged();
            }
        }, 1000);


        mColumnChartView.setOnTouchListener(new View.OnTouchListener() {
                                                @Override
                                                public boolean onTouch(View view, MotionEvent event) {
                                                    if (event.getAction() == MotionEvent.ACTION_UP) {
                                                        //允许ScrollView截断点击事件，ScrollView可滑动
                                                        mRecyclerView.requestDisallowInterceptTouchEvent(false);
                                                    } else {
                                                        //不允许ScrollView截断点击事件，点击事件由子View处理
                                                        mRecyclerView.requestDisallowInterceptTouchEvent(true);
                                                    }
                                                    return false;
                                                }
                                            }
        );

    }


    ColumnChartData data;

    private void setmColumnChartView() {

        mColumnChartView = heardView.findViewById(R.id.mColumnChartView);
        List<Column> columns = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        List<AxisValue> axisYValues = new ArrayList<>();
        for (int i = 0; i < 90; i++) {
            List<SubcolumnValue> values = new ArrayList<>();
            values.add(new SubcolumnValue(0, ChartUtils.pickColor()).setLabel(0 + ""));

            Column column = new Column(values);
            column.setHasLabels(true);//标签
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);

            axisValues.add(new AxisValue(i).setLabel(dates[i]));
        }
        axisYValues.add(new AxisValue(5000).setLabel(5000 + ""));

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

////                generateLineData(subcolumnValue.getColor(), map.get(date[i]) != null ? map.get(date[i]).dayData : null);
//                generateLineData(subcolumnValue.getColor());
                L.d("点击的条目:" + i);
                L.d("点击的条目:" + mColumnChartView.getZoomLevel());
                dateTitle.setText(dates[i]);
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    private void setUpdateValue(List<DayStepsTab> lists) {
        L.d("数据之一：" + lists.size());
        mColumnChartView.cancelDataAnimation();


        List<Column> columns = data.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            for (SubcolumnValue value : columns.get(i).getValues()) {
                if (lists.get(i) != null) {
                    value.setTarget(lists.get(i).Total).setLabel(lists.get(i).Total + "");
                } else value.setTarget(0);
            }
        }

        mColumnChartView.startDataAnimation();


    }

    private void setUpdateValue() {
        mColumnChartView.cancelDataAnimation();
        for (Column column : data.getColumns()) {
            L.d("数据之一：" + data.getColumns().size());
            for (SubcolumnValue value : column.getValues()) {
                L.d("数据之一：" + column.getValues().size());
                int v = (int) (Math.random() * 100);
                value.setTarget(v).setLabel(v + "");
            }
        }
        mColumnChartView.startDataAnimation();
    }


    BaseQuickAdapter adapter;


    private void setRecyclerView() {
        mRecyclerView = findViewById(R.id.mRecyclerView);
        final TextView subtitle = findViewById(R.id.subtitle);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        adapter = new BaseQuickAdapter<DayStepsTab, BaseViewHolder>(R.layout.item_recycyler, lists) {
            @Override
            protected void convert(BaseViewHolder holder, DayStepsTab dayStepsTab) {
                holder.setText(R.id.title, dayStepsTab.Total + getString(R.string.step));
                holder.setText(R.id.text, dayStepsTab.date);
                if (dayStepsTab.Total > 5000) {
                    holder.setTextColor(R.id.title, Color.YELLOW);
                    holder.setTextColor(R.id.text, Color.YELLOW);
                } else {
                    holder.setTextColor(R.id.title, Color.WHITE);
                    holder.setTextColor(R.id.text, Color.WHITE);
                }
            }
        };

        adapter.addHeaderView(heardView);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                View view = layoutManager.findViewByPosition(0);
                if (view != null) {
                    if (subtitle.getVisibility() == View.VISIBLE)
                        subtitle.setVisibility(View.GONE);
                } else {
                    if (subtitle.getVisibility() == View.GONE)
                        subtitle.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    public static String setFormat(long value, String format) {

        return new DecimalFormat(format).format(value);
    }


    public List<DayStepsTab> Map2List(Map<String, DayStepsTab> map) {
        Collection<DayStepsTab> valueCollection = map.values();
        final int size = valueCollection.size();

        List<DayStepsTab> valueList = new ArrayList(valueCollection);
        return valueList;
    }

}
