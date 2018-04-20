package com.embednet.wdluo.JackYan.module;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/23
 */
public class UserInfo implements Serializable {

    public String name = "用户";
    public String phone = "";
    public String pwd = "";
    public String heardImgUrl = "";
    public boolean isLogin = false;
    public boolean isbind = false;
    public String userId;
    public int sex = 0;
    public int age = 20;
    public int height = 175;
    public int weight = 60;
    public int stepsTarget = 5000;
    public String merchantNo;
    public String appVersionNo;
    public String deviceSN;
    public String firmwareVersionNo;
    public String email;
    List<DayStepsTab> lists;

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", pwd='" + pwd + '\'' +
                ", heardImgUrl='" + heardImgUrl + '\'' +
                ", isLogin=" + isLogin +
                ", isbind=" + isbind +
                ", userId=" + userId +
                ", sex=" + sex +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", stepsTarget=" + stepsTarget +
                ", merchantNo='" + merchantNo + '\'' +
                ", appVersionNo='" + appVersionNo + '\'' +
                ", deviceSN='" + deviceSN + '\'' +
                ", firmwareVersionNo='" + firmwareVersionNo + '\'' +
                ", email='" + email + '\'' +
                ", lists=" + lists +
                '}';
    }
}
