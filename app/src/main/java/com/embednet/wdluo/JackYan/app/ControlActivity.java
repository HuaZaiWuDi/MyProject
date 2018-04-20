package com.embednet.wdluo.JackYan.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.ui.SweetDialog;

import cn.qqtheme.framework.picker.OptionPicker;

public class ControlActivity extends BaseActivity {


    String TGA = ControlActivity.class.getSimpleName();


    ImageView power, broad;
    TextView inputPower;
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
        inputPower = findViewById(R.id.powerValue);
    }


    public void isOpen(View v) {
        isPower = !isPower;
        power.setBackgroundResource(isPower ? R.mipmap.icon_on : R.mipmap.icon_off);
    }

    public void value(View v) {
        OptionPicker picker = new OptionPicker(this, getResources().getStringArray(R.array.inputPower));
        picker.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        picker.setHeight((int) (picker.getScreenHeightPixels() * 0.4));
        picker.setTitleText(R.string.settingSex);
        picker.setCanceledOnTouchOutside(false);
        picker.setDividerRatio(0.2f);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(true);
        picker.setTextSize(21);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                inputPower.setText(item);
            }
        });
        picker.show();
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
                .setTitleText(getString(R.string.isNewVersion))
                .show();
    }

    public void DFU(View v) {
        startActivity(new Intent(this, UpdateDFUActivity.class));
    }

    public void DeviceInfo(View v) {
        new SweetDialog(this)
                .setDaration(2000)
                .setTitleText(getString(R.string.DeviceInfo))
                .setContentText(getString(R.string.deviceInfo, "", "", "", ""))
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


}
