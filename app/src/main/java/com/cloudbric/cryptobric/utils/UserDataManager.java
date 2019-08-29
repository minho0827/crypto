package com.cloudbric.cryptobric.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cloudbric.cryptobric.data.UserData;

public class UserDataManager {
    private Context context;

    public UserDataManager(Context context) {
        this.context = context;
    }

    public UserData getUserData() {
        SharedPreferences preferences = context.getSharedPreferences(Constant.PREF_NAME_KEY,Context.MODE_PRIVATE);
        String uuid = preferences.getString(Constant.PREF_KEY_USER_UUID,Constant.COMMON_NULL);
        String userLanguage = preferences.getString(Constant.PREF_KEY_USER_LANGUAGE, Constant.COMMON_NULL);
        String userWalletAddress = preferences.getString(Constant.PREF_KEY_WALLET_ADDRESS, Constant.COMMON_NULL);
        String userEmailAddress = preferences.getString(Constant.PREF_KEY_EMAIL_ADDRESS, Constant.COMMON_NULL);
        String userConnetCountryISO = preferences.getString(Constant.PREF_KEY_USERCONNETCOUNTRYISO, Constant.COMMON_NULL);
        String userAppVersion = preferences.getString(Constant.PREF_KEY_USER_APPVERSION, Constant.COMMON_NULL);
        String userNotiChannel = preferences.getString(Constant.PREF_KEY_USER_NOTI_BOOLEAN, Constant.COMMON_NULL);
        String userLoginSession = preferences.getString(Constant.PREF_KEY_USER_LOGIN_SESSION, Constant.COMMON_NULL);
        String userFirebaseToken = preferences.getString(Constant.PREF_KEY_USER_FIREBASE_TOKEN, Constant.COMMON_NULL);
        String userLastScanDate = preferences.getString(Constant.PREF_KEY_USER_LAST_SCAN_DATE, Constant.COMMON_NULL);

        UserData userData = new UserData();
        userData.setUserUUID(uuid);
        userData.setUserLanguage(userLanguage);
        userData.setUserWalletAddress(userWalletAddress);
        userData.setUserEmailAddress(userEmailAddress);
        userData.setUserConnetCountryISO(userConnetCountryISO);
        userData.setUserAppVersion(userAppVersion);
        userData.setUserNotiChannel(userNotiChannel);
        userData.setUserLoginSession(userLoginSession);
        userData.setUserFirebaseToken(userFirebaseToken);
        userData.setUserLastScanDate(userLastScanDate);

        return userData;
    }

    public void setUserData(UserData userData) {
        SharedPreferences preferences = context.getSharedPreferences(Constant.PREF_NAME_KEY,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(Constant.PREF_KEY_USERCONNETCOUNTRYISO,userData.getUserConnetCountryISO());
        editor.putString(Constant.PREF_KEY_USER_LANGUAGE,userData.getUserLanguage());
        editor.putString(Constant.PREF_KEY_WALLET_ADDRESS,userData.getUserWalletAddress());
        editor.putString(Constant.PREF_KEY_EMAIL_ADDRESS,userData.getUserWalletAddress());
//        editor.putString(Constant.PREF_KEY_USERCONNETCOUNTRYISO,userData.getUserConnetCountryISO());
//        editor.putString(Constant.PREF_KEY_USER_APPVERSION,userData.getUserAppVersion());
//        editor.putString(Constant.PREF_KEY_USER_NOTI_BOOLEAN,userData.getUserNotiChannel());
//        editor.putString(Constant.PREF_KEY_USER_LOGIN_SESSION,userData.getUserLoginSession());
//        editor.putString(Constant.PREF_KEY_USER_FIREBASE_TOKEN,userData.getUserFirebaseToken());
//        editor.putString(Constant.PREF_KEY_USER_LAST_SCAN_DATE,userData.getUserLastScanDate());

        editor.apply();
        editor.commit();

    }
}
