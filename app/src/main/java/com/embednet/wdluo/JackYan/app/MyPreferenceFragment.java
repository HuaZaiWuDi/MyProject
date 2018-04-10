package com.embednet.wdluo.JackYan.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.embednet.wdluo.JackYan.Constants;
import com.embednet.wdluo.JackYan.R;

import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 项目名称：LfteAndRightTouchDemo
 * 类描述：
 * 创建人：Jack
 * 创建时间：2017/7/24
 */
public class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    EditTextPreference edit;
    Preference login;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.my_preference);

        edit = (EditTextPreference) findPreference(Constants.UserName);

        login = findPreference("login");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


        int sex = sharedPreferences.getInt(Constants.UserSex, 0);
        final String userImage = sharedPreferences.getString(Constants.UserImgHeard, "");

        login.setTitle(sharedPreferences.getString(Constants.UserName, sharedPreferences.getString(Constants.UserPhone, "用户")));
        login.setSummary("登录账号以同步数据");

        edit.setSummary(sharedPreferences.getString(Constants.UserName, "请输入名字"));


        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                try {
                    subscriber.onNext(Glide.with(getActivity())
                            .load(userImage)
                            .submit(500, 500)
                            .get());
                } catch (InterruptedException | ExecutionException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Drawable>() {
                    @Override
                    public void call(Drawable drawable) {
                        login.setIcon(drawable);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        login.setIcon(R.mipmap.img_heard);
                    }
                });

    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("login")) {
            startActivity(new Intent(getActivity(), Login2Activity.class));
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public void onResume() {
        super.onResume();
        //必须要注册，不然监听事件不响应
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        Log.d("改变3", s);
        if (s.equals("UserName")) {
            edit.setSummary(s);
            login.setTitle(s);
        } else if (s.equals("AutoLogin")) {

        } else if (s.equals("AutoConnect")) {

        } else if (s.equals("login")) {

        }
    }
}
