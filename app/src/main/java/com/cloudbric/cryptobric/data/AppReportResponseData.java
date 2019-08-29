package com.cloudbric.cryptobric.data;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class AppReportResponseData {
    private boolean result;
    @SerializedName("result_info") @Expose
    public ResultInfo resultInfo;

    @Data
    public static class ResultInfo {
        @SerializedName("contribute_result") @Expose
        private List<contributeResult> contributeResult;

        @Data
        public static class contributeResult {

            private String uuid;
            @SerializedName("contributed_apps") @Expose
            private String contributedApps;

        }

    }

}
