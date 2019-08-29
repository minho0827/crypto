package com.cloudbric.cryptobric.data;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class CryptoData {

    private boolean result;
    @SerializedName("result_info")
    @Expose
    private List<ResultInfo> resultInfo;

    @Data
    public static class ResultInfo {

        @SerializedName("store_type")
        @Expose
        private String storeType;
        private String category;
        @SerializedName("app_name")
        @Expose
        private String appName;
        private String comment;
        private String company;
        @SerializedName("number_of_downloads")
        @Expose
        private String numberOfDownloads;
        private String score;
        @SerializedName("icon_image_url")
        @Expose
        private String iconImageUrl;
        @SerializedName("partnership_status")
        @Expose
        private String partnershipStatus;

    }


}
