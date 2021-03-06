package com.embednet.wdluo.JackYan.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.embednet.wdluo.JackYan.Constants;
import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.module.UserInfo;
import com.embednet.wdluo.JackYan.net.NetService;
import com.embednet.wdluo.JackYan.util.L;
import com.embednet.wdluo.JackYan.util.RxActivityUtils;
import com.embednet.wdluo.JackYan.util.Utils;
import com.google.gson.JsonObject;

import lab.dxythch.com.netlib.rx.NetManager;
import lab.dxythch.com.netlib.rx.RxManager;
import lab.dxythch.com.netlib.rx.RxNetSubscriber;
import laboratory.dxy.jack.com.jackupdate.util.StatusBarUtils;
import okhttp3.RequestBody;

public class GuideActivity extends BaseActivity {
    ImageView img_bg, splashImg;
    TextView splashText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        StatusBarUtils.from(this).setHindStatusBar(true).process();

        img_bg = (ImageView) findViewById(R.id.img_bg);
        splashImg = (ImageView) findViewById(R.id.splashImg);
        splashText = (TextView) findViewById(R.id.splashText);

        splashText.setText(R.string.appName);
        img_bg.setImageResource(R.drawable.pic_bg_ocean);
        splashImg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.layout_up));

        splashImg.getAnimation().setFillAfter(true);

        splashText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.text_selce));


    }


    @Override
    protected void onStart() {
        super.onStart();

        gotoNewPage();
    }


    private void gotoNewPage() {
        boolean isLogin = sharedPreferences.getBoolean(Constants.isLogin, false);
        boolean isBind = sharedPreferences.getBoolean(Constants.isBind, false);
        if (!isLogin) {
            img_bg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    RxActivityUtils.skipActivityAndFinish(GuideActivity.this, Login2Activity.class);
                }
            }, 2000);
        } else if (!isBind) {
            img_bg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    RxActivityUtils.skipActivityAndFinish(GuideActivity.this, Main2Activity.class);
                    RxActivityUtils.skipActivityAndFinish(GuideActivity.this, ScnnerActivity.class);
                }
            }, 2000);
        } else {
            img_bg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    RxActivityUtils.skipActivityAndFinish(GuideActivity.this, Main2Activity.class);
                }
            }, 2000);
        }
    }
}
