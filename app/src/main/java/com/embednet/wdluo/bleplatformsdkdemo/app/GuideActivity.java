package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;
import com.tbruyelle.rxpermissions.RxPermissions;

import laboratory.dxy.jack.com.jackupdate.ui.RxToast;
import rx.functions.Action1;

public class GuideActivity extends AppCompatActivity {
    ImageView img_bg, splashImg;
    TextView splashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        img_bg = (ImageView) findViewById(R.id.img_bg);
        splashImg = (ImageView) findViewById(R.id.splashImg);
        splashText = (TextView) findViewById(R.id.splashText);

        splashText.setText(R.string.appName);
        img_bg.setImageResource(R.drawable.pic_bg_ocean);
        splashImg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.layout_up));

        splashImg.getAnimation().setFillAfter(true);

        splashText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.text_selce));


        img_bg.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(GuideActivity.this, Login2Activity.class));
                finish();
            }
        }, 2000);

        sheckPromission();
    }


    private void sheckPromission() {
        //定位权限
        if (Build.VERSION.SDK_INT >= 23)
            new RxPermissions(this)
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                L.d("权限请求成功");
                            } else {
                                L.d("权限请求失败");
                                RxToast.error(getString(R.string.NoPromiss));
                            }
                        }
                    });
    }
}
