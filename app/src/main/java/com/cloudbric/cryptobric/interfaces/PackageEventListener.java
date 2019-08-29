package com.cloudbric.cryptobric.interfaces;

public interface PackageEventListener {
    void registerPackageEvent();
    void unregisterPackageEvent();
    void onPackgeDeleted(String pacakageName);
}