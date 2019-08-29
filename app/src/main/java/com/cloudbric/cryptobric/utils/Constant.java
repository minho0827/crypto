package com.cloudbric.cryptobric.utils;

public class Constant {

    /**
     * DEBUG_MODE :: 통신 URL을 확실히 확인하세요.
     ************************************************************************************************************************************************/
    public static final boolean DEBUG_MODE = false;


    public static final String REQ_IP_STACK_IP_COUNTRY_SCAN = "http://api.ipstack.com/";                        // IP -> Country ISO Code
    public static final String SERVER_ADDR = "https://api-labs.cloudbric.com";

    public static final int ALL_SCAN_LIST = 0;
    public static final int CRYPTO_SCAN_LIST = 1;

    /**
     * 스캔type 키값
     ************************************************************************************************************************************************/
    public static final int TYPE_ALL_SCAN = 1;                                                      // 전체스캔
    public static final int TYPE_TARGET_SCAN = 2;                                                   // 선택한 어플리케이션만 스캔

    /**
     * 자주 사용되는 String
     ************************************************************************************************************************************************/
    public static final String COMMON_NULL = "";                                               // null
    public static final String ZERO_1 = "0";                                              // [0]

    /**
     * 알파벳
     ************************************************************************************************************************************************/
    public static final String Y = "Y";                                              // YES
    public static final String N = "N";                                              // No
    public static final String J = "J";                                              // Japan
    public static final String K = "ko";                                              // Korea
    public static final String E = "en";                                              // English


    public static final String INTENT_PUSH_TRANSFER = "Push_Transfer";                                  // Push Transfer


    /**
     * REQUSET_KEY - Tomcat Server Header
     ************************************************************************************************************************************************/

    public static final String RK_ACCESS_KEY = "access_key";                                        // IpStack Api access_key
    public static final String RK_IPSTACK_KEY = "9cf958a2e78c77919bc508620692719f";
    public static final String RK_FORMAT = "format";
    public static final String RK_PLATFORM = "platform";                                            // android,ios
    public static final String RK_APP_ID = "app_id";                                                // UUID
    public static final String RK_LANGUAGE = "lang";                                                // 언어
    public static final String RK_CURRENT_VERSION = "current_version";                              // 현재버전
    public static final String RK_API_KEY = "zzg0cockog4g0sk4kgcc44ow0go40sw88wkkg8ks";             // 서버 요청시 필요한 apikey
    public static final boolean RK_SUCCESS = true;                                                  // 서버 통신 성공
    public static final String RK_INSPECT_TYPE_ALL = "all";                                         //스캔 결과 타입
    public static final String RK_INSPECT_TYPE_CRYPTO = "crypto";                                   //스캔 결과 타입
    public static final String RK_SCAN_STATUS_SCAN_ERROR = "scan_error";                            //스캔 결과 타입 scan_error
    public static final String RK_SCAN_STATUS_SCAN_SUCCESS = "scan_success";                            //스캔 결과 타입 scan_error
    public static final String RK_SCAN_STATUS_SCAN_SERVER_DOWN = "scan_server_down";                            //스캔 결과 타입 scan_error

    /**
     * PREFERENCE KEY
     ************************************************************************************************************************************************/
    public static final String PREF_NAME_KEY = "CLOUDBRIC";                                         // Preference Key

