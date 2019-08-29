package com.cloudbric.cryptobric.network;

import com.cloudbric.cryptobric.data.AppReportResponseData;
import com.cloudbric.cryptobric.data.CryptoData;
import com.cloudbric.cryptobric.data.UsertResponseData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RestService {


    @Headers({"Content-Type: application/json", "X-Cloudbric-Key: zzg0cockog4g0sk4kgcc44ow0go40sw88wkkg8ks"})
    @GET("mobile/version")
    Call<UsertResponseData> getAppversionCheck(@Query("app_id") String appId, @Query("platform") String platform, @Query("current_version") String currentVersion);


    @Headers({"Content-Type: application/json", "X-Cloudbric-Key: zzg0cockog4g0sk4kgcc44ow0go40sw88wkkg8ks"})
    @GET("mobile/about")
    Call<UsertResponseData> getAboutData(@Query("type") String appId, @Query("lang") String lang);


    /* crypto 어플리케이션 제보 */
    @Headers({"Content-Type: application/json", "X-Cloudbric-Key: zzg0cockog4g0sk4kgcc44ow0go40sw88wkkg8ks"})
    @POST("/mobile/contribute_app")
    Call<AppReportResponseData> postApplicationReportList(@Body Map<String, String> param);


    /* 유저정보 insert */
    @Headers({"Content-Type: application/json", "X-Cloudbric-Key: zzg0cockog4g0sk4kgcc44ow0go40sw88wkkg8ks"})
    @PUT("/mobile/user_info")
    Call<UsertResponseData> putUserInfo(@Body Map<String, String> param);

    /* 스캔 결과 insert */
    @Headers({"Content-Type: application/json", "X-Cloudbric-Key: zzg0cockog4g0sk4kgcc44ow0go40sw88wkkg8ks"})
    @POST("/mobile/scan")
    Call<UsertResponseData> postScanResultList(@Body Map<String, String> param);


    /* 유저 device insert */
    @Headers({"Content-Type: application/json", "X-Cloudbric-Key: zzg0cockog4g0sk4kgcc44ow0go40sw88wkkg8ks"})
    @POST("/mobile/device")
    Call<UsertResponseData> postDevceInfo(@Body Map<String, String> param);


    // 검증가능한 cryptoList
    @Headers({"Content-Type: application/json", "X-Cloudbric-Key: zzg0cockog4g0sk4kgcc44ow0go40sw88wkkg8ks"})
    @GET("/mobile/available_apps")
    Call<CryptoData> requeConfirmCryptoList(@QueryMap Map<String, Object> param);


}
