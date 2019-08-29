package com.cloudbric.cryptobric.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.contextwrapper.MyLanguageChangeWrapper;
import com.cloudbric.cryptobric.data.UserData;
import com.cloudbric.cryptobric.listener.OnSingleClickListener;
import com.cloudbric.cryptobric.network.CommonApiCall;
import com.cloudbric.cryptobric.utils.AlertDialogUtil;
import com.cloudbric.cryptobric.utils.CommonUtil;
import com.cloudbric.cryptobric.utils.Constant;
import com.cloudbric.cryptobric.utils.PermissionUtil;
import com.cloudbric.cryptobric.utils.PrefsUtils;
import com.cloudbric.cryptobric.utils.RequestCodeUtil;
import com.cloudbric.cryptobric.utils.UserDataManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "SplashActivity";
    private UserData mUserData;                                                                                      // 유저 정보
    private AlertDialog dialog;
    AppCompatEditText mEtEmailAddress;
    AppCompatEditText mEtWalletAddress;
    private String mPackageName;

    /**
     * Static String variable
     ************************************************************************************************************************************************/
    private static final String strPATH = "path";                                                                   // 문자 path
    private Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// requestWindowFeature(Window.FEATURE_NO_TITLE);

        statusBarSetColor();
        setContentView(R.layout.activity_splash);

        mContext = this;
        userDataManager = new UserDataManager(mContext);

        HandlerThread ht = new HandlerThread("SplashThread");
        ht.start();

        android.os.Handler h = new android.os.Handler(ht.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                runOnUiThread(() -> {
                    if (!CommonUtil.isNetworkConnected(mContext)) {
                        AlertDialogUtil.showSingleDialog(mContext, "인터넷 연결이 없어 앱을 실행할 수 없습니다.", new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                AlertDialogUtil.dismissDialog();
                                finish();
                            }
                        });

                        return;
                    }

                    uiInit();
                    getAppversionCheck();
                    settingSplashMethod();
                });
            }
        };

        h.sendEmptyMessageDelayed(0, 1000);
    }


    public String getVersionInfo(Context context) {
        String version = "Unknown";
        PackageInfo packageInfo;

        if (context == null) {
            return version;
        }
        try {
            packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getApplicationContext().getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "getVersionInfo :" + e.getMessage());
        }
        return version;
    }


    private void getAppversionCheck() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            mPackageName = packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        String strPackageName = mPackageName;
        String strVesion = getVersionInfo(mContext);
        CommonApiCall.getAppversionCheck(mContext, TAG, strPackageName, Constant.MEMBER_JOIN_USER_TYPE_OS, strVesion);

    }

    private void statusBarSetColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0xFF006CFF);


    }


    public void showMyInfoMationDialog() {

        String strSkipYn = PrefsUtils.getString(SplashActivity.this,
                Constant.PREF_KEY_INFOMATION_SKIP_YN, "");

        if (strSkipYn.equals(Constant.N)) {
            gotoMain();
//        } else if (StringUtils.isEmpty(strEmailAddress) ||
//                StringUtils.isEmpty(strWalletAddress)) {

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper
                    (this, android.R.style.Theme_Holo_Light));
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.layout_alert_infomation_popup, null);

            InputMethodManager imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEtEmailAddress, 0);
            AppCompatTextView tvSkip = v.findViewById(R.id.tv_skip);
            AppCompatTextView tvOk = v.findViewById(R.id.tv_ok);
            mEtEmailAddress = v.findViewById(R.id.et_email_address);
            mEtWalletAddress = v.findViewById(R.id.et_wallet_address);

            mEtEmailAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    mEtWalletAddress.requestFocus();
                    return true;
                }
            });

            mEtWalletAddress.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_ENTER)) {


                        imm.hideSoftInputFromWindow(mEtEmailAddress.getWindowToken(), 0);
                        mEtEmailAddress.clearFocus();
                    }
                    return false;
                }
            });


            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validate()) {
                        PrefsUtils.setString(mContext, Constant.PREF_KEY_EMAIL_ADDRESS, mEtEmailAddress.getText().toString());
                        PrefsUtils.setString(mContext, Constant.PREF_KEY_WALLET_ADDRESS, mEtWalletAddress.getText().toString());
                        finish();
                        gotoMain();
                        PrefsUtils.setString(SplashActivity.this,
                                Constant.PREF_KEY_INFOMATION_SKIP_YN, Constant.N);

                    } else {
//                        ToastUtil.showToastAsShort(mContext, "입력되지 않았습니다.");
                    }

                }
            });


            tvSkip.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    PrefsUtils.setString(SplashActivity.this,
                            Constant.PREF_KEY_INFOMATION_SKIP_YN, Constant.N);
                    clbNotPaidShowDialog();


                }
            });

            builder.setView(v);

            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();

        }
    }


    /**
     * crypto showCryptopaid 팝업 		:: 메세지 1개 / 버튼 1개
     ************************************************************************************************************************************************/
    public void showCryptopaid(Context context, String title, String body) {

        closeDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper
                (context, android.R.style.Theme_Holo_Light));
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_alert_send_popup, null);

        AppCompatTextView txtMessage = v.findViewById(R.id.txtMessage);
        AppCompatTextView txtBody = v.findViewById(R.id.txtBody);
        FrameLayout frameOk = v.findViewById(R.id.frame_ok);

        if (StringUtils.isNotEmpty(title)) {
            txtMessage.setText(title);
            txtBody.setText(body);
        }

        frameOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
                gotoMain();
            }
        });

        frameOk.setVisibility(View.VISIBLE);

        builder.setView(v);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void clbNotPaidShowDialog() {

        showCryptopaid(mContext, getString(R.string.msg_clb_paid_title),
                getString(R.string.msg_clb_payment));
    }


    public boolean validate() {
        boolean valid = true;

        String email = mEtEmailAddress.getText().toString().trim();
        String walletAddress = mEtWalletAddress.getText().toString().trim();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEtEmailAddress.setError("Please enter your email address");
            valid = false;
        } else {
            mEtEmailAddress.setError(null);
        }

        if (walletAddress.isEmpty()) {
            mEtWalletAddress.setError("Please enter your wallet address.");
            valid = false;

        } else {
            mEtWalletAddress.setError(null);
        }

        return valid;
    }


    public void showAgreeDialog() {
        closeDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light));
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_alert_terms_of_use_popup, null);

        AppCompatTextView tvNext = v.findViewById(R.id.tv_next);
        AppCompatTextView tvAgree = v.findViewById(R.id.tv_agree);
        AppCompatCheckBox cbCheck = v.findViewById(R.id.cb_check);
        LinearLayout linearLayoutCheck = v.findViewById(R.id.linear_layout);

        linearLayoutCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayoutCheck.isSelected()) {
                    cbCheck.setBackgroundResource(R.drawable.check_gray);
                    cbCheck.setClickable(false);
                    tvAgree.setTextColor(Color.BLACK);
                    tvNext.setTextColor(Color.BLACK);
                    linearLayoutCheck.setSelected(false);
                    cbCheck.setChecked(false);


                } else {
                    cbCheck.setBackgroundResource(R.drawable.check_on);
                    tvAgree.setTextColor(0xFF006CFF);
                    tvNext.setTextColor(0xFF006CFF);
                    linearLayoutCheck.setSelected(true);
                    cbCheck.setChecked(true);

                }

            }
        });
        tvNext.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (cbCheck.isChecked()) {
                    PrefsUtils.setString(SplashActivity.this, Constant.PREF_KEY_USER_AGREE_YN, Constant.Y);
                    closeDialog();
                    showAllowDialog();
                } else {
//                    ToastUtil.showToastAsShort(SplashActivity.this, "동의해 주세요.");
                }
            }
        });

        builder.setView(v);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        cbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cbCheck.isChecked()) {
                    tvAgree.setTextColor(0xFF006CFF);
                    tvNext.setTextColor(0xFF006CFF);
                } else {
                    tvAgree.setTextColor(Color.BLACK);
                    tvNext.setTextColor(Color.BLACK);

                }
            }
        });
    }


    public void showAllowDialog() {
        closeDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light));
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_alert_authority_popup, null);


        AppCompatTextView tvAllow = v.findViewById(R.id.tv_allow);

        tvAllow.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
