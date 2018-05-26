package com.embednet.wdluo.JackYan.login;

import com.embednet.wdluo.JackYan.Constants;
import com.embednet.wdluo.JackYan.module.result.BaseToken;
import com.embednet.wdluo.JackYan.module.result.LoginResult;
import com.embednet.wdluo.JackYan.module.result.WxToken;
import com.embednet.wdluo.JackYan.module.result.WxUser;
import com.embednet.wdluo.JackYan.util.L;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

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
public class WeiXinLogin implements IWXAPIEventHandler {

    private RxSubscriber<LoginResult> subscriber;
    private OkHttpClient mClient;

    public WeiXinLogin(RxSubscriber<LoginResult> subscriber) {
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
        Observable.create(new ObservableOnSubscribe<WxToken>() {
            @Override
            public void subscribe(ObservableEmitter<WxToken> emitter) throws Exception {
                Request request = new Request.Builder().url(buildTokenUrl(code)).build();
                try {
                    Response response = mClient.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    WxToken token = WxToken.parse(jsonObject);
                    emitter.onNext(token);
                } catch (IOException | JSONException e) {
                    emitter.onError(e);
                }
            }
        }).subscribe(new RxSubscriber<WxToken>() {
            @Override
            protected void _onNext(WxToken wxToken) {
                fetchUserInfo(wxToken);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                subscriber.onError(e);
            }
        });

    }

    private void fetchUserInfo(final WxToken wxToken) {
        Observable.create(new ObservableOnSubscribe<LoginResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoginResult> emitter) throws Exception {
                Request request = new Request.Builder().url(buildUserInfoUrl(wxToken)).build();
                try {
                    Response response = mClient.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    WxUser user = WxUser.parse(jsonObject);
                    subscriber.onNext(new LoginResult(wxToken, user, Constants.WECHAR));
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
