package com.embednet.wdluo.JackYan.module.result;

/**
 * Created by shaohui on 2016/12/3.
 */

public class LoginResult {

    private BaseToken mToken;

    private BaseUser mUserInfo;

    private String phone;
    private String email;
    private String loginType;

    public LoginResult(BaseToken token) {
        mToken = token;
    }

    public LoginResult(BaseToken token, BaseUser userInfo, String loginType) {
        mToken = token;
        mUserInfo = userInfo;
        this.loginType = loginType;
    }

    public String getLoginType() {
        return loginType == null ? "" : loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getPhone() {
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BaseToken getToken() {
        return mToken;
    }

    public void setToken(BaseToken token) {
        mToken = token;
    }

    public BaseUser getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(BaseUser userInfo) {
        mUserInfo = userInfo;
    }


    @Override
    public String toString() {
        return "LoginResult{" +
                "mToken=" + mToken.toString() +
                ", mUserInfo=" + mUserInfo.toString() +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", loginType='" + loginType + '\'' +
                '}';
    }
}
