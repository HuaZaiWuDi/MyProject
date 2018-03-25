package com.embednet.wdluo.bleplatformsdkdemo.login;

import com.embednet.wdluo.bleplatformsdkdemo.Constants;
import com.embednet.wdluo.bleplatformsdkdemo.module.result.BaseToken;
import com.embednet.wdluo.bleplatformsdkdemo.module.result.LoginResult;
import com.embednet.wdluo.bleplatformsdkdemo.module.result.WxToken;
import com.embednet.wdluo.bleplatformsdkdemo.module.result.WxUser;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.bmob.v3.okhttp3.OkHttpClient;
import cn.bmob.v3.okhttp3.Request;
import cn.bmob.v3.okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 项目名称：ShareAndLogin
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/27
 */
public class WeiXinLogin implements IWXAPIEventHandler {

    private Subscriber<LoginResult> subscriber;
    private OkHttpClient mClient;

    public WeiXinLogin(Subscriber<LoginResult> subscriber) {
        this.subscriber = subscriber;
        mClient = new OkHttpClient();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp instanceof SendAuth.Resp && baseResp.getType() == 1) {
            SendAuth.Resp resp = (SendAuth.Resp) baseResp;
            L.d("微信登录：" + resp.errCode);
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    getToken(resp.code);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    subscriber.onError(new Throwable("登录关闭"));
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    subscriber.onError(new Throwable("登录失败"));
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    subscriber.onError(new Throwable("登录失败"));
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    subscriber.onError(new Throwable("登录关闭"));
                    break;
                default:
                    subscriber.onError(new Throwable("登录失败"));
            }
        }
    }

    private void getToken(final String code) {
        Observable.create(new Observable.OnSubscribe<WxToken>() {
            @Override
            public void call(Subscriber<? super WxToken> subscriber) {
                Request request = new Request.Builder().url(buildTokenUrl(code)).build();
                try {
                    Response response = mClient.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    WxToken token = WxToken.parse(jsonObject);
                    subscriber.onNext(token);
                } catch (IOException | JSONException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribe(new Action1<WxToken>() {
            @Override
            public void call(WxToken wxToken) {
                fetchUserInfo(wxToken);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                subscriber.onError(throwable);
            }
        });

    }

    private void fetchUserInfo(final WxToken wxToken) {
        Observable.create(new Observable.OnSubscribe<LoginResult>() {
            @Override
            public void call(Subscriber<? super LoginResult> subscriber) {
                Request request = new Request.Builder().url(buildUserInfoUrl(wxToken)).build();
                try {
                    Response response = mClient.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    WxUser user = WxUser.parse(jsonObject);
                    subscriber.onNext(new LoginResult(wxToken, user));
                } catch (IOException | JSONException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    private String buildTokenUrl(String code) {
        return Constants.BASE_URL
                + "oauth2/access_token?appid="
                + Constants.WEIXIN_LOGIN_APPID
                + "&secret="
                + Constants.WEIXIN_LOGIN_SECRT
                + "&code="
                + code
                + "&grant_type=authorization_code";
    }

    private String buildUserInfoUrl(BaseToken token) {
        return Constants.BASE_URL
                + "userinfo?access_token="
                + token.getAccessToken()
                + "&openid="
                + token.getOpenid();
    }

}
