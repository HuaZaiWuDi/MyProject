package com.embednet.wdluo.JackYan.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.embednet.wdluo.JackYan.MyApplication;
import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.module.UserInfo;
import com.embednet.wdluo.JackYan.ui.CircleImageView;
import com.embednet.wdluo.JackYan.ui.PickerView;
import com.embednet.wdluo.JackYan.util.RxActivityUtils;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.NumberPicker;

public class PersonalActivity extends BaseAvtivity {

    ImageView switch_stepsMark, switch_stepsFunction;
    boolean isMark, isFun;
    UserInfo info;
    TextView text_stepsTarget;
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

        info = (UserInfo) MyApplication.aCache.getAsObject("UserInfo");

        userImg = findViewById(R.id.userImg);
        if (info != null) {
            if (info.heardImgUrl != null)
                Glide.with(this)
                        .asDrawable()
                        .apply(new RequestOptions().placeholder(R.mipmap.img_heard))
                        .load(info.heardImgUrl)
                        .into(userImg);
            TextView title = findViewById(R.id.UserName);
            if (info.name != null)
                title.setText(info.name);
            TextView text = findViewById(R.id.UserPhone);
            if (info.phone != null)
                text.setText(info.phone);
            text_stepsTarget.setText(info.stepsTarget + "");
        } else {
            info = new UserInfo();
            info.stepsTarget = 5000;
            MyApplication.aCache.put("UserInfo", info);
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
                RxActivityUtils.finishAllActivity();
                startActivity(new Intent(PersonalActivity.this, Login2Activity.class));
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
    protected void onPause() {
        super.onPause();
        MyApplication.aCache.put("UserInfo", info);
    }

    int dataInt = 0;
    AlertDialog dialog;
    String value;

    private void setUserNameDialog(final String Title, String unit) {

        final View view = LayoutInflater.from(this).inflate(R.layout.pop_username, null);

        ((TextView) view.findViewById(R.id.title)).setText(Title);
        ((TextView) view.findViewById(R.id.unit)).setText(unit);
        PickerView pickerView = view.findViewById(R.id.mPickerView);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info.stepsTarget = Integer.parseInt(value);
                text_stepsTarget.setText(value);
                MyApplication.aCache.put("UserInfo", info);
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final List<String> data = new ArrayList<>();

        data.clear();
        for (int j = 1000; j <= 30000; j = j + 1000) {
            data.add(j + "");
        }
        dataInt = data.indexOf(info.stepsTarget + "");
        pickerView.setData(data);
        pickerView.setSelected(dataInt);

        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                //这里数据的监听会有
                value = text;

            }
        });
        dialog = new AlertDialog.Builder(this)
                .setView(view)
                .show();

    }

}
