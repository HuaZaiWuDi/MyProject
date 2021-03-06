package com.embednet.wdluo.JackYan.app;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.module.DayStepsTab;
import com.embednet.wdluo.JackYan.module.IconTab;
import com.embednet.wdluo.JackYan.util.L;
import com.embednet.wdluo.JackYan.util.Utils;
import com.jiang.android.indicatordialog.IndicatorBuilder;
import com.jiang.android.indicatordialog.IndicatorDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import laboratory.dxy.jack.com.jackupdate.util.TimeUtil;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class HistoryDataActivity extends BaseActivity {
    TextView dateTitle;


    List<DayStepsTab> lists = new ArrayList<>();
    List<IconTab> icons = new ArrayList<>();

    ColumnChartView mColumnChartView;

    RecyclerView mRecyclerView;

    Calendar calendarDay = Calendar.getInstance();
    Calendar calendarWeek = Calendar.getInstance();
    Calendar calendarMonth = Calendar.getInstance();


    String[] dates = new String[90];
    String[] weeks = new String[90];
    String[] months = new String[90];

    View heardView;
    int ViewMode = 0;

    TextView text_mode, CountSteps, CountKm, targetSteps;
    ImageView img_mode;

    int stepsTarget = 5000;


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


        text_mode = findViewById(R.id.text_mode);
        img_mode = findViewById(R.id.img_mode);
        CountSteps = findViewById(R.id.CountSteps);
        CountKm = findViewById(R.id.CountKm);
        targetSteps = findViewById(R.id.targetSteps);


        lists.clear();
        icons.clear();
        icons.add(new IconTab(getString(R.string.View_Day), true));
        icons.add(new IconTab(getString(R.string.View_Week), false));
        icons.add(new IconTab(getString(R.string.View_Month), false));


        for (int i = 0; i < 90; i++) {
            if (i != 0) {
                calendarDay.add(Calendar.DATE, -1);
                calendarWeek.add(Calendar.DATE, -7);
                calendarMonth.add(Calendar.MONTH, -1);
            }

            dates[i] = Utils.formatData(calendarDay.getTime(), "MM/dd");

            String firstDay = Utils.formatData(TimeUtil.getFirstDayOfWeek(calendarWeek.getTime()), "MM/dd");
//            String lastDay = Utils.formatData(TimeUtil.getLastDayOfWeek(calendarWeek.getTime()), "MM/dd");

//            weeks[i] = firstDay + "-" + lastDay;
            weeks[i] = firstDay;

            String month = Utils.formatData(calendarMonth.getTime(), "yy/MM");
            months[i] = month;


            Random random = new Random();
            int steps = random.nextInt(6000);

            DayStepsTab tab = new DayStepsTab();
            tab.date = Utils.formatData(calendarDay.getTime(), getString(R.string.formatDate));
            tab.Total = steps;
            lists.add(tab);
        }

        dates[0] = getString(R.string.today);
        dates[1] = getString(R.string.Yesterday);
        weeks[0] = getString(R.string.thisWeek);
        weeks[1] = getString(R.string.LastWeek);
        months[0] = getString(R.string.thisMonth);
        months[1] = getString(R.string.LastMonth);


//        heardView = LayoutInflater.from(this).inflate(R.layout.item_heard_recycler, null);
        dateTitle = findViewById(R.id.dayTitle);

        setRecyclerView();
        setmColumnChartView();

        changeChart();


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

    IndicatorDialog dialog;

    //更换图标视图
    public void bottom(View v) {

        final BaseQuickAdapter popAdapter = new BaseQuickAdapter<IconTab, BaseViewHolder>(R.layout.item, icons) {
            @Override
            protected void convert(BaseViewHolder holder, IconTab iconTab) {
                TextView tv = holder.getView(R.id.item_add);
                tv.setText(iconTab.title);

                if (iconTab.ischecked)
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_ok, 0, 0, 0);
                else
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            }
        };

        popAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                icons.get(0).ischecked = false;
                icons.get(1).ischecked = false;
                icons.get(2).ischecked = false;

                icons.get(position).ischecked = true;
                popAdapter.notifyDataSetChanged();

                text_mode.setText(icons.get(position).title);
//                dialog.dismiss();
                ViewMode = position;
                changeChart();
            }
        });


        dialog = new IndicatorBuilder(this)
                .width(400)
                .height(400)
                .ArrowDirection(IndicatorBuilder.TOP)
                .bgColor(Color.WHITE)
                .gravity(IndicatorBuilder.GRAVITY_RIGHT)
                .radius(18)
                .ArrowRectage(0.9f)
                .layoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
                .adapter(popAdapter)
                .create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show(v);

    }

    private void changeChart() {
//        setUpdateValue(dates, lists, ViewMode == 0 ? stepsTarget : ViewMode == 1 ? stepsTarget * 7 : stepsTarget * 30);
        setUpdateValue(dates, lists, stepsTarget);
        syncData(0);

        mColumnChartView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mColumnChartView.setZoomLevelWithAnimation(1, 0, 6f);

                adapter.notifyDataSetChanged();
            }
        }, 1000);

    }


    ColumnChartData data;

    private void setmColumnChartView() {

        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < 90; i++) {
            List<SubcolumnValue> values = new ArrayList<>();
            values.add(new SubcolumnValue(0, ChartUtils.pickColor()).setLabel(0 + ""));

            Column column = new Column(values);
//            column.setHasLabels(true);//标签
//            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
        }

        data = new ColumnChartData(columns);
        // Set stacked flag.叠加
        data.setStacked(false);
        mColumnChartView.setValueSelectionEnabled(true);//选中突出


        mColumnChartView.setZoomEnabled(false);
        mColumnChartView.setZoomType(ZoomType.HORIZONTAL);
        mColumnChartView.setColumnChartData(data);

        mColumnChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {

                syncData(i);
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }


    public void syncData(int position) {
        switch (ViewMode) {
            case 0:
                dateTitle.setText(dates[position]);
                CountSteps.setText(lists.get(position).Total + "");
                CountKm.setText(lists.get(position).Total / 120 + "");
                targetSteps.setText(stepsTarget + "");
                break;
            case 1:
                dateTitle.setText(weeks[position]);
                CountSteps.setText(lists.get(position).Total * 7 + "");
                CountKm.setText(lists.get(position).Total / 120 * 7 + "");
                targetSteps.setText(stepsTarget * 7 + "");
                break;
            case 2:
                dateTitle.setText(months[position]);
                CountSteps.setText(lists.get(position).Total * 30 + "");
                CountKm.setText(lists.get(position).Total / 120 * 30 + "");
                targetSteps.setText(stepsTarget * 30 + "");
                break;
        }

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

    private void setUpdateValue(String[] axis, List<DayStepsTab> lists, int stepsTarget) {
        mColumnChartView.cancelDataAnimation();

        List<AxisValue> axisValues = new ArrayList<>();
        List<AxisValue> axisYValues = new ArrayList<>();
        for (int i = 0; i < 90; i++) {
            axisValues.add(new AxisValue(i).setLabel(axis[i]));
        }

        axisYValues.add(new AxisValue(stepsTarget).setLabel("" + stepsTarget));
        axisYValues.add(new AxisValue(stepsTarget / 2).setLabel("" + stepsTarget / 2));
        data.setAxisXBottom(new Axis(axisValues).setTextColor(Color.GRAY));
        data.setAxisYRight(new Axis(axisYValues).setHasLines(true).setHasSeparationLine(true).setInside(true).setTextColor(Color.GRAY));
        List<Column> columns = data.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            for (SubcolumnValue value : columns.get(i).getValues()) {
                if (lists.get(i) != null) {
                    value.setTarget(lists.get(i).Total > stepsTarget * 1.3 ? (int) (stepsTarget * 1.3) : lists.get(i).Total).setLabel(lists.get(i).Total + "").setColor(Color.parseColor(lists.get(i).Total >= stepsTarget ? "#F81C09" : "#70BF52"));
                } else value.setTarget(0);
            }
        }

        Viewport v = new Viewport(0, (float) (stepsTarget * 1.3), (float) columns.size(), 0);

        mColumnChartView.setMaximumViewport(v);
        mColumnChartView.setCurrentViewport(v);
        mColumnChartView.setCurrentViewportWithAnimation(v);

        mColumnChartView.startDataAnimation();
    }


    BaseQuickAdapter adapter;


    private void setRecyclerView() {
        mRecyclerView = findViewById(R.id.mRecyclerView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        adapter = new BaseQuickAdapter<DayStepsTab, BaseViewHolder>(R.layout.item_recycyler, lists) {
            @Override
            protected void convert(BaseViewHolder holder, DayStepsTab dayStepsTab) {
                holder.setText(R.id.title, dayStepsTab.Total + getString(R.string.step));
                holder.setText(R.id.text, dayStepsTab.date);
//                if (dayStepsTab.Total > 5000) {
//                    holder.setTextColor(R.id.title, Color.RED);
//                    holder.setTextColor(R.id.text, Color.RED);
//                } else {
                holder.setTextColor(R.id.title, Color.WHITE);
                holder.setTextColor(R.id.text, Color.WHITE);
//                }
            }
        };

//        adapter.addHeaderView(heardView);

        mRecyclerView.setAdapter(adapter);

//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                View view = layoutManager.findViewByPosition(0);
//                if (view != null) {
//                    if (subtitle.getVisibility() == View.VISIBLE)
//                        subtitle.setVisibility(View.GONE);
//                } else {
//                    if (subtitle.getVisibility() == View.GONE)
//                        subtitle.setVisibility(View.VISIBLE);
//                }
//            }
//        });
        mColumnChartView = findViewById(R.id.mColumnChartView);

    }

}
