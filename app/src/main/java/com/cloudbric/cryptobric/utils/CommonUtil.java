package com.cloudbric.cryptobric.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.cloudbric.cryptobric.BaseApplication;
import com.cloudbric.cryptobric.exceptions.NoPreferenceValueException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 공용적으로 사용되는 Class
 * @author 유민호
 * @since 2019.06.24
 *
 * 1.  getAppHashKey           :: App Hash Key 추출
 * 2.  isNavigationBar         :: 네비게이터가 존재하는 스마트폰인지 확인
 * 3.  getPhoneNumber          :: 단말의 전화번호 확인
 * 4.  getCurrentVersion       :: 현재 디바이스의 App 버전을 확인
 * 5.  getTextHexColor         :: int 값의 color를 16진수 hex값 형태로 반환 (ButtonSelectorListener)
 * 6.  isAvailableTouch        :: 터치한 지점이 해당 뷰의 유효한 지점인지 체크 후 리턴 (전체 화면 내에서의 비교 || ButtonSelectorListener)
 * 7.  isNetworkConnected      :: 현재 네트워크가 연결되어 있는지 확인
 * 8.  isEmail                 :: String값이 이메일 형식인지 체크
 * 9.  isStringNullCheck       :: String이 공백 포함 체크
 * 10. getTelephonyManagerInfo :: 디바이스의 데이터망에 대한 국가 코드
 * 11. getTimeZoneID           :: 디바이스의 시간대에 맞게 날짜시각을 받아오기 위한 디바이스의 TimeZone 확인
 * 12. getDecimalFormat        :: Double 변수의 소수점 뒷자리가 0이면 잘라주는 Format (소수점 5자리까지)
 * 13. getDecimalFormatPrice   :: Double 변수의 숫자를 금액표기를 위한 3단위로 콤마
 * 14. isStringDouble          :: Double형인지 아닌지 구분 (실수)
 */

public class CommonUtil {

    /**
     * Static String variable
     **************************************************************************************************************************************/
    private static final String PLUS_82 = "+82";
    private static final String PERCENT_06X = "%06X";
    private static final String SHA = "SHA";
    private static final String getDecimalFormat = "#.##################";
    private static final String getDecimalFormatPrice = "###,###.##";

    /**
     * 1. App Hash Key 추출
     */
    public static String getAppHashKey() {
        String hashKey = Constant.COMMON_NULL;

        try {
            PackageInfo info = BaseApplication.getContext().getPackageManager().getPackageInfo(BaseApplication.getContext().getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance(SHA);
                md.update(signature.toByteArray());
                hashKey = new String(Base64.encode(md.digest(), 0));
            }
        } catch (Exception e) {
            LogUtil.e("name not found");
        }
        LogUtil.d("debug hash key = [" + hashKey + "]");

        return hashKey;
    }

    /**
     * 2. 네비게이터가 존재하는 스마트폰인지 확인
     */
    public static boolean isNavigationBar() {
        boolean hasMenuKey = ViewConfiguration.get(BaseApplication.getContext()).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        return !hasMenuKey && !hasBackKey;
    }

