package com.cloudbric.cryptobric.utils;

import android.content.Context;

public class ScanDetected {

    // -1 : 한번도 스캔 안했을경우

    public static int getScanDetectedCount(Context context) {
        return PrefsUtils.getInt(context, Constant.PREF_KEY_SCAN_DETECTED_COUNT, -1);
    }

    public static void setScanDetectedCount(Context context, int scanDetectedCount) {
        PrefsUtils.setInt(context, Constant.PREF_KEY_SCAN_DETECTED_COUNT, scanDetectedCount);
    }


    public static int getAllScanDetectedCount(Context context) {
        return PrefsUtils.getInt(context, Constant.PREF_KEY_ALL_SCAN_DETECTED_COUNT, 0);
    }

    public static void setAllScanDetectedCount(Context context, int allScanDetectedCount) {
            PrefsUtils.setInt(context, Constant.PREF_KEY_ALL_SCAN_DETECTED_COUNT, allScanDetectedCount);
    }

    //메인에서 바이러스 카운트 세는 함수
    public static int getDetectedCountForMain(Context context) {
        int scanDetectedCount = getScanDetectedCount(context);
        int allScanDetectedCount = getAllScanDetectedCount(context);

        if (scanDetectedCount != -1) {
            //우선순위 1
            return scanDetectedCount;
        } else {
            //우선순위 2
            return allScanDetectedCount;
        }
    }


    public static long getScanDetectedTime(Context context) {
        return PrefsUtils.getLong(context, Constant.PREF_KEY_LAST_SCANNED_TIME, 0);
    }

    public static void setScanDetectedTime(Context context, long scanDetectedTime) {
        PrefsUtils.setLong(context, Constant.PREF_KEY_LAST_SCANNED_TIME, scanDetectedTime);
    }


    public static long getAllScanDetectedTime(Context context) {
        return PrefsUtils.getLong(context, Constant.PREF_KEY_LAST_ALL_SCANNED_TIME, 0);
    }

    public static void setAllScanDetectedTime(Context context, long allScanDetectedTime) {
        PrefsUtils.setLong(context, Constant.PREF_KEY_LAST_ALL_SCANNED_TIME, allScanDetectedTime);
    }

    //crypto 메인에서 바이러스 시간 세는 함수
    public static long getDetectedTimeForMain(Context context) {
        long scanDetectedTime = getScanDetectedTime(context);
//        long allscanDetectedTime = getAllScanDetectedTime(context);

//        if (scanDetectedTime != -1) {
//            //우선순위 1
        return scanDetectedTime;
//        } else {
//            //우선순위 2
//        return allscanDetectedTime;
//        }
    }


    // all 메인에서 바이러스 시간 세는 함수
    public static long getAllDetectedTimeForMain(Context context) {
//        long scanDetectedTime = getScanDetectedTime(context);
        long allscanDetectedTime = getAllScanDetectedTime(context);

//        if (scanDetectedTime != -1) {
//            //우선순위 1
//            return scanDetectedTime;
//        } else {
//            //우선순위 2
        return allscanDetectedTime;
//        }
    }
}
