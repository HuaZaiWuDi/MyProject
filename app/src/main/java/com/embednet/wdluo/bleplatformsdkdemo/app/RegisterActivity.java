package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.support.v4.view.ViewCompat;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.MyApplication;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.module.SMSResultCode;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;
import com.embednet.wdluo.bleplatformsdkdemo.util.SmsContent;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import laboratory.dxy.jack.com.jackupdate.ui.RxToast;
import laboratory.dxy.jack.com.jackupdate.util.RxRegUtils;
import laboratory.dxy.jack.com.jackupdate.util.StatusBarUtils;
import laboratory.dxy.jack.com.jackupdate.util.Utils;
import laboratory.dxy.jack.com.jackupdate.util.timer.MyTimer;
import laboratory.dxy.jack.com.jackupdate.util.timer.MyTimerListener;

public class RegisterActivity extends BaseAvtivity {

    AutoCompleteTextView phoneOrEmail;
    private String phone;
    private TextView sendCode;
    private String SMSCode;
    private String password;
    private EditText code;
    private EditText passwordEdit;


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                Object[] message = (Object[]) intent.getExtras().get("pdus");      // 获取短信数据(可能有多段)
                for (Object pdu : message) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);    // 把短信数据封装成SmsMessage对象
                    Date date = new Date(sms.getTimestampMillis());             // 短信时间
                    String address = sms.getOriginatingAddress();               // 获取发信人号码
                    String originatingAddress = sms.getDisplayOriginatingAddress();
                    String serviceCenterAddress = sms.getServiceCenterAddress();
                    String body = sms.getMessageBody();                         // 短信内容

                    L.d("短信信息：" + date + ", " + address + ", " + body);
                    L.d("短信信息：" + address + ", " + originatingAddress + ", " + serviceCenterAddress);
                    if (body.contains("Mesh Room")) {
                        String regEx = "[^0-9]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(body);
                        SMSCode = m.replaceAll("").trim().toString().substring(0, 6);
                        code.setText(SMSCode);
                        return;
                    }
                }
            }
        }
    };


