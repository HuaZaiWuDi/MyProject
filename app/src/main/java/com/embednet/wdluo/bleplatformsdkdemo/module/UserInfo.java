package com.embednet.wdluo.bleplatformsdkdemo.module;

import cn.bmob.v3.BmobUser;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/23
 */
public class UserInfo extends BmobUser{


    public String name;
    public String pwd;
    public int id;

    public UserInfo(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }


}
