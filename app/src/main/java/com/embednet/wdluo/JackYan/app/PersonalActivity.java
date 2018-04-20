package com.embednet.wdluo.JackYan.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.embednet.wdluo.JackYan.Constants;
import com.embednet.wdluo.JackYan.MyApplication;
import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.module.UserInfo;
import com.embednet.wdluo.JackYan.ui.CircleImageView;
import com.embednet.wdluo.JackYan.util.RxActivityUtils;

import cn.qqtheme.framework.picker.NumberPicker;

public class PersonalActivity extends BaseActivity {

    ImageView switch_stepsMark, switch_stepsFunction;
    boolean isMark, isFun;
    UserInfo info;
    TextView text_stepsTarget, title, text;
    View parent;
    CircleImageView userImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        setTitleText(R.string.Usercontrol);
        setBack();
//        popInit();


        parent = findViewById(R.id.parent);
        text_stepsTarget = findViewById(R.id.text_stepsTarget);
        switch_stepsMark = findViewById(R.id.switch_stepsMark);
        switch_stepsFunction = findViewById(R.id.switch_stepsFunction);
        isMark = isFun = true;


    }


    public void SingOut(View v) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear();
        MyApplication.aCache.remove(Constants.UserInfo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RxActivityUtils.skipActivityAndFinishAll(PersonalActivity.this, Login2Activity.class);
            }
        }, 500);
    }

    public void login(View v) {

    }

    public void stepsTarget(View v) {
        NumberPicker picker = new NumberPicker(this);
        picker.setTitleText(getString(R.string.targetSteps));
        picker.setGravity(Gravity.BOTTOM);
        picker.setHeight((int) (picker.getScreenHeightPixels() * 0.4));
        picker.setCycleDisable(false);
        picker.setDividerRatio(0.2f);
        picker.setOffset(2);//偏移量
        picker.setRange(1000, 30000, 1000);//数字范围
        picker.setSelectedItem(info.stepsTarget);
        picker.setTextSize(21);
        picker.setLabel(getString(R.string.step));
        picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                text_stepsTarget.setText(item.intValue() + getString(R.string.step));
                info.stepsTarget = item.intValue();
            }
        });
        picker.show();
    }


    public void stepsNotify(View v) {
        isMark = !isMark;
        switch_stepsMark.setBackgroundResource(isMark ? R.mipmap.icon_on : R.mipmap.icon_off);
    }

    public void stepsFunction(View v) {
        isFun = !isFun;
        switch_stepsFunction.setBackgroundResource(isFun ? R.mipmap.icon_on : R.mipmap.icon_off);
    }

    public void UserInfo(View v) {
        startActivity(new Intent(this, UserInfoActivity.class));
    }

    public void deviceCanter(View v) {
        startActivity(new Intent(this, ControlActivity.class));

    }

    public void help(View v) {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void searchDevice(View v) {
        startActivity(new Intent(this, ScnnerActivity.class));
    }


    @Override
    protected void onStart() {
        super.onStart();
        info = getUserInfo();

        if (info != null) {
            userImg = findViewById(R.id.userImg);
            if (info.heardImgUrl != null)
                Glide.with(this)
                        .asDrawable()
                        .apply(new RequestOptions().placeholder(R.mipmap.img_heard))
                        .load(info.heardImgUrl)
                        .into(userImg);
            title = findViewById(R.id.UserName);
            if (info.name != null)
                title.setText(info.name);
            text = findViewById(R.id.UserPhone);
            if (info.phone != null)
                text.setText(info.phone);
            text_stepsTarget.setText(info.stepsTarget + "");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        putUserInfo(info);
    }


}
