package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.embednet.wdluo.bleplatformsdkdemo.R;
//import com.embednet.wdluo.bleplatformsdkdemo.ui.SweetDialog;


public class AboutActivity extends BaseAvtivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
//        StatusBarUtils.from(this).setTransparentStatusbar(true).process();
        setTitleText(R.string.help);
        setBack();
    }


    public void device(View v) {

    }


    public void APP(View v) {
//        new SweetDialog(this)
//                .setDaration(2000)
//                .setTitleText("已经是最新版本了！")
//                .show();

    }

    public void DFU(View v) {
        startActivity(new Intent(this, UpdateDFUActivity.class));
    }

}
