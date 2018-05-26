package com.embednet.wdluo.JackYan.login;

import android.os.Bundle;

import com.embednet.wdluo.JackYan.Constants;
import com.embednet.wdluo.JackYan.module.result.BaseToken;
import com.embednet.wdluo.JackYan.module.result.LoginResult;
import com.embednet.wdluo.JackYan.module.result.WeiboToken;
import com.embednet.wdluo.JackYan.module.result.WeiboUser;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lab.dxythch.com.netlib.utils.RxSubscriber;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 项目名称：ShareAndLogin
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/27
 */
public class WeiBoLogin implements WeiboAuthListener {

    private RxSubscriber<LoginResult> subscriber;

    public WeiBoLogin(RxSubscriber<LoginResult> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onComplete(Bundle bundle) {
        Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
        WeiboToken weiboToken = WeiboToken.parse(accessToken);
        fetchUserInfo(weiboToken, subscriber);
    }

    private void fetchUserInfo(final WeiboToken weiboToken, RxSubscriber<LoginResult> subscriber) {

        Observable.create(new ObservableOnSubscribe<LoginResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoginResult> emitter) throws Exception {
                OkHttpClient client = new OkHttpClient();
                Request request =
                        new Request.Builder().url(buildUserInfoUrl(weiboToken, Constants.USER_INFO)).build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    WeiboUser user = WeiboUser.parse(jsonObject);

                    emitter.onNext(new LoginResult(weiboToken, user,Constants.WEIBO));
                } catch (IOException | JSONException e) {
                    emitter.onError(e);
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
