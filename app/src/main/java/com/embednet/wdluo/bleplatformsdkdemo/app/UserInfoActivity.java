package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.embednet.wdluo.bleplatformsdkdemo.MyApplication;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.module.UserInfo;
import com.embednet.wdluo.bleplatformsdkdemo.ui.CircleImageView;
import com.embednet.wdluo.bleplatformsdkdemo.ui.PickerView;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;

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


        final CircleImageView fab = findViewById(R.id.fab);
        AppBarLayout app_bar = findViewById(R.id.app_bar);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == Math.abs(appBarLayout.getTotalScrollRange())) {
                    fab.setVisibility(View.GONE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });

        text_weight = findViewById(R.id.text_weight);
        text_height = findViewById(R.id.text_height);
        text_sex = findViewById(R.id.text_sex);
        text_age = findViewById(R.id.text_age);

        info = (UserInfo) MyApplication.aCache.getAsObject("UserInfo");


        parentView = LayoutInflater.from(this).inflate(R.layout.activity_user_info, null, false);


        final Toolbar toolbar = findViewById(R.id.toolbar);
        if (info != null) {
            toolbar.setTitle(info.name);
            Glide.with(this)
                    .asDrawable()
                    .apply(RequestOptions.placeholderOf(R.mipmap.img_heard))
                    .load(info.heardImgUrl)
                    .into(fab);

        }


        text_sex.setText(info.sex == 0 ? getString(R.string.man) : info.sex == 1 ? getString(R.string.woman) : getString(R.string.noknown));
        text_age.setText(getString(R.string.year, info.age));
        text_height.setText(info.height + "cm");
        text_weight.setText(info.weight + "kg");
    }

    public void sex(View v) {
        setUserNameDialog(getString(R.string.settingSex), 0);
    }

    public void age(View v) {
        setUserNameDialog(getString(R.string.settingAge), 1);
    }

    public void height(View v) {
        setUserNameDialog(getString(R.string.settingHeight), 2);
    }

    public void weight(View v) {
        setUserNameDialog(getString(R.string.settingWeight), 3);

    }


    int dataInt = 0;


    private void setUserNameDialog(final String Title, final int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_username, null, false);


        PickerView pickerView = new PickerView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);
        pickerView.setLayoutParams(params);

//        PickerView pickerView = view.findViewById(R.id.mPickerView);


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
                data.add(getString(R.string.man));
                data.add(getString(R.string.woman));
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
                    info.sex = text == getString(R.string.man) ? 0 : 1;
                }
                try {
                    if (!TextUtils.isEmpty(text)) {
                        dataInt = Integer.parseInt(text);

                        switch (position) {

                            case 1:
                                text_age.setText(getString(R.string.year, dataInt));
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

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyApplication.aCache.put("UserInfo", info);
                    }
                })
                .setTitle(Title)
                .setNegativeButton(R.string.cancel, null)
                .setView(pickerView)
                .show();

    }


}
