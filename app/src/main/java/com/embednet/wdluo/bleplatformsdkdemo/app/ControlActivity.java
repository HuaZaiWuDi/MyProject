package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.ui.SweetDialog;

import laboratory.dxy.jack.com.jackupdate.ui.recyclerview.CommonAdapter;

public class ControlActivity extends BaseAvtivity {


    String TGA = ControlActivity.class.getSimpleName();


    CommonAdapter adapter;

    ImageView power, broad;
    boolean isPower, isBroad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        setTitleText(R.string.controlCenter);
        setBack();

        power = findViewById(R.id.power);
        broad = findViewById(R.id.broad);
        isPower = isBroad = true;

    }


    public void isOpen(View v) {
        isPower = !isPower;
        power.setBackgroundResource(isPower ? R.mipmap.icon_on : R.mipmap.icon_off);
    }

    public void value(View v) {

    }

    public void broad(View v) {
        isBroad = !isBroad;
        broad.setBackgroundResource(isBroad ? R.mipmap.icon_on : R.mipmap.icon_off);
    }

    public void deviceCanter(View v) {
        startActivity(new Intent(this, ControlActivity.class));

    }

    public void help(View v) {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void APP(View v) {
        new SweetDialog(this)
                .setDaration(2000)
                .setTitleText("已经是最新版本了！")
                .show();
    }

    public void DFU(View v) {
        startActivity(new Intent(this, UpdateDFUActivity.class));
    }

    public void DeviceInfo(View v) {
        new SweetDialog(this)
                .setDaration(2000)
                .setTitleText(getString(R.string.DeviceInfo))
                .setContentText("设备名称：" + "\n" + "设备型号：" + "\n" + "设备SN序列号:" + "\n" + "设备固件版本号：" + "\n")
                .show();
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
