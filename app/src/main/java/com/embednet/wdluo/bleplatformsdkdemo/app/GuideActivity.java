package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.embednet.wdluo.bleplatformsdkdemo.R;

import laboratory.dxy.jack.com.jackupdate.util.StatusBarUtils;

public class GuideActivity extends AppCompatActivity {
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

        splashText.setText(R.string.app_name);
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
    }
}
