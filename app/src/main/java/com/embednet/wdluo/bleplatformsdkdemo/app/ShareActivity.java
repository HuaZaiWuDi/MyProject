package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.MyApplication;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.module.UserInfo;
import com.embednet.wdluo.bleplatformsdkdemo.ui.CircleImageView;
import com.embednet.wdluo.bleplatformsdkdemo.ui.RoundView;
import com.embednet.wdluo.bleplatformsdkdemo.util.ScreenUtil;
import com.embednet.wdluo.bleplatformsdkdemo.util.ShareUtlis;

import java.util.ArrayList;
import java.util.List;

import laboratory.dxy.jack.com.jackupdate.ui.RxToast;
import laboratory.dxy.jack.com.jackupdate.util.ApplicationInfoUtil;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class ShareActivity extends BaseAvtivity {

    RoundView mRoundDisPlayView;
    UserInfo info;
    int target = 1000;
    ColumnChartView mColumnChartView;
    LinearLayout share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        setTitleText(R.string.sharePage);
        setBack();
        ImageView img_right = findViewById(R.id.img_right);
        img_right.setImageResource(R.mipmap.share);

        img_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(null);
            }
        });

        mRoundDisPlayView = findViewById(R.id.mRoundDisPlayView);
        mRoundDisPlayView.setCentreText(5000, getString(R.string.stepsTarget, 5000))
                .setUnit(getString(R.string.step));

        info = (UserInfo) MyApplication.aCache.getAsObject("UserInfo");

        CircleImageView userImg = findViewById(R.id.userImg);
        if (info != null) {
            if (info.heardImgUrl != null)
                Glide.with(this)
                        .asDrawable()
                        .load(info.heardImgUrl)
                        .into(userImg);
            TextView title = findViewById(R.id.UserName);
            if (info.name != null)
                title.setText(info.name);
            TextView text = findViewById(R.id.UserPhone);
            text.setText(getString(R.string.sumDaySteps, 75));
        } else {
            info = new UserInfo();
            info.stepsTarget = 5000;
            MyApplication.aCache.put("UserInfo", info);
        }
        setmColumnChartView();
        share = findViewById(R.id.share);
    }

    private void setmColumnChartView() {
        mColumnChartView = findViewById(R.id.mColumnChartView);
        List<Column> columns = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        List<AxisValue> axisYValues = new ArrayList<>();
        int sum = 0;
        for (int i = 0; i < 24; i++) {
            List<SubcolumnValue> values = new ArrayList<>();
            int v = (int) (Math.random() * 1300);
            sum += v;
            values.add(new SubcolumnValue(v, sum > 5000 ? Color.RED : Color.parseColor("#70BF52")).setLabel(v + ""));

            Column column = new Column(values);
            columns.add(column);

            if (i == 0) {
                axisValues.add(new AxisValue(1).setLabel(setFormat(i, "00") + ":00"));
            } else if (i % 6 == 0)
                axisValues.add(new AxisValue(i).setLabel(setFormat(i, "00") + ":00"));
        }
        axisValues.add(new AxisValue(22).setLabel(setFormat(24, "00") + ":00"));
        axisYValues.add(new AxisValue(target / 2).setLabel((int) (target / 2) + ""));
        axisYValues.add(new AxisValue(target).setLabel((int) (target) + ""));

        ColumnChartData data = new ColumnChartData(columns);
        mColumnChartView.setValueSelectionEnabled(true);//选中突出
        data.setAxisXBottom(new Axis(axisValues).setTextColor(Color.GRAY));
        data.setAxisYRight(new Axis(axisYValues).setHasLines(true).setInside(true).setTextColor(Color.GRAY));


        mColumnChartView.setZoomEnabled(false);
        mColumnChartView.setColumnChartData(data);
//
//        Viewport v = new Viewport(0, 5000, (float) columns.size(), 0);
//
//        mColumnChartView.setMaximumViewport(v);
//        mColumnChartView.setCurrentViewport(v);
//        mColumnChartView.setCurrentViewportWithAnimation(v);

        mColumnChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {

            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    Bitmap bitmap;

    public void share(String packages) {
        bitmap = ScreenUtil.snapShotWithoutStatusBar(this);
        ShareUtlis.smpleShareImage(this, bitmap, packages);
    }


    @Override
    protected void onStart() {
        super.onStart();
//        share.setVisibility(View.VISIBLE);
//        mColumnChartView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public void userInfo(View v) {
//        if (checkApp(Constants.QQ)) {
//            share(Constants.QQ);
//        }
    }

    public void img_qq(View v) {
        if (checkApp(Constants.QQ)) {
            share(Constants.QQ);
        }
    }

    public void img_QQ_zone(View v) {
        if (checkApp(Constants.QZONE)) {
            share(Constants.QZONE);
        }
    }

    public void img_WX(View v) {
        if (checkApp(Constants.WECHAR)) {
            share(Constants.WECHAR);
        }
    }

    public void img_friends(View v) {
        if (checkApp(Constants.WECHAR)) {
            share(Constants.WECHAR);
        }
    }

    public void img_weibo(View v) {
        if (checkApp(Constants.WEIBO)) {
            share(Constants.WEIBO);
        }
    }

    public void img_facebook(View v) {
        if (checkApp(Constants.FACEBOOK)) {
            share(Constants.FACEBOOK);
        }
    }

    public void img_twitter(View v) {
        if (checkApp(Constants.TWITTER)) {
            share(Constants.TWITTER);
        }
    }

    public void img_whatsapp(View v) {
        if (checkApp(Constants.WHATSAPP)) {
            share(Constants.WHATSAPP);
        }
    }


    private boolean checkApp(String appPackage) {
        String name = ApplicationInfoUtil.getProgramNameByPackageName(this, appPackage);
        if (TextUtils.isEmpty(name)) {
            RxToast.warning(getString(R.string.uninstall));
            return false;
        }
        return true;
    }
}
