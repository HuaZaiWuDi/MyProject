package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.embednet.wdluo.bleplatformsdkdemo.MyApplication;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.module.UserInfo;
import com.embednet.wdluo.bleplatformsdkdemo.ui.PickerView;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;
import com.embednet.wdluo.bleplatformsdkdemo.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import laboratory.dxy.jack.com.jackupdate.util.StatusBarUtils;

public class UserInfoActivity extends BaseAvtivity {

    View parentView;
    PopupWindow UserNamePopW;
    UserInfo info;
    TextView text_weight, text_height, text_sex, text_age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        StatusBarUtils.from(this).setTransparentStatusbar(true).setHindStatusBar(true).process();


        text_weight = findViewById(R.id.text_weight);
        text_height = findViewById(R.id.text_height);
        text_sex = findViewById(R.id.text_sex);
        text_age = findViewById(R.id.text_age);

        info = (UserInfo) MyApplication.aCache.getAsObject("UserInfo");


        parentView = LayoutInflater.from(this).inflate(R.layout.activity_user_info, null, false);


        final Toolbar toolbar = findViewById(R.id.toolbar);
        if (info != null)
            toolbar.setTitle(info.name);


        text_sex.setText(info.sex == 0 ? "男" : info.sex == 1 ? "女" : "未知");
        text_age.setText(info.age + "岁");
        text_height.setText(info.height + "cm");
        text_weight.setText(info.weight + "kg");
    }

    public void sex(View v) {
        setUserNamePopW(getString(R.string.settingSex), 0);
        ScreenUtil.setBackgroundAlpha(UserInfoActivity.this, 0.5f);
        UserNamePopW.showAtLocation(parentView, Gravity.BOTTOM, 0, -90);
    }

    public void age(View v) {
        setUserNamePopW(getString(R.string.settingAge), 1);
        ScreenUtil.setBackgroundAlpha(UserInfoActivity.this, 0.5f);
        UserNamePopW.showAtLocation(parentView, Gravity.BOTTOM, 0, -90);
    }

    public void height(View v) {
        setUserNamePopW(getString(R.string.settingHeight), 2);
        ScreenUtil.setBackgroundAlpha(UserInfoActivity.this, 0.5f);
        UserNamePopW.showAtLocation(parentView, Gravity.BOTTOM, 0, -90);
    }

    public void weight(View v) {
        setUserNamePopW(getString(R.string.settingWeight), 3);
        ScreenUtil.setBackgroundAlpha(UserInfoActivity.this, 0.5f);
        UserNamePopW.showAtLocation(parentView, Gravity.BOTTOM, 0, -90);

    }


    int dataInt = 0;

    private void setUserNamePopW(final String Title, final int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_username, null, false);
        UserNamePopW = new PopupWindow(this);
        UserNamePopW.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        UserNamePopW.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        UserNamePopW.setBackgroundDrawable(new ColorDrawable(0x00000000));
        UserNamePopW.setFocusable(true);
        UserNamePopW.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);//解决popup被输入法挡住的问题
        UserNamePopW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        UserNamePopW.setOutsideTouchable(true);
        UserNamePopW.setContentView(view);
        UserNamePopW.setAnimationStyle(R.style.popAnim);

        UserNamePopW.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ScreenUtil.setBackgroundAlpha(UserInfoActivity.this, 1.0f);
            }
        });

        final TextView title = (TextView) view.findViewById(R.id.title);
        TextView ok = (TextView) view.findViewById(R.id.ok);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);

        PickerView pickerView = (PickerView) view.findViewById(R.id.mPickerView);


        title.setText(Title);

        final List<String> data = new ArrayList<>();

        switch (position) {
            case 1:
                data.clear();
                dataInt = info.age;
                for (int j = 0; j <= 90; j++) {
                    data.add(j + "");
                }
                pickerView.setData(data);
                pickerView.setSelected(dataInt);

                break;
            case 2:
                data.clear();
                dataInt = info.height;
                for (int j = 0; j < 250; j++) {
                    data.add(j + "");
                }

                pickerView.setData(data);
                pickerView.setSelected(dataInt);
                break;
            case 3:
                data.clear();
                dataInt = info.weight;
                for (int i = 0; i < 200; i++) {
                    data.add(i + "");
                }

                pickerView.setData(data);
                pickerView.setSelected(dataInt);
                break;
            case 0:
                data.clear();
                dataInt = info.sex;
                if (dataInt < 0) dataInt = 0;
                data.add("男");
                data.add("女");
                pickerView.setData(data);
                pickerView.setSelected(dataInt);
                break;
        }

        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                //这里数据的监听会有
                L.d("(year-dataInt):" + dataInt);
                if (position == 0) {
                    text_sex.setText(text);
                    info.sex = text == "男" ? 0 : 1;
                }
                try {
                    if (!TextUtils.isEmpty(text)) {
                        dataInt = Integer.parseInt(text);

                        switch (position) {

                            case 1:
                                text_age.setText(dataInt + "岁");
                                info.age = dataInt;
                                break;
                            case 2:
                                text_height.setText(dataInt + "cm");
                                info.height = dataInt;
                                break;
                            case 3:
                                text_weight.setText(dataInt + "kg");
                                info.weight = dataInt;
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserNamePopW.dismiss();
                MyApplication.aCache.put("UserInfo", info);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserNamePopW.dismiss();
            }
        });

    }

}
