package com.embednet.wdluo.JackYan.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.embednet.wdluo.JackYan.BuildConfig;
import com.embednet.wdluo.JackYan.Constants;
import com.embednet.wdluo.JackYan.MyApplication;
import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.module.UserInfo;
import com.embednet.wdluo.JackYan.module.result.LoginResult;
import com.embednet.wdluo.JackYan.ui.SweetDialog;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import lab.dxythch.com.netlib.net.ServiceAPI;
import lab.dxythch.com.netlib.rx.RxNetSubscriber;
import laboratory.dxy.jack.com.jackupdate.util.StatusBarUtils;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：Jack
 * 创建时间：2018/1/11
 */
public class BaseActivity extends AutoLayoutActivity {

    protected SharedPreferences sharedPreferences;

    String titleText = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (titleText != null)
            L.d("【" + titleText + "】：onCreate");
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
        this.titleText = titleText;
        TextView title = (TextView) findViewById(R.id.Title);
        if (title != null)
            title.setText(titleText);
    }

    public void setTitleText(@StringRes int titleText) {
        this.titleText = getString(titleText);
        TextView title = (TextView) findViewById(R.id.Title);
        if (title != null)
            title.setText(titleText);
    }


    @Override
    protected void onStart() {
        super.onStart();
        L.d("【" + titleText + "】：onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.d("【" + titleText + "】：onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.d("【" + titleText + "】：onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.d("【" + titleText + "】：onStop");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        L.d("【" + titleText + "】：onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d("【" + titleText + "】：onDestroy");
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

    public String getUserId() {
        return sharedPreferences.getString(Constants.UserId, "");
    }

    public void setUserId(String userId) {
        sharedPreferences.edit().putString(Constants.UserId, userId).apply();
    }


    public UserInfo getUserInfo() {
        UserInfo info = (UserInfo) MyApplication.aCache.getAsObject(Constants.UserInfo);
        if (info == null) {
            info = new UserInfo();
            info.weight = 60;
            info.height = 175;
            info.sex = 0;
            info.age = 25;
            info.stepsTarget = 5000;
            info.name = getString(R.string.user);
            putUserInfo(info);
        }
        return info;
    }

    public static void putUserInfo(UserInfo userInfo) {
        MyApplication.aCache.put(Constants.UserInfo, userInfo);
    }


    public void loginSuccess(final LoginResult result) {
        L.d("登录信息：" + result.toString());
        final SweetAlertDialog dialog = new SweetDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(getString(R.string.loginSuccess))
                .setContentText("正在进行初始化\n请保证网络通畅")
                .setConfirmText("ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (BuildConfig.DEBUG) {
                            //登录成功
                            startActivity(new Intent(BaseActivity.this, ScnnerActivity.class));
                            finish();
                        }
                    }
                });
        dialog.show();
        new CountDownTimer(1000 * 7, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                dialog.setTitleText("初始化失败")
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        };

        String wechat = result.getLoginType().equals(Constants.WECHAR) ? result.getUserInfo().getOpenId() : "";
        String qq = result.getLoginType().equals(Constants.QQ) ? result.getUserInfo().getOpenId() : "";

        ServiceAPI.getInstance().updateUserInfo(result.getUserInfo().getNickname(), result.getPhone(), result.getEmail(), wechat, qq, new RxNetSubscriber<String>() {
            @Override
            protected void _onNext(String s) {
                L.d("更新用户信息：" + s);

                UserInfo info = getUserInfo();
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean(Constants.isLogin, true);
                edit.apply();

                info.sex = result.getUserInfo().getSex() - 1;
                info.name = result.getUserInfo().getNickname();
                info.heardImgUrl = result.getUserInfo().getHeadImageUrl();
                info.phone = result.getPhone();
                info.email = result.getEmail();
                putUserInfo(info);
                L.d("用户信息：" + info.toString());
                dialog.setTitleText("初始化成功")
                        .showContentText(false)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismissWithAnimation();
                        //登录成功
                        startActivity(new Intent(BaseActivity.this, ScnnerActivity.class));
                        finish();
                    }
                }, 1000);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dialog.setTitleText("初始化失败")
                        .showContentText(false)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }

        });
    }

}
