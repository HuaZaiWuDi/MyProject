package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.MyApplication;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.ui.RoundView;
import com.embednet.wdluo.bleplatformsdkdemo.util.ScreenUtil;
import com.embednet.wdluo.bleplatformsdkdemo.util.ShareUtlis;

import laboratory.dxy.jack.com.jackupdate.ui.RxToast;
import laboratory.dxy.jack.com.jackupdate.util.ApplicationInfoUtil;

public class ShareActivity extends BaseAvtivity {

    RoundView mRoundDisPlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        setTitleText("分享界面");
        setBack();

        mRoundDisPlayView = findViewById(R.id.mRoundDisPlayView);
        mRoundDisPlayView.setCentreText(0 / 60 + "", "步", "目标5000步");

    }


    public void share() {
        Bitmap bitmap = ScreenUtil.snapShotWithoutStatusBar(this);
        ShareUtlis.smpleShareImage(MyApplication.getApplication(), bitmap);
    }


    public void img_qq(View v) {
        if (checkApp(Constants.QQ)) {
            share();
        }
    }

    public void img_QQ_zone(View v) {
        if (checkApp(Constants.QQ)) {
            share();
        }
    }

    public void img_WX(View v) {
        if (checkApp(Constants.WECHAR)) {
            share();
        }
    }

    public void img_friends(View v) {
        if (checkApp(Constants.WECHAR)) {
            share();
        }
    }

    public void img_weibo(View v) {
        if (checkApp(Constants.WEIBO)) {
            share();
        }
    }

    public void img_facebook(View v) {
        if (checkApp(Constants.WEIBO)) {
            share();
        }
    }


    private boolean checkApp(String appPackage) {

        String name = ApplicationInfoUtil.getProgramNameByPackageName(this, appPackage);
        if (TextUtils.isEmpty(name)) {
            RxToast.warning("未安装");
            return false;
        }
        return true;
    }
}
