package com.embednet.wdluo.bleplatformsdkdemo.module;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/23
 */
public class SMSResultCode {


    public static String ErrorInfo(int errorCode) {
        String errorInfo = "";
        switch (errorCode) {
            case 101:
                errorInfo = "账号或者密码错误";
                break;
            case 109:
                errorInfo = "登录信息不正确";
                break;
            case 207:
                errorInfo = "验证码错误";
                break;
            case 202:
                errorInfo = "用户名已经存在";
                break;
            case 209:
                errorInfo = "该手机号码已经存在";
                break;
            case 10010:
                errorInfo = "该手机号发送短信达到限制";
                break;
            case 10011:
                errorInfo = "该账户无可用的发送短信条数";
                break;
            case 10012:
                errorInfo = "身份信息必须审核通过才能使用该功能";
                break;
            case 10013:
                errorInfo = "非法短信内容";
                break;
            default:
                errorInfo = "出现异常请重试";
                break;
        }

        return errorInfo;
    }
}
