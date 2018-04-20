package com.embednet.wdluo.JackYan;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/19
 */
public class Constants {

    public static final String UserSex = "UserSex";
    public static final String UserImgHeard = "UserImgHeard";
    public static final String UserName = "UserName";
    public static final String UserPhone = "UserPhone";
    public static final String UserPasswrod = "UserPasswrod";
    public static final String AutoLogin = "AutoLogin";
    public static final String isLogin = "isLogin";
    public static final String isBind = "isBind";
    public static final String UserInfo = "UserInfo";
    public static final String UserId = "UserId";


    public static final String PACKAGE_NAME = "com.embednet.wdluo.jackYan";


    ///////////////////////////////////////////////////////////////////////////
    // 第三方登录信息
    ///////////////////////////////////////////////////////////////////////////
    public static String QQ_Login_ID = "1106565969";
    public static String WEIBO_LOGIN_KEY = "2436112383";
    public static String WEIBO_LOGIN_SECRET = "c4a9e96ad23df29702e70c1de203824b";
    public static String WEIBO_LOGIN_URL = "http://app-manager.dxycloud.com/admin/productmanage";

    public static String WEIXIN_LOGIN_APPID = "wxc5dcbc4125e117db";
    public static String WEIXIN_LOGIN_SECRT = "8fcec072c14e3a0d5e963657c3fbd789";
    public static String BMOB_APPID = "4d0b207b9731b474694cbfdc2bf5fbd4";

    //QQ
    public static final String SCOPE = "get_simple_userinfo";
    public static final String URL = "https://graph.qq.com/user/get_user_info";

    //微博
    public static final String USER_INFO = "https://api.weibo.com/2/users/show.json";

    //微信
    public static final String SCOPE_USER_INFO = "snsapi_userinfo";
    public static final String SCOPE_BASE = "snsapi_base";
    public static final String BASE_URL = "https://api.weixin.qq.com/sns/";

    //bugly
    public static String BUGLY_APP_ID = "8dfebf15bd";


    public static final String MSM = "com.android.mms";
    public static final String QQ = "com.tencent.mobileqq";
    public static final String QZONE = "com.qzone";
    public static final String WECHAR = "com.tencent.mm";
    public static final String WHATSAPP = "com.whatsapp";
    public static final String WEIBO = "com.sina.weibo";
    public static final String FACEBOOK = "com.facebook.katana";
    public static final String TWITTER = "com.twitter.android";

    ///////////////////////////////////////////////////////////////////////////
    // OTA Config
    ///////////////////////////////////////////////////////////////////////////
    public static final String[] FILE_Extensions = {"bin", "hex", "zip"};       //文件picker的后缀名


    ///////////////////////////////////////////////////////////////////////////
    // 广播
    ///////////////////////////////////////////////////////////////////////////
    public static final String ACTIVE_CONNECT_STATUE = "ACTIVE_CONNECT_STATUE";
    public static final String EXTRA_CONNECT_STATUE = "EXTRA_CONNECT_STATUE";


    ///////////////////////////////////////////////////////////////////////////
    // 经纬度
    ///////////////////////////////////////////////////////////////////////////
    public static double Longitude = 0;
    public static double latitude = 0;
    public static String GPSMode = "gps84";
    public static String merchantNo = "100576";
}