    /**
     * 3. 단말의 전화번호 확인
     *    --> 앞자리가 +82로 시작하는 번호는 +82를 제거하고 0으로 대체함.
     */
    @SuppressLint("HardwareIds")
    public static String getPhoneNumber(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String number = telephonyManager.getLine1Number();

            if(number != null)
                if(number.startsWith(PLUS_82))
                    number = number.replace(PLUS_82, Constant.ZERO_1);
                else
                    number = Constant.COMMON_NULL;

            return number;
        } catch (SecurityException e) {
            e.printStackTrace();

            return null;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * 4. 현재 디바이스의 App 버전을 확인
     */
    public static String getCurrentVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(); return null;
        } catch (Exception e) {
            e.printStackTrace(); return null;
        }
    }

    /**
     * 5. int 값의 color를 16진수 hex값 형태로 반환 (ButtonSelectorListener)
     */
    public static String getTextHexColor(int colorValue) {
        return String.format(PERCENT_06X, (0xFFFFFF & colorValue));
    }

    /**
     * 6. 터치한 지점이 해당 뷰의 유효한 지점인지 체크 후 리턴 (전체 화면 내에서의 비교 || ButtonSelectorListener)
     */
    public static boolean isAvailableTouch(View v, MotionEvent event) {
        Rect r = new Rect();
        v.getGlobalVisibleRect(r);

        return 0.0f <= event.getX() && event.getX() <= (r.right - r.left) && 0.0f <= event.getY() && event.getY() <= (r.bottom - r.top);
    }

    /**
     * 7. 현재 네트워크가 연결되어 있는지 확인
     */
    public static boolean isNetworkConnected(Context context) {
        boolean isConnected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null) {
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnectedOrConnecting()) {
                isConnected = true;
            } else if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnectedOrConnecting()) {
                isConnected = true;
            }
        }

        return isConnected;
    }

    /**
     * 8. String값이 이메일 형식인지 체크
     */
    public static boolean isEmail(String email) {
        return email != null && Pattern.matches("[\\w~\\-.]+@[\\w~\\-]+(\\.[\\w~\\-]+)+", email.trim());
    }

    /**
     * 9. String이 공백 포함 체크
     */
    public static boolean isStringNullSpaceCheck(String spaceCheck) {
        for(int i = 0 ; i < spaceCheck.length() ; i++)
        {
            if(spaceCheck.charAt(i) == ' ')
                return true;
        }
        return false;
    }

    /**
     * 10. 디바이스의 데이터망에 대한 국가 코드
     */
    public static String getTelephonyManagerInfo(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkCountryIso()) {
            case Constant.KOREAN_M:
            case Constant.KOREAN_C:
            case Constant.ENGLISH_M:
            case Constant.ENGLISH_C:
                return telephonyManager.getNetworkCountryIso().toUpperCase();

            default:
                return Constant.OTHER_COUNTRY;
        }
    }

    /**
     * 11. 디바이스의 시간대에 맞게 날짜시각을 받아오기 위한 디바이스의 TimeZone 확인
     */
    public static String getTimeZoneID() {
        switch (TimeZone.getDefault().getID()) {
            case Constant.C_Korea:
                return "KST";

            case Constant.C_Japan_1:
            case Constant.C_Japan_2:
                return "JST";

            default:
                return "EST";
        }
    }

    /**
     * 12. Double 변수의 소수점 뒷자리가 0이면 잘라주는 Format (소수점 5자리까지)
     */
    public static String getDecimalFormat(Double data) {
        DecimalFormat decimalFormat = new DecimalFormat(getDecimalFormat);
        return decimalFormat.format(data);
    }

    /**
     * 13. Double 변수의 숫자를 금액표기를 위한 3단위로 콤마
     * * 일본, 미국에 대하여 시세값에 대한 소수점 처리 추가*
     */
    public static String getDecimalFormatPrice(Double data) {
        DecimalFormat decimalFormat = new DecimalFormat(getDecimalFormatPrice);
        return decimalFormat.format(data);
    }

    /**
     * 14. Double형인지 아닌지 구분
     */
    public static boolean isStringDouble(String data) {
        try {
            return Pattern.matches("^[+-]?\\d*(\\.?\\d*)$", data);
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }




    public static void hideProgress(CbProgressBar progressBar) {
        if (progressBar != null) {
            progressBar.hide();
        }
    }


    public static HashMap<String, String[]> getScanResultsInPreference(Context context, String key) throws NoPreferenceValueException {
        String jsonStr = PrefsUtils.getString(context, key, "");

        Log.d("CommonUtil", "getScanResultsInPreference: " + jsonStr);

        if (StringUtils.isEmpty(jsonStr)) {
            throw new NoPreferenceValueException("No preference value found with the key.");
        }

//        return new Gson().fromJson(jsonStr, new TypeToken<t>(){}.getType());
        return new Gson().fromJson(jsonStr, new TypeToken<HashMap<String, String[]>>(){}.getType());
    }



    public static void setScanResultsToPreference(Context context, HashMap<String, String[]> scannedResults, String key) {
        String jsonStr = new Gson().toJson(scannedResults);
        PrefsUtils.setString(context, key, jsonStr);
    }



    public static void removeResultInPreference(Context context, String preferenceKey, String packageName) {
        HashMap<String, String[]> mapInpreference = null;

        try {
            mapInpreference = CommonUtil.getScanResultsInPreference(context, preferenceKey);
        } catch (NoPreferenceValueException e) {
            e.printStackTrace();
        }

        if (mapInpreference != null && !mapInpreference.isEmpty()) {
            if (mapInpreference.get(packageName) != null) {
                mapInpreference.remove(packageName);
            }
        }

        setScanResultsToPreference(context, mapInpreference, preferenceKey);
    }

    public static void updateCryptoScanDetectedCount(Context context) {
        HashMap<String, String[]> scannedResultsAll = null;

        try {
            scannedResultsAll = CommonUtil.getScanResultsInPreference(context, Constant.PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_ALL);
        } catch (NoPreferenceValueException e) {
            e.printStackTrace();
        }


        if (scannedResultsAll == null || scannedResultsAll.isEmpty()) {
            return;
        }

        int detectedCount = 0;

        List<PackageInfo> pkgAppsList = context.getPackageManager().getInstalledPackages(PackageManager.PERMISSION_GRANTED);
        for (PackageInfo pack : pkgAppsList) {


            String[] infoArray = scannedResultsAll.get(pack.packageName);
            if (infoArray != null) {
                int order = 0;

                if ("DETECTED".equals(infoArray[1])) {
                    detectedCount++;
                }
            }
        }

        // cryptoscan 검출 count 저장
        ScanDetected.setScanDetectedCount(context, detectedCount);
    }


    public static String GetDevicesUUID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    public static void updateAllScanDetectedCount(Context context) {
        HashMap<String, String[]> scannedResultsAll = null;

        try {
            scannedResultsAll = CommonUtil.getScanResultsInPreference(context, Constant.PREF_KEY_ALL_SCANNED_VIRUS_RESULT_ALL);
        } catch (NoPreferenceValueException e) {
            e.printStackTrace();
        }


        if (scannedResultsAll == null || scannedResultsAll.isEmpty()) {
            return;
        }

        int detectedCount = 0;

        List<PackageInfo> pkgAppsList = context.getPackageManager().getInstalledPackages(PackageManager.PERMISSION_GRANTED);
        for (PackageInfo pack : pkgAppsList) {


            String[] infoArray = scannedResultsAll.get(pack.packageName);
            if (infoArray != null) {
                int order = 0;

                if ("DETECTED".equals(infoArray[1])) {
                    detectedCount++;
                }
            }
        }

        // cryptoscan 검출 count 저장
        ScanDetected.setAllScanDetectedCount(context, detectedCount);
    }
}
