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

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lab.dxythch.com.netlib.rx.RxSubscriber;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 项目名称：ShareAndLogin
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/27
 */
public class QQlogin implements IUiListener {

    private RxSubscriber<LoginResult> subscriber;

    private String buildUserInfoUrl(BaseToken token, String base) {
        return base
                + "?access_token="
                + token.getAccessToken()
                + "&oauth_consumer_key="
                + Constants.QQ_Login_ID
                + "&openid="
                + token.getOpenid();
    }


    public QQlogin(RxSubscriber<LoginResult> subscriber) {
        this.subscriber = subscriber;
    }

    private void fetchUserInfo(final Object o, RxSubscriber<LoginResult> subscriber) {

        Observable.create(new ObservableOnSubscribe<LoginResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoginResult> emitter) throws Exception {
                try {
                    QQToken token = QQToken.parse((JSONObject) o);

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(buildUserInfoUrl(token, Constants.URL)).build();

                    try {
                        Response response = client.newCall(request).execute();
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        QQUser user = QQUser.parse(token.getOpenid(), jsonObject);
                        emitter.onNext(new LoginResult(token, user, Constants.QQ));
                    } catch (IOException | JSONException e) {
                        emitter.onError(e);
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
