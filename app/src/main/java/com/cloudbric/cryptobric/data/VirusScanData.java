package com.cloudbric.cryptobric.data;


import android.content.pm.PackageInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class VirusScanData {

    private String packageName;
    public String virusInfo;
    public String appName;
    public String companyName;
    public String scanResult;
    public boolean isSelected;
    public PackageInfo packageInfo;
    public int order;

}
