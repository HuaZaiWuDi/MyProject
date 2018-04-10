package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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

import cn.qqtheme.framework.picker.NumberPicker;
import cn.qqtheme.framework.picker.OptionPicker;

public class UserInfoActivity extends BaseAvtivity {

    UserInfo info;
    TextView text_weight, text_height, text_sex, text_age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
//        StatusBarUtils.from(this).setTransparentStatusbar(true).setHindStatusBar(true).process();


        setTitleText(R.string.settingUserInfo);
        setBack();

        final CircleImageView fab = findViewById(R.id.userImg);
        final TextView userName = findViewById(R.id.userName);


        text_weight = findViewById(R.id.text_weight);
        text_height = findViewById(R.id.text_height);
        text_sex = findViewById(R.id.text_sex);
        text_age = findViewById(R.id.text_age);

        info = (UserInfo) MyApplication.aCache.getAsObject("UserInfo");


        if (info != null) {
            userName.setText(info.name);
            Glide.with(this)
                    .asDrawable()
                    .apply(RequestOptions.placeholderOf(R.mipmap.img_heard))
                    .load(info.heardImgUrl)
                    .into(fab);

            text_sex.setText(info.sex == 0 ? getString(R.string.man) : info.sex == 1 ? getString(R.string.woman) : getString(R.string.noknown));
            text_age.setText(info.age + getString(R.string.year));
            text_height.setText(info.height + "cm");
            text_weight.setText(info.weight + "kg");
        } else {
            info = new UserInfo();
            info.weight = 60;
            info.height = 175;
            info.sex = 0;
            info.age = 25;
            MyApplication.aCache.put("UserInfo", info);
        }

    }

    public void sex(View v) {
        OptionPicker picker = new OptionPicker(this, new String[]{
                getString(R.string.man), getString(R.string.woman)
        });
        picker.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        picker.setHeight((int) (picker.getScreenHeightPixels() * 0.4));
        picker.setTitleText(R.string.settingSex);
        picker.setCanceledOnTouchOutside(false);
        picker.setDividerRatio(0.2f);
        picker.setSelectedIndex(info.sex > 1 ? 1 : info.sex);
        picker.setCycleDisable(true);
        picker.setTextSize(21);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                text_sex.setText(item);
                info.sex = index;
            }
        });
        picker.show();
    }

    public void age(View v) {
        NumberPicker picker = new NumberPicker(this);
        picker.setTitleText(getString(R.string.settingAge));
        picker.setGravity(Gravity.BOTTOM);
        picker.setHeight((int) (picker.getScreenHeightPixels() * 0.4));
        picker.setCycleDisable(false);
        picker.setDividerRatio(0.2f);
        picker.setOffset(2);//偏移量
        picker.setRange(0, 100, 1);//数字范围
        picker.setSelectedItem(info.age);
        picker.setTextSize(21);
        picker.setLabel(getString(R.string.year));
        picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                text_age.setText(item.intValue() + getString(R.string.year));
                info.age = item.intValue();
            }
        });
        picker.show();
    }

    public void height(View v) {
        NumberPicker picker = new NumberPicker(this);
        picker.setTitleText(getString(R.string.settingHeight));
        picker.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        picker.setHeight((int) (picker.getScreenHeightPixels() * 0.4));
        picker.setCycleDisable(false);
        picker.setDividerRatio(0.2f);
        picker.setOffset(2);//偏移量
        picker.setRange(0, 250, 1);//数字范围
        picker.setSelectedItem(info.height);
        picker.setTextSize(21);
        picker.setLabel("cm");
        picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                text_height.setText(item.intValue() + "cm");
                info.height = item.intValue();
            }
        });
        picker.show();
    }

    public void weight(View v) {
        NumberPicker picker = new NumberPicker(this);
        picker.setTitleText(getString(R.string.settingWeight));
        picker.setGravity(Gravity.BOTTOM);
        picker.setHeight((int) (picker.getScreenHeightPixels() * 0.4));
        picker.setCycleDisable(false);
        picker.setDividerRatio(0.2f);
        picker.setOffset(2);//偏移量
        picker.setRange(0, 250, 1);//数字范围
        picker.setSelectedItem(info.weight);
        picker.setTextSize(21);
        picker.setLabel("kg");
        picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                text_weight.setText(item.intValue() + "kg");
                info.weight = item.intValue();
            }
        });
        picker.show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.aCache.put("UserInfo", info);
    }

    int dataInt = 0;
    AlertDialog dialog;
    String value;

    private void setUserNameDialog(final String Title, final int position, String unit) {

        View view = LayoutInflater.from(this).inflate(R.layout.pop_username, null);


        ((TextView) view.findViewById(R.id.title)).setText(Title);
        ((TextView) view.findViewById(R.id.unit)).setText(unit);
        PickerView pickerView = view.findViewById(R.id.mPickerView);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 0) {
                    text_sex.setText(value);
                }
                try {
                    if (!TextUtils.isEmpty(value)) {
                        dataInt = Integer.parseInt(value);

                        switch (position) {
                            case 1:
                                text_age.setText(getString(R.string.year, dataInt));
                                break;
                            case 2:
                                text_height.setText(dataInt + "cm");
                                break;
                            case 3:
                                text_weight.setText(dataInt + "kg");
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
                }


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
                value = text;
                L.d("(year-dataInt):" + dataInt);
                if (position == 0) {
                    info.sex = value == getString(R.string.man) ? 0 : 1;
                }
                try {
                    if (!TextUtils.isEmpty(value)) {
                        dataInt = Integer.parseInt(value);

                        switch (position) {
                            case 1:
                                info.age = dataInt;
                                break;
                            case 2:
                                info.height = dataInt;
                                break;
                            case 3:
                                info.weight = dataInt;
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
                }

            }
        });
        dialog = new AlertDialog.Builder(this)
                .setView(view)
                .show();

    }


}
