package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.embednet.wdluo.bleplatformsdkdemo.MyApplication;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.module.UserInfo;
import com.embednet.wdluo.bleplatformsdkdemo.ui.CircleImageView;

public class PersonalActivity extends BaseAvtivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        setTitleText("个人中心");
        setBack();

        UserInfo info = (UserInfo) MyApplication.aCache.getAsObject("UserInfo");

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
            if (info.phone != null)
                text.setText(info.phone);
        }

    }


    public void SingOut(View v) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear();
        edit.putBoolean("AutoLogin", false);
        edit.apply();
        MyApplication.getBmobUser().logOut();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(PersonalActivity.this, Login2Activity.class));
                finish();
            }
        }, 500);
    }

    public void login(View v) {

    }

    public void stepsTarget(View v) {

    }

    public void stepsNotify(View v) {

    }

    public void stepsFunction(View v) {

    }

    public void UserInfo(View v) {
        startActivity(new Intent(this, UserInfoActivity.class));
    }

    public void deviceCanter(View v) {
        startActivity(new Intent(this, ControlActivity.class));

    }

    public void help(View v) {

    }
}
