package com.cloudbric.cryptobric.data;

import lombok.Data;

@Data
public class VersionData {

    private String result;
    private DeviceAppInfo deviceAppInfo;   //app

    /*
    * app정보
    *
    * */
    @Data
    public static class DeviceAppInfo {
        private String installStatus;
        private String latestVersion;
        private String latestReleaseDate;
    }


}