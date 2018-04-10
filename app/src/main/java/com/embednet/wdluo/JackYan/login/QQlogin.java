package com.embednet.wdluo.JackYan.login;

import com.embednet.wdluo.JackYan.Constants;
import com.embednet.wdluo.JackYan.module.result.BaseToken;
import com.embednet.wdluo.JackYan.module.result.LoginResult;
import com.embednet.wdluo.JackYan.module.result.QQToken;
import com.embednet.wdluo.JackYan.module.result.QQUser;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

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
public class QQlogin implements IUiListener {

    private Subscriber<LoginResult> subscriber;

    private String buildUserInfoUrl(BaseToken token, String base) {
        return base
                + "?access_token="
                + token.getAccessToken()
                + "&oauth_consumer_key="
                + Constants.QQ_Login_ID
                + "&openid="
                + token.getOpenid();
    }


    public QQlogin(Subscriber<LoginResult> subscriber) {
        this.subscriber = subscriber;
    }

    private void fetchUserInfo(final Object o, Subscriber<LoginResult> subscriber) {

        Observable.create(new Observable.OnSubscribe<LoginResult>() {
            @Override
            public void call(Subscriber<? super LoginResult> subscriber) {
                try {
                    QQToken token = QQToken.parse((JSONObject) o);

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(buildUserInfoUrl(token, Constants.URL)).build();

                    try {
                        Response response = client.newCall(request).execute();
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        QQUser user = QQUser.parse(token.getOpenid(), jsonObject);
                        subscriber.onNext(new LoginResult(token, user));
                    } catch (IOException | JSONException e) {
                        subscriber.onError(e);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }


    @Override
    public void onComplete(Object o) {
        fetchUserInfo(o, subscriber);
    }

    @Override
    public void onError(UiError uiError) {
        subscriber.onError(new Throwable(uiError.errorMessage));
    }

    @Override
    public void onCancel() {
        subscriber.onError(new Throwable("登录关闭"));
    }



}
