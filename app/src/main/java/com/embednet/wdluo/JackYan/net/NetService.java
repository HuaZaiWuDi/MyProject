package com.embednet.wdluo.JackYan.net;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by MacBook on 18/5/26.
 */

public interface NetService {

    String BASE_URL = "ttp://www.vvjoin.com:8080";


//    http://www.vvjoin.com:8080/CRMShop/aismoke_gainUserId.action

    //    @FormUrlEncoded
    @POST("CRMShop/aismoke_gainUserId.action")
    Observable<String> getUserId(@Body RequestBody body);


//    http://www.vvjoin.com:8080/CRMShop/aismoke_gainAppConfig.action

    @POST("CRMShop/aismoke_gainAppConfig.action")
    Observable<String> gainAppConfig(@Body RequestBody body);


//    http://www.vvjoin.com:8080/CRMShop/aismoke_bindDevice.action

    @POST("CRMShop/aismoke_bindDevice.action")
    Observable<String> bindDevice(@Body RequestBody body);

//    http://www.vvjoin.com:8080/CRMShop/aismoke_refreshHistory.action

    @POST("CRMShop/aismoke_refreshHistory.action")
    Observable<String> refreshHistory(@Body RequestBody body);

    //    http://www.vvjoin.com:8080/CRMShop/aismoke_gainFirmwareConfig.action
    @POST("CRMShop/aismoke_gainFirmwareConfig.action")
    Observable<String> gainFirmwareConfig(@Body RequestBody body);

//    http://www.vvjoin.com:8080/CRMShop/aismoke_gainUserInfo.action

    @POST("CRMShop/aismoke_gainUserInfo.action")
    Observable<String> gainUserInfo(@Body RequestBody body);

//    http://www.vvjoin.com:8080/CRMShop/aismoke_updateUserInfo.action

    @POST("CRMShop/aismoke_updateUserInfo.action")
    Observable<String> updateUserInfo(@Body RequestBody body);


//    http://www.vvjoin.com:8080/CRMShop/aismoke_updateFamilyUserInfo.action

    @POST("CRMShop/aismoke_updateFamilyUserInfo.action")
    Observable<String> updateFamilyUserInfo(@Body RequestBody body);


}