    public static final String PREF_KEY_USER_SAVE_ID = "userSaveID";                                     // 유저 이메일 저장
    public static final String PREF_KEY_USER_UUID = "userUUID";                                       // 유저 UUID
    public static final String PREF_KEY_USER_LANGUAGE = "PREF_KEY_USER_LANGUAGE";                                   // 유저 언어 설정
    public static final String PREF_KEY_USERCONNETCOUNTRYNUMBER = "userConnetCountryNumber";                        // 유저가 접속되는 디바이스 국가 번호
    public static final String PREF_KEY_USER_APPVERSION = "userAppVersion";                                 // 유저 AppVersion
    public static final String PREF_KEY_USERCONNETCOUNTRYISO = "PREF_KEY_USERCONNETCOUNTRYISO";                           // 유저가 접속되는 디바이스 국가
    public static final String PREF_KEY_USER_NOTI_BOOLEAN = "userNotiChannel";                                // 유저 알람 채널 여부
    public static final String PREF_KEY_USER_LOGIN_SESSION = "userLoginSession";                               // 유저 로그인 세션
    public static final String PREF_KEY_USER_FIREBASE_TOKEN = "PREF_KEY_USER_FIREBASE_TOKEN";                              // 유저 FCM Token
    public static final String PREF_KEY_USER_IPSATCK_KEY = "userIpStackKey";                                 // 유저 IpStack Key
    public static final String PREF_KEY_USER_LAST_SCAN_DATE = "userLastScanDate";                            // 유저 마지막으로 스캔한 날짜
    public static final String PREF_KEY_USER_AGREE_YN = "PREF_KEY_USER_AGREE_YN";                            //
    public static final String PREF_KEY_USER_ALLOW_YN = "PREF_KEY_USER_ALLOW_YN";                            //
    public static final String PREF_KEY_INFOMATION_SKIP_YN = "PREF_KEY_INFOMATION_SKIP_YN";                  //인포메이션 skip여부
    public static final String PREF_KEY_EMAIL_ADDRESS = "PREF_KEY_EMAIL_ADDRESS";                  //인포메이션 skip여부
    public static final String PREF_KEY_CRYPTO_SCAN_PAID_ALERT = "PREF_KEY_CRYPTO_SCAN_PAID_ALERT";
    public static final String PREF_KEY_CLB_PAID_YN = "PREF_KEY_CLB_PAID_YN";
    public static final String PREF_KEY_WALLET_ADDRESS = "PREF_KEY_WALLET_ADDRESS";                  //인포메이션 skip여부
    public static final String PREF_KEY_DEVICE_UUID = "PREF_KEY_DEVICE_UUID";                  //uuid

    //crypto key
    public static final String PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_DETECTED = "PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_DETECTED";
    public static final String PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_ALL = "PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_ALL";
    public static final String PREF_KEY_LAST_SCANNED_TIME = "PREF_KEY_LAST_SCANNED_TIME";
    public static final String PREF_KEY_LAST_ALL_SCANNED_TIME = "PREF_KEY_LAST_ALL_SCANNED_TIME";


    public static final String PREF_KEY_SCAN_DETECTED_COUNT = "PREF_KEY_SCAN_DETECTED_COUNT";
    public static final String PREF_KEY_ALL_SCAN_DETECTED_COUNT = "PREF_KEY_ALL_SCAN_DETECTED_COUNT";


    //all key
    public static final String PREF_KEY_ALL_SCANNED_VIRUS_RESULT_DETECTED = "PREF_KEY_ALL_SCANNED_VIRUS_RESULT_DETECTED";
    public static final String PREF_KEY_ALL_SCANNED_VIRUS_RESULT_ALL = "PREF_KEY_ALL_SCANNED_VIRUS_RESULT_ALL";
//    public static final String PREF_KEY_LAST_ALL_SCANNED_TIME = "PREF_KEY_LAST_ALL_SCANNED_TIME";


    /**
     * 디바이스 언어 및 국가코드 및 TimeZone
     ************************************************************************************************************************************************/
    public static final String OTHER_COUNTRY = "ETC";                                            // 서비스 지역 외 국가
    public static final String C_Korea = "Asia/Seoul";                                     // TimeZone Korea
    public static final String C_Japan_1 = "Asia/Tokyo";                                     // TimeZone Japan
    public static final String C_Japan_2 = "Japan";                                          // TimeZone Japan
    public static final String KOREAN_S = "ko";                                             // Korean Language
    public static final String KOREAN_M = "kr";                                             // Korean CountryCodeIso
    public static final String KOREAN_C = "KR";                                             // Korean oDoo DB
    public static final String KOREAN_L = "korean";                                         // Korean AcceptTerms
    public static final String ENGLISH_S = "en";                                             // English Language
    public static final String ENGLISH_M = "us";                                             // English CountryCodeIso
    public static final String ENGLISH_C = "US";                                             // English oDoo DB
    public static final String ENGLISH_L = "english";                                        // English AcceptTerms
    public static final String JAPANES_S = "ja";                                             // Japan Language
    public static final String JAPANES_M = "jp";                                             // Japan CountryCodeIso
    public static final String JAPANES_C = "JP";                                             // Japan oDoo DB
    public static final String JAPANES_L = "japanese";                                       // Japan AcceptTerms

    public static final String MEMBER_JOIN_USER_TYPE_OS = "android";                                        // Member OS Type

}
