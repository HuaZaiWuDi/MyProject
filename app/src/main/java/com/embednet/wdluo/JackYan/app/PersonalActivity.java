package com.embednet.wdluo.JackYan.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import com.embednet.wdluo.JackYan.ui.BasePopupWindow;
import com.embednet.wdluo.JackYan.ui.CircleImageView;
import com.embednet.wdluo.JackYan.ui.PickerView;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.NumberPicker;
import laboratory.dxy.jack.com.jackupdate.ui.RxToast;
import laboratory.dxy.jack.com.jackupdate.util.L;
import rx.functions.Action1;

import static laboratory.dxy.jack.com.jackupdate.util.ImageTakerHelper.REQUEST_ALBUM;
import static laboratory.dxy.jack.com.jackupdate.util.ImageTakerHelper.REQUEST_CAMERA;
import static laboratory.dxy.jack.com.jackupdate.util.ImageTakerHelper.openAlbum;
import static laboratory.dxy.jack.com.jackupdate.util.ImageTakerHelper.openCamera;
import static laboratory.dxy.jack.com.jackupdate.util.ImageTakerHelper.readBitmapFromAlbumResult;
import static laboratory.dxy.jack.com.jackupdate.util.ImageTakerHelper.readBitmapFromCameraResult;

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
        popInit();


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
                startActivity(new Intent(PersonalActivity.this, Login2Activity.class));
                finish();
            }
        }, 500);
    }

    public void login(View v) {

    }

    public void userImg(View v) {
        window.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
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


    BasePopupWindow window;

    private void popInit() {
        window = new BasePopupWindow(this);
        window.initPop(getString(R.string.choosePhoto), getString(R.string.choosePhotoMode), new String[]{getString(R.string.takePhoto), getString(R.string.changeByAlbum)});
        window.setOnItemClickLisetener(new BasePopupWindow.OnItemClickLisetener() {
            @Override
            public void OnClick(int position, final String text) {
                if (Build.VERSION.SDK_INT >= 23) {
                    new RxPermissions(PersonalActivity.this)
                            .request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if (aBoolean) {
                                        if (text.equals(getString(R.string.takePhoto))) {
                                            openCamera(PersonalActivity.this, getPackageName());
                                        } else {
                                            openAlbum(PersonalActivity.this);
                                        }
                                    } else {
                                        L.d("权限请求失败");
                                        RxToast.error(getString(R.string.NoPromiss));
                                    }
                                }
                            });
                } else {
                    if (text.equals(getString(R.string.takePhoto))) {
                        openCamera(PersonalActivity.this, getPackageName());
                    } else {
                        openAlbum(PersonalActivity.this);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String imgPath = null;
        try {
            L.d("data:" + data);
            if (data != null) {
                if (requestCode == REQUEST_ALBUM) {
                    imgPath = readBitmapFromAlbumResult(this, data);
                }

            } else {
                if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
                    imgPath = readBitmapFromCameraResult(this, data);
                }
            }
            L.d("图片路径：" + imgPath);
            if (!TextUtils.isEmpty(imgPath))
                showUserImg(imgPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showUserImg(String imgPath) {

        Glide.with(this)
                .asDrawable()
                .apply(new RequestOptions().placeholder(R.mipmap.img_heard))
                .load(imgPath)
                .into(userImg);
        info.heardImgUrl = imgPath;
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
