package com.embednet.wdluo.bleplatformsdkdemo.login;

import android.os.Bundle;

import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.module.result.BaseToken;
import com.embednet.wdluo.bleplatformsdkdemo.module.result.LoginResult;
import com.embednet.wdluo.bleplatformsdkdemo.module.result.WeiboToken;
import com.embednet.wdluo.bleplatformsdkdemo.module.result.WeiboUser;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.bmob.v3.okhttp3.OkHttpClient;
import cn.bmob.v3.okhttp3.Request;
import cn.bmob.v3.okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 项目名称：ShareAndLogin
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/27
 */
public class WeiBoLogin implements WeiboAuthListener {

    private Subscriber<LoginResult> subscriber;

    public WeiBoLogin(Subscriber<LoginResult> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onComplete(Bundle bundle) {
        Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
        WeiboToken weiboToken = WeiboToken.parse(accessToken);
        fetchUserInfo(weiboToken, subscriber);
    }

    private void fetchUserInfo(final WeiboToken weiboToken, Subscriber<LoginResult> subscriber) {

        Observable.create(new Observable.OnSubscribe<LoginResult>() {
            @Override
            public void call(Subscriber<? super LoginResult> subscriber) {
                OkHttpClient client = new OkHttpClient();
                Request request =
                        new Request.Builder().url(buildUserInfoUrl(weiboToken, Constants.USER_INFO)).build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    WeiboUser user = WeiboUser.parse(jsonObject);

                    subscriber.onNext(new LoginResult( weiboToken, user));
                } catch (IOException | JSONException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    private String buildUserInfoUrl(BaseToken token, String baseUrl) {
        return baseUrl + "?access_token=" + token.getAccessToken() + "&uid=" + token.getOpenid();
    }

    @Override
    public void onWeiboException(WeiboException e) {
        subscriber.onError(e);
    }

    @Override
    public void onCancel() {
        subscriber.onError(new Throwable("登录关闭"));
    }

}
