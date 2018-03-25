package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.embednet.wdluo.bleplatformsdkdemo.MyApplication;
import com.embednet.wdluo.bleplatformsdkdemo.R;

public class PersonalActivity extends BaseAvtivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        setTitleText("个人中心");
        setBack();

        getFragmentManager().beginTransaction().replace(R.id.mFrameLayout, new MyPreferenceFragment()).commit();

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
}
