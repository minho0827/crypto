package com.cloudbric.cryptobric.data;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class UsertResponseData {

    private boolean result;
    @SerializedName("result_info")
    @Expose
    public ResultInfo resultInfo;

    @Data
    public static class ResultInfo {


        String value;
        @SerializedName("install_status")
        @Expose
        private String installStatus;
        @SerializedName("latest_version")
        @Expose
        private String latestVersion;
        @SerializedName("latest_release_date")
        @Expose
        private String latestReleaseDate;
        @SerializedName("user_email")
        @Expose
        private String userEmail;
        @SerializedName("user_wallet_address")
        @Expose

        private String userWalletAddress;
        private String uuid;
        @SerializedName("registration_id")
        @Expose
        private String registrationId;                                                          //firebase에서 받은 registration id
        private String model;                                                                   //모델
        private String platform;                                                                //플랫폼 ex) Android, iOS
        private String version;                                                                 //앱 버전
        private String manufacturer;
        private String serial;
        @SerializedName("country_code")
        @Expose
        private String countryCode;
        @SerializedName("inspect_type")
        @Expose
        private String inspectType;                                                             //스캔 타
        private String status;
        @SerializedName("app_count")
        @Expose
        private int appCount;
        @SerializedName("scan_date")
        @Expose
        private String scanDate;


    }

}
