package com.embednet.wdluo.JackYan.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.util.L;
import com.embednet.wdluo.JackYan.util.RxActivityUtils;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import laboratory.dxy.jack.com.jackupdate.util.StatusBarUtils;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：Jack
 * 创建时间：2018/1/11
 */
public class BaseAvtivity extends AutoLayoutActivity {

    protected SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d("onCreate");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        L.d("测试代码");
        StatusBarUtils.from(this).setStatusBarColor(Color.parseColor("#333333")).process();
        RxActivityUtils.addActivity(this);
    }

    public void setBack() {
        ImageView back = findViewById(R.id.back);
        if (back != null)
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
    }


    public void setTitleText(String titleText) {
        TextView title = (TextView) findViewById(R.id.Title);
        if (title != null)
            title.setText(titleText);
    }

    public void setTitleText(int titleText) {
        TextView title = (TextView) findViewById(R.id.Title);
        if (title != null)
            title.setText(titleText);
    }


    @Override
    protected void onStart() {
        super.onStart();
        L.d("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.d("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.d("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.d("onStop");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        L.d("onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d("onDestroy");
    }


    protected void transitionAnimation(Activity from, Class to, Pair<View, String>[] pairs) {

        Intent intent = new Intent(from, to);
        //主要的语句
        //通过makeSceneTransitionAnimation传入多个Pair
        //每个Pair将一个当前Activity的View和目标Activity中的一个Key绑定起来
        //在目标Activity中会调用这个Key
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                from,
                pairs);
        // ActivityCompat是android支持库中用来适应不同android版本的
        ActivityCompat.startActivity(from, intent, activityOptions.toBundle());
    }


    protected String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(new Date());
    }

    public static String setFormat(long value, String format) {
        return new DecimalFormat(format).format(value);
    }


    //--------------------------------------------适配屏幕
    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT)) {
            view = new AutoFrameLayout(context, attrs);
        }

        if (name.equals(LAYOUT_LINEARLAYOUT)) {
            view = new AutoLinearLayout(context, attrs);
        }

        if (name.equals(LAYOUT_RELATIVELAYOUT)) {
            view = new AutoRelativeLayout(context, attrs);
        }

        if (view != null) return view;

        return super.onCreateView(name, context, attrs);
    }


//    public void LoaderImage(View view, Object img) {
//        RequestOptions options = new RequestOptions();
//        options.centerCrop();
//        options.placeholder(R.mipmap.img_heard);
//        options.error(R.mipmap.ic_type_folder);
//
//        Glide.with(this)
//                .asDrawable()
//                .apply(options)
//                .load(img)
//                .into(view);
//    }

}
