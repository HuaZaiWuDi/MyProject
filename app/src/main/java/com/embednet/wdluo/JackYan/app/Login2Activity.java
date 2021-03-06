package com.embednet.wdluo.JackYan.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.embednet.wdluo.JackYan.BuildConfig;
import com.embednet.wdluo.JackYan.Constants;
import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.login.QQlogin;
import com.embednet.wdluo.JackYan.login.WeiBoLogin;
import com.embednet.wdluo.JackYan.login.WeiXinLogin;
import com.embednet.wdluo.JackYan.module.result.LoginResult;
import com.embednet.wdluo.JackYan.util.L;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import lab.dxythch.com.netlib.utils.RxSubscriber;
import laboratory.dxy.jack.com.jackupdate.ui.RxToast;
import laboratory.dxy.jack.com.jackupdate.util.StatusBarUtils;

public class Login2Activity extends BaseActivity {
    private View qq, wechar, weibo, linear, text_login, text_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        StatusBarUtils.from(this).setHindStatusBar(true).process();
        Glide.with(this)
                .asDrawable()
                .load(R.drawable.login_bg)
                .into((ImageView) findViewById(R.id.img_bg));


        wechar = findViewById(R.id.email_sign_in_button);
        qq = findViewById(R.id.QQ);
        weibo = findViewById(R.id.WeiBo);
        linear = findViewById(R.id.linear);
        text_login = findViewById(R.id.text_login);
        text_register = findViewById(R.id.text_register);

    }


    public void phoneOrEmail(View v) {

        Pair[] pairs = new Pair[]{new Pair<>(qq, Constants.UserName),
                new Pair<>(weibo, Constants.UserSex),
                new Pair<>(linear, "linear"),
                new Pair<>(text_login, "login"),
                new Pair<>(wechar, Constants.UserImgHeard)};

        transitionAnimation(this, LoginActivity.class, pairs);
    }

    public void register(View v) {
        Pair[] pairs = new Pair[]{new Pair<>(qq, Constants.UserName),
                new Pair<>(linear, "linear"),
                new Pair<>(text_register, "register")};

        transitionAnimation(this, RegisterActivity.class, pairs);
    }


    Tencent mTencent;
    SsoHandler ssoHandler;
    IWXAPI iwxapi;


    public void wechar(View v) {
        iwxapi = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_LOGIN_APPID);
        if (!iwxapi.isWXAppInstalled()) return;
        iwxapi.registerApp(Constants.WEIXIN_LOGIN_APPID);
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

    //非死不可
    public void facebook(View v) {
        if (BuildConfig.DEBUG) {
            startActivity(new Intent(Login2Activity.this, Main2Activity.class));
            finish();
            return;
        }
    }

    RxSubscriber<LoginResult> subscriber = new RxSubscriber<LoginResult>() {

        @Override
        public void onError(Throwable throwable) {
            L.d(throwable.getMessage());
            RxToast.error(throwable.getMessage());
        }

        @Override
        protected void _onNext(LoginResult loginResult) {
            L.d("登录成功：" + loginResult.toString());
            loginSuccess(loginResult);
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        Tencent.handleResultData(intent, new QQlogin(subscriber));
        if (ssoHandler != null)
            ssoHandler.authorizeCallBack(0, 0, intent);
        if (iwxapi != null)
            iwxapi.handleIntent(intent, new WeiXinLogin(subscriber));
    }


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

}