//               permissionCheck();
                permissionCheck();

            }
        });

        builder.setView(v);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

    }


    private void closeDialog() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dialog = null;
            }
        }
    }


    /**
     * (1) + (2) + (3) :: SplashActivity 작업 처리
     ************************************************************************************************************************************************/
    private void settingSplashMethod() {
        if (CommonUtil.isNetworkConnected(mContext)) {


            requestLanguage();
//            requestCountryCode();


            if (requestGooglePlayService()) {

            }

            String agreeYN = PrefsUtils.getString(SplashActivity.this, Constant.PREF_KEY_USER_AGREE_YN, Constant.N);
            String allowYN = PrefsUtils.getString(SplashActivity.this, Constant.PREF_KEY_USER_ALLOW_YN, Constant.N);
            if (Constant.N.equals(agreeYN)) {
                showAgreeDialog();
            } else if (Constant.N.equals(allowYN)) {
                showAllowDialog();
            } else {
                permissionCheck();
            }
        }
    }


    private void permissionCheck() {
        boolean isPermission = PermissionUtil.checkAndRequestPermission(this,
                RequestCodeUtil.PermissionReqCode.REQ_PERMISSION_ALL,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE);


        if (isPermission) {
//            gotoMain();

            showMyInfoMationDialog();
        } else {

        }
    }


    private void permissionPassed() {
        PrefsUtils.setString(SplashActivity.this, Constant.PREF_KEY_USER_ALLOW_YN, Constant.Y);
    }

    private void gotoMain() {

        permissionPassed();
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);

        finish();
    }

    /**
     * 권한 등록 CallBack Result
     ************************************************************************************************************************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.verifyPermissions(grantResults)) {
            switch (requestCode) {
                case RequestCodeUtil.PermissionReqCode.REQ_PERMISSION_ALL:
                    // 권한 등록에 모두 성공한 경우 작업이지만, 현재에는 아무 작업에 대한 진행이 없습니다.
//                    gotoMain();
                    showMyInfoMationDialog();
                    break;
            }
        } else {
            /**
             * 권한 등록에 거부하거나 허용하지 않은 경우 안드로이드 OS의 권한 페이지로 이동시켜주는 팝업창.
             * 선택 가능하며, 선택 거부하는 경우에는 특이사항 없습니다.
             */
            AlertDialogUtil.showPermissionCheckDialog(mContext, getString(R.string.msg_popup_permission_go),
                    new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            AlertDialogUtil.dismissDialog();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }, new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            AlertDialogUtil.dismissDialog();
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        closeDialog();
        super.onDestroy();
        //Remove all the callbacks otherwise navigation will execute even after activity is killed or closed.
        mWaitHandler.removeCallbacksAndMessages(null);

    }

    private void requestLanguage() {
        /**
         * 설치 초기에는 Null값입니다.
         * 설치시에 바로 적용되는 언어를 기본적으로 적용하게 됩니다.
         * 현재에는 [영어/한국어]가 있습니다.
         * 한국,미국, 없다면 기본은 영어 설정합니다.
         */
        switch (mUserData.getUserLanguage()) {
            case Constant.COMMON_NULL:
                Configuration configuration = getApplicationContext().getResources().getConfiguration();
                Locale systemLocale;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    systemLocale = getSystemLocale(configuration);
                else
                    systemLocale = getSystemLocaleLegacy(configuration);
                String strLanguage = systemLocale.getLanguage();

                switch (strLanguage) {
                    case Constant.KOREAN_S:
                        mUserData.setUserLanguage(Constant.K);
                        MyLanguageChangeWrapper.wrap(getBaseContext(), strLanguage);
                        break;

                    case Constant.ENGLISH_S:
                        mUserData.setUserLanguage(Constant.E);
                        MyLanguageChangeWrapper.wrap(getBaseContext(), strLanguage);
                        break;

                    default:

                        break;
                }
                break;

            case Constant.K:
                mUserData.setUserLanguage(Constant.K);
                MyLanguageChangeWrapper.wrap(getBaseContext(), Constant.KOREAN_S);
                break;

            case Constant.E:
                mUserData.setUserLanguage(Constant.E);
                MyLanguageChangeWrapper.wrap(getBaseContext(), Constant.ENGLISH_S);
                break;


            default:
                mUserData.setUserLanguage(Constant.E);
                MyLanguageChangeWrapper.wrap(getBaseContext(), Constant.ENGLISH_S);

        }
        userDataManager.setUserData(mUserData);
    }


    /**
     * (2) 국가 정보 설정
     ************************************************************************************************************************************************/
    private void requestCountryCode() {
        /**
         *  1. 현재 USIM의 국가코드를 확인합니다.
         *  2. 국가코드의 여부를 확인합니다.
         *  3. 국가코드가 없다면 IPStack을 확인합니다.
         *  4. 국가코드가 있다면 KR/JP/US인 경우에는 그대로 저장하며 ETC라면 IPStack으로 확인합니다.
         */
        String country = CommonUtil.getTelephonyManagerInfo(mContext);
        if (StringUtils.isEmpty(country)) {
//            requestIpStack();

        } else {
            mUserData.setUserConnetCountryISO(country);
            userDataManager.setUserData(mUserData);
        }

    }


    private void timeZoneSetting() {
        String strTimeZoneId = CommonUtil.getTimeZoneID();

        switch (strTimeZoneId) {
            case Constant.C_Korea:
                mUserData.setUserConnetCountryISO(Constant.KOREAN_C);
                break;
            default:
                String[] timeZoneUsArray = mContext.getResources().getStringArray(R.array.timezone_us);
                List<String> usTimeZoneList = new ArrayList<>();
                Collections.addAll(usTimeZoneList, timeZoneUsArray);


        }

    }


    /**
     * View Resource Setting
     ************************************************************************************************************************************************/
    @Override
    protected void uiInit() {
        super.uiInit();
        // 뒤로가기 버튼 사용 조건
        isBackButtonNotice = false;
        // Application User Local DataBase
        mUserData = userDataManager.getUserData();

    }

    /**
     * 구글 플레이 서비스 체크 여부
     ************************************************************************************************************************************************/
    private boolean requestGooglePlayService() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(mContext);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, RequestCodeUtil.GooglePlayServiceCheckReqCode.REQ_GOOGLE_CHECK, dialogInterface -> finish()).show();
            }
            return false;
        }
        return true;
    }


}