//    @Receiver(actions = Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
//    void receivedSMS(@Receiver.Extra("pdus") Serializable[] pdus) {
//        Object[] message = pdus;
//        for (Object pdu : message) {
//            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);    // 把短信数据封装成SmsMessage对象
//            Date date = new Date(sms.getTimestampMillis());             // 短信时间
//            String address = sms.getOriginatingAddress();               // 获取发信人号码
//            String originatingAddress = sms.getDisplayOriginatingAddress();
//            String serviceCenterAddress = sms.getServiceCenterAddress();
//            String body = sms.getMessageBody();                         // 短信内容
//
//            L.d("短信信息：" + date + ", " + address + ", " + body);
//            L.d("短信信息：" + address + ", " + originatingAddress + ", " + serviceCenterAddress);
//            if (body.contains("Mesh Room")) {
//                String regEx = "[^0-9]";
//                Pattern p = Pattern.compile(regEx);
//                Matcher m = p.matcher(body);
//                String smsContent = m.replaceAll("").trim().toString().substring(0, 6);
//                smscode.setText(smsContent);
//                return;
//            }
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        StatusBarUtils.from(this).setHindStatusBar(true).process();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(receiver, filter);


        Glide.with(this)
                .asDrawable()
                .load(R.drawable.login_bg)
                .into((ImageView) findViewById(R.id.img_bg));


        phoneOrEmail = findViewById(R.id.phoneOrEmail);
        Button login = findViewById(R.id.email_sign_in_button);
        passwordEdit = findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifySmsCodeAndLogin();
            }
        });

        sendCode = findViewById(R.id.sendCode);
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attemptLogin()) {
                    timeOut = 60;
                    timer.startTimer();
                    sendSMS();
                }
            }
        });

        code = findViewById(R.id.code);


        SmsContent smsContent = new SmsContent(this, new Handler(), code, phone);
        SMSCode = smsContent.getSmsContent();


        ViewCompat.setTransitionName(findViewById(R.id.email_sign_in_button), "linear");
        ViewCompat.setTransitionName(findViewById(R.id.title), "register");
        ViewCompat.setTransitionName(code, "code");
        ViewCompat.setTransitionName(sendCode, "sendCode");
        ViewCompat.setTransitionName(phoneOrEmail, Constants.UserPhone);
        ViewCompat.setTransitionName(passwordEdit, Constants.UserPasswrod);

    }

    int timeOut = 60;

    MyTimer timer = new MyTimer(1000, new MyTimerListener() {
        @Override
        public void enterTimer() {
            timeOut--;
            if (timeOut < 0) {
                timer.stopTimer();
                sendCode.setText("获取验证码");
                sendCode.setAlpha(1f);
                sendCode.setEnabled(true);
            } else {
                sendCode.setText(timeOut + "秒后重发");
                sendCode.setAlpha(0.5f);
                sendCode.setEnabled(false);
            }
        }
    });


    private boolean verifySmsCode() {
        SMSCode = code.getText().toString().trim();
        if (TextUtils.isEmpty(SMSCode)) return false;
        if (RxRegUtils.isNumeric(SMSCode)) return true;
        else return false;
    }

    private boolean attemptLogin() {
        phone = phoneOrEmail.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            phoneOrEmail.setError("请输入正确的手机号码");
            return false;
        }

        if (!isPhoneValid(phone)) {
            phoneOrEmail.setError("请输入正确的手机号码");
            return false;
        }

        return true;
    }

    private boolean VerifyPassword() {
        password = passwordEdit.getText().toString();
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && isPasswordValid(password)) {
            return true;
        }
        passwordEdit.setError(getString(R.string.error_invalid_password));
        return false;
    }


    private boolean isPasswordValid(String password) {

        if (RxRegUtils.isUsername(password)) return true;
        else {
            passwordEdit.setError("取值范围a-z,A-Z,0-9,必须是6-20位");
            return false;
        }
    }

    private boolean isPhoneValid(String phone) {

        if (TextUtils.isEmpty(phone)) return false;

        //TODO: Replace this with your own logic
        return Pattern.matches("^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$", phone);
    }

    private void sendSMS() {

        if (!Utils.isCanUseSim(this)) {
            RxToast.warning("SIM卡无效");
            return;
        }

        BmobSMS.requestSMSCode(phone, getString(R.string.app_name), new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                L.d("requestId:" + integer);
                if (e == null) {
                    L.d("验证码发送成功");
                    RxToast.success("验证码发送成功");

                } else {
                    L.d("验证码发送失败：" + e.getLocalizedMessage());
                    RxToast.error("验证码发送失败" + SMSResultCode.ErrorInfo(e.getErrorCode()));
                }
            }
        });
    }

    private void verifySmsCodeAndLogin() {
        if (!attemptLogin() || !verifySmsCode() || !VerifyPassword()) {
            RxToast.error("登录失败:" + SMSResultCode.ErrorInfo(109));
            return;
        }

        L.d("phone:" + phone);
        L.d("password:" + password);
        L.d("SMSCode:" + SMSCode);

        BmobUser bmobUser = MyApplication.getBmobUser();
        bmobUser.setMobilePhoneNumber(phone);
        bmobUser.setPassword(password);
        bmobUser.setUsername(phone);

        bmobUser.signOrLogin(SMSCode, new SaveListener<Object>() {
            @Override
            public void done(Object o, BmobException e) {
                if (e != null) {
                    L.d("注册成功");
                    RxToast.success("注册成功");
                    doLoginSuccess();
                } else {
                    L.d("注册失败:" + e.toString());
                    RxToast.error("登录失败:" + SMSResultCode.ErrorInfo(e.getErrorCode()));
                }
            }
        });
        L.d("电话号码：" + bmobUser.getMobilePhoneNumber());
        L.d("用户名：" + bmobUser.getUsername());


    }


    private void doLoginSuccess() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(Constants.UserPasswrod, password);
        edit.putString(Constants.UserPhone, phone);
        edit.putBoolean("AutoLogin", true);
        edit.apply();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //登录成功
                startActivity(new Intent(RegisterActivity.this, ScnnerActivity.class));
                finish();
            }
        }, 500);
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();

    }
}
