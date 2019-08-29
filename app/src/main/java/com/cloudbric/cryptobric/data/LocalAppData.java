package com.cloudbric.cryptobric.data;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LocalAppData extends ApplicationInfo {

    public String virusInfo;
    public String scanResult;
    public boolean isSelected;
    public String label;
    public PackageInfo packageInfo;
    public static ApplicationInfo applicationInfo;


    public LocalAppData(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

}
