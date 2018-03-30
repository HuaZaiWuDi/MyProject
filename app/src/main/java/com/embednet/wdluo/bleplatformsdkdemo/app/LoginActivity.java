package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.MyApplication;
import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.login.QQlogin;
import com.embednet.wdluo.bleplatformsdkdemo.login.WeiBoLogin;
import com.embednet.wdluo.bleplatformsdkdemo.login.WeiXinLogin;
import com.embednet.wdluo.bleplatformsdkdemo.module.SMSResultCode;
import com.embednet.wdluo.bleplatformsdkdemo.module.UserInfo;
import com.embednet.wdluo.bleplatformsdkdemo.module.result.LoginResult;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import laboratory.dxy.jack.com.jackupdate.ui.RxToast;
import laboratory.dxy.jack.com.jackupdate.util.RxRegUtils;
import laboratory.dxy.jack.com.jackupdate.util.StatusBarUtils;
import laboratory.dxy.jack.com.jackupdate.util.Utils;
import laboratory.dxy.jack.com.jackupdate.util.timer.MyTimer;
import laboratory.dxy.jack.com.jackupdate.util.timer.MyTimerListener;
import rx.Subscriber;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseAvtivity {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText code;
    private Button mEmailSignInButton;
    private RelativeLayout Login2Code;
    private TextInputLayout login2Password;
    private TextView sendCode;
    private boolean isPassword = false;
    private String SMSCode;
    private String password;
    private String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarUtils.from(this).setHindStatusBar(true).process();
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

//        setTitleText("登录");
//        setBack();
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mPasswordView = (EditText) findViewById(R.id.password);


        login2Password = findViewById(R.id.login2Password);
        Login2Code = findViewById(R.id.Login2Code);


        Glide.with(this)
                .asDrawable()
                .load(R.drawable.login_bg)
                .into((ImageView) findViewById(R.id.img_bg));


        ViewCompat.setTransitionName(findViewById(R.id.QQ), Constants.UserName);
        ViewCompat.setTransitionName(findViewById(R.id.wechar), Constants.UserImgHeard);
        ViewCompat.setTransitionName(findViewById(R.id.weibo), Constants.UserSex);
        ViewCompat.setTransitionName(findViewById(R.id.email_sign_in_button), "linear");
        ViewCompat.setTransitionName(findViewById(R.id.title), "login");


        code = findViewById(R.id.code);

        sendCode = findViewById(R.id.sendCode);
        sendCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attemptLogin()) {
                    timeOut = 60;
                    timer.startTimer();
                    sendSMS();
                }
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                verifySmsCodeAndLogin();
            }
        });

    }

    int timeOut = 60;

    MyTimer timer = new MyTimer(1000, new MyTimerListener() {
        @Override
        public void enterTimer() {
            timeOut--;
            if (timeOut < 0) {
                timer.stopTimer();
                sendCode.setText(R.string.getSms);
                sendCode.setAlpha(1f);
                sendCode.setEnabled(true);
            } else {
                sendCode.setText(getString(R.string.reStart, timeOut));
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
        phone = mEmailView.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            mEmailView.setError(getString(R.string.error_field_required));
            return false;
        }

        if (!isPhoneValid(phone)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            return false;
        }

        return true;
    }

    private boolean VerifyPassword() {
        password = mPasswordView.getText().toString();
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && isPasswordValid(password)) {
            return true;
        }
        mPasswordView.setError(getString(R.string.error_invalid_password));
        return false;
    }


    private boolean isPasswordValid(String password) {

        if (RxRegUtils.isUsername(password)) return true;
        else {
            mPasswordView.setError(getString(R.string.ValueRange));
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
            RxToast.warning(getString(R.string.SimunValid));
            return;
        }

//        BmobSMS.requestSMSCode(this, phone, getString(R.string.app_name), new RequestSMSCodeListener() {
//            @Override
//            public void done(Integer integer, BmobException e) {
//                L.d("requestId:" + integer);
//                if (e == null) {
//                    L.d("验证码发送成功");
//                    RxToast.success("验证码发送成功");
//
//                } else {
//                    L.d("验证码发送失败：" + e.getLocalizedMessage());
//                    RxToast.error("发送失败：" + SMSResultCode.ErrorInfo(e.getErrorCode()));
//                }
//            }
//        });

        BmobSMS.requestSMSCode(phone, getString(R.string.app_name), new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                L.d("requestId:" + integer);
                if (e == null) {
                    L.d("验证码发送成功");
                    RxToast.success(getString(R.string.SmsSendSuccess));

                } else {
                    L.d("验证码发送失败：" + e.getLocalizedMessage());
                    RxToast.error(getString(R.string.SmsSendFail) + SMSResultCode.ErrorInfo(e.getErrorCode()));
                }
            }
        });

    }

    private void verifySmsCodeAndLogin() {

        if (!attemptLogin()) {
            return;
        }

        //验证码登录
        if (!isPassword) {
            if (!verifySmsCode()) return;

            BmobUser.loginBySMSCode(phone, SMSCode, new LogInListener<BmobUser>() {
                @Override
                public void done(BmobUser userInfo, BmobException e) {
                    if (userInfo != null) {
                        L.d("登录成功");
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putBoolean(Constants.AutoLogin, true);
                        edit.apply();

                        UserInfo info = new UserInfo();
                        info.phone = phone;

                        doLoginSuccess(info);
                    } else {
                        L.d("登录失败：" + e.toString());
                        RxToast.error(getString(R.string.loginFail) + SMSResultCode.ErrorInfo(e.getErrorCode()));
                    }
                }
            });

            L.d("phone:" + phone);
            L.d("SMSCode:" + SMSCode);
            L.d("Password:" + password);

        } else {
            //密码登录
            if (!VerifyPassword()) return;
            L.d("phone:" + phone);
            L.d("SMSCode:" + SMSCode);
            L.d("Password:" + password);

            BmobUser.loginByAccount(phone, password, new LogInListener<BmobUser>() {
                @Override
                public void done(BmobUser userInfo, BmobException e) {
                    if (userInfo != null) {
                        L.d("登录成功");
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(Constants.UserPhone, phone);
                        edit.putString(Constants.UserPasswrod, password);
                        edit.putBoolean(Constants.AutoLogin, true);
                        edit.apply();

                        UserInfo info = new UserInfo();
                        info.pwd = password;
                        info.phone = phone;
                        doLoginSuccess(info);
                    } else {
                        L.d("登录失败：" + e.toString());
                        RxToast.error(getString(R.string.loginFail) + SMSResultCode.ErrorInfo(e.getErrorCode()));
                    }
                }
            });
        }
    }


    private void doLoginSuccess(UserInfo info) {
        RxToast.success(getString(R.string.loginSuccess));

        MyApplication.aCache.put("UserInfo", info);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //登录成功
                startActivity(new Intent(LoginActivity.this, ScnnerActivity.class));
                finish();
            }
        }, 500);
    }

    Tencent mTencent;
    SsoHandler ssoHandler;
    IWXAPI iwxapi;


    public void wechar(View v) {
        iwxapi = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_LOGIN_APPID);

        final SendAuth.Req req = new SendAuth.Req();
        req.scope = Constants.SCOPE_USER_INFO;
        req.state = String.valueOf(System.currentTimeMillis());
        iwxapi.sendReq(req);
    }

    public void WeiBo(View v) {
        AuthInfo authInfo = new AuthInfo(this, Constants.WEIBO_LOGIN_KEY,
                Constants.WEIBO_LOGIN_URL, Constants.WEIBO_LOGIN_SECRET);
        ssoHandler = new SsoHandler(this, authInfo);
        ssoHandler.authorize(new WeiBoLogin(subscriber));
    }

    public void QQ(View v) {
        mTencent = Tencent.createInstance(Constants.QQ_Login_ID, getApplicationContext());
        mTencent.login(this, Constants.SCOPE, new QQlogin(subscriber));
    }

    Subscriber<LoginResult> subscriber = new Subscriber<LoginResult>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable throwable) {
            L.d("登录失败");
            RxToast.error(throwable.getMessage());
        }

        @Override
        public void onNext(LoginResult result) {
            L.d("登录成功:" + result.getUserInfo().getNickname());
            L.d("登录成功:" + result.getUserInfo().getHeadImageUrl());
            L.d("登录成功:" + result.getUserInfo().getHeadImageUrlLarge());
            L.d("登录成功:" + result.getUserInfo().getOpenId());
            L.d("登录成功:" + result.getUserInfo().getSex());

            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean(Constants.AutoLogin, true);
            edit.apply();

            UserInfo info = new UserInfo();
            info.sex = result.getUserInfo().getSex() - 1;
            info.name = result.getUserInfo().getNickname();
            info.heardImgUrl = result.getUserInfo().getHeadImageUrl();

            doLoginSuccess(info);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.handleResultData(data, new QQlogin(subscriber));
        if (ssoHandler != null)
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        if (iwxapi != null)
            iwxapi.handleIntent(data, new WeiXinLogin(subscriber));
    }

    @Override
    protected void onDestroy() {
        recycle();
        super.onDestroy();
    }

    //释放登录资源
    private void recycle() {
        if (mTencent != null) {
            mTencent.releaseResource();
            mTencent = null;
        }
        if (ssoHandler != null)
            ssoHandler = null;
        if (iwxapi != null) {
            iwxapi.detach();
            iwxapi = null;
        }
        subscriber = null;
    }


    //切换登录方式
    public void loginmode(View v) {
        if (!isPassword) {
            isPassword = true;
            Login2Code.setVisibility(View.GONE);
            login2Password.setVisibility(View.VISIBLE);
        } else {
            isPassword = false;
            Login2Code.setVisibility(View.VISIBLE);
            login2Password.setVisibility(View.GONE);
        }
    }

    //切换到注册界面
    public void register(View v) {
        Pair[] pairs = new Pair[]{new Pair<>(mEmailView, Constants.UserPhone),
                new Pair<>(mPasswordView, Constants.UserPasswrod),
                new Pair<>(code, "code"),
                new Pair<>(sendCode, "sendCode"),
                new Pair<>(findViewById(R.id.title), "register"),
                new Pair<>(findViewById(R.id.email_sign_in_button), "linear")};

        transitionAnimation(this, RegisterActivity.class, pairs);
    }

}

