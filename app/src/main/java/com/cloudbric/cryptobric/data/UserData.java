package com.cloudbric.cryptobric.data;


import lombok.Data;

@Data
public class UserData {

    private String userUUID;                                // 유저 UU 아이디
    private String userHP;                                  // 유저 핸드폰번호
    private String userLanguage;                            // 유저 언어 설정
    private String userWalletAddress;                       // 유저 지갑주소
    private String userEmailAddress;                        // 유저 이메일주소
    private String userConnetCountryISO;                    // 유저 접속되는 디바이스 국가
    private String userConnetCountryNumber;                 // 유저 접속되는 디바이스 국가 번호
    private String userAppVersion;                          // 유저 App Version
    private String userLoginBoolean;                        // 유저 로그인 여부
    private String userNotiChannel;                         // 유저 알람 채널 여부
    private String userLoginSession;                        // 유저 로그인 세션
    private String userFirebaseToken;                       // 유저 FCM Token
    private String userIpStackKey;                          // 유저 IpStack Key
    private String userLastScanDate;                        // 유저 마지막 스캔 날짜
    private String isTermsOfUse;                         //유저 약관동의 여부
}
