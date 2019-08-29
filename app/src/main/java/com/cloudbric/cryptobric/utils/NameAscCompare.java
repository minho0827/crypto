package com.cloudbric.cryptobric.utils;

import com.cloudbric.cryptobric.data.LocalAppData;

import java.util.Comparator;

public class NameAscCompare  implements Comparator<LocalAppData> {

    @Override
    public int compare(LocalAppData o1, LocalAppData o2) {
        return o1.getPackageInfo().applicationInfo.name.compareTo(o2.getPackageInfo().applicationInfo.name);

    }
}
