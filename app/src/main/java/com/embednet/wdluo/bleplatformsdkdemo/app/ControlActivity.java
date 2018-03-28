package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.embednet.wdluo.bleplatformsdkdemo.R;

import java.util.ArrayList;
import java.util.List;

import laboratory.dxy.jack.com.jackupdate.ui.recyclerview.CommonAdapter;

public class ControlActivity extends BaseAvtivity {


    String TGA = ControlActivity.class.getSimpleName();



    CommonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        setTitleText("控制界面");
        setBack();

    }


    public void isOpen(View v) {

    }

    public void value(View v) {

    }

    public void broad(View v) {

    }

    public void device(View v) {

    }

    public void DFU(View v) {
        startActivity(new Intent(this, UpdateDFUActivity.class));
    }


    /**
     * * byte[2]=表示开机，
     * byte[3]=输出功率中0x00表示无充电（0x01表示在充电）
     * byte[4]=剩余电量75%，
     * byte[5]=负载阻值165/100=1.65欧姆，
     * byte[6]=广播关闭，
     * byte[7]=设备类型为0x80，
     * byte[8]=设备固件版本号0X01，
     */


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public class Item {
        String title;
        String text;
        int icon;
        boolean isOpen;


    }

}
