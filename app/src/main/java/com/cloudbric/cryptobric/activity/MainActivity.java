package com.cloudbric.cryptobric.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.customview.Category;
import com.cloudbric.cryptobric.customview.CategoryDropdownAdapter;
import com.cloudbric.cryptobric.customview.CategoryDropdownMenu;
import com.cloudbric.cryptobric.data.VirusScanData;
import com.cloudbric.cryptobric.exceptions.NoPreferenceValueException;
import com.cloudbric.cryptobric.listener.OnSingleClickListener;
import com.cloudbric.cryptobric.network.CommonApiCall;
import com.cloudbric.cryptobric.utils.AlertDialogUtil;
import com.cloudbric.cryptobric.utils.CommonUtil;
import com.cloudbric.cryptobric.utils.Constant;
import com.cloudbric.cryptobric.utils.PrefsUtils;
import com.cloudbric.cryptobric.utils.ScanDetected;
import com.cloudbric.cryptobric.utils.ToastUtil;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import net.ahope.virusshieldlib.VirusScanner;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.NonNull;


//메인 화면
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_crypto_scan)
    AppCompatImageButton btnCryptoScan;

    @BindView(R.id.linear_validated_crypto)
    LinearLayout linearValidatedCrypto;

    @BindView(R.id.frame_support_bg)
    FrameLayout frameSupportBg;

    @BindView(R.id.linear_about)
    LinearLayout linearAbout;


    @BindView(R.id.linear_last_scan)
    LinearLayout linearLastScan;

    @BindView(R.id.linear_settings)
    LinearLayout linearSettings;

    @BindView(R.id.linear_support)
    LinearLayout linearSupport;

    @BindView(R.id.tv_scan)
    AppCompatTextView tvScan;

    @BindView(R.id.tv_current_scan_state)
    AppCompatTextView tvCurrentScanState;


    FrameLayout frameMore;

    @BindView(R.id.tv_last_scan)
    AppCompatTextView tvLastScan;
    @BindView(R.id.tv_not_scanned)
    AppCompatTextView tvNotScanned;

    @BindView(R.id.tv_scan_count)
    AppCompatTextView tvScanCount;


    @BindView(R.id.toggle_scan_mode)
    ToggleButton toggleScanMode;

    HashMap<String, String[]> cryptoScannedResults;
    HashMap<String, String[]> scanResultObj;
    boolean isScanningMode = false;
    private final static String TAG = "MainActivity";
    public static final int SCAN_PERIOD_WARNING_DAYS = 7;
    private List<VirusScanData> mVireusScanResultList = new ArrayList<>();
    Context context;
    private AsyncTask<Boolean, Integer, Boolean> mPourBeerTask;

    private List<Fragment> mFragments;
    private int mCurrentItemID;
    String mFcmToken;
    private android.os.Handler mHandler;
    private Runnable mUpdateTime;
    private VirusScanner virusScanner = null;
    private Disposable disposable;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;

        progressBar = findViewById(R.id.spin_kit);


        if (toggleScanMode.getText().equals("all")) {
            toggleScanMode.setBackgroundResource(R.drawable.all_on);

        } else if (toggleScanMode.getText().equals("crypto")) {
            toggleScanMode.setBackgroundResource(R.drawable.crypto_on);

        }

        try {
            cryptoScannedResults = CommonUtil.getScanResultsInPreference
                    (context, Constant.PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_DETECTED);

        } catch (NoPreferenceValueException e) {
            e.printStackTrace();
            cryptoScannedResults = new HashMap<>();
        }

        frameMore = findViewById(R.id.frame_more);
        frameMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanningMode) {
                    ToastUtil.showToastAsShort(context, getString(R.string.msg_scanning));
                } else {
                    showScanCategoryMenu();
                }
            }
        });

        toggleScanMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanningMode) {

                    ToastUtil.showToastAsShort(context, getString(R.string.msg_scanning));
                } else {
                    if (toggleScanMode.getText().equals("all")) {
                        toggleScanMode.setBackgroundResource(R.drawable.all_on);

                    } else if (toggleScanMode.getText().equals("crypto")) {
                        toggleScanMode.setBackgroundResource(R.drawable.crypto_on);

                    }
                    DateTime currentTime = new DateTime();
                    isScanPeriodPastWarningNeeded(currentTime.getMillis());
                    cryptoScannedChangeBackgroundColor();
                }
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        mFcmToken = task.getResult().getToken();

                        // Log and toast
//                        Log.d(TAG, "=========== fcm token ===========:" + token);
//                        Toast.makeText(MainActivity.this, mFcmToken, Toast.LENGTH_SHORT).show();
                    }
                });


//        statusBarSetColor();
        postDevceInfo();


        linearSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanningMode) {
                    ToastUtil.showToastAsShort(context, getString(R.string.msg_scanning));
                } else {
                    callSettingsActivity();
                }
            }
        });

        linearAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanningMode) {
                    ToastUtil.showToastAsShort(context, getString(R.string.msg_scanning));
                } else {
                    callAboutActiivty();
                }
            }
        });
        linearSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanningMode) {
                    ToastUtil.showToastAsShort(context, getString(R.string.msg_scanning));
                } else {
                    bottomSheet();
                }
            }
        });
        btnCryptoScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strScanType = "";
                strScanType = toggleScanMode.getText().toString();

                if ("STOP".equals(tvScan.getText().toString())) {
                    // running
                    tvScan.setText("SCAN");
                    linearLastScan.setVisibility(View.VISIBLE);
                    tvScanCount.setVisibility(View.GONE);
                    stopScan();
                    stopCountdown();
                } else {
                    tvScan.setText("STOP");
                    linearLastScan.setVisibility(View.GONE);
                    tvScanCount.setVisibility(View.VISIBLE);

                    if (strScanType.equals("crypto")) {
                        HashMap<String, Object> params = new HashMap<>();
                        cryptoScanVirus(params);
                        tvScanCount.setVisibility(View.VISIBLE);

                    } else if (strScanType.equals("all")) {
                        HashMap<String, Object> params = new HashMap<>();
                        allScanVirus(params);
                    }

                }
            }
        });
        linearValidatedCrypto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanningMode) {
                    ToastUtil.showToastAsShort(context, getString(R.string.msg_scanning));
                } else {
                    callCryptoListActivity();
                }
            }
        });


    }


    @SuppressLint("MissingPermission")
    private void postDevceInfo() {
        Locale systemLocale = getApplicationContext().getResources().getConfiguration().locale;
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult ->
        {
            Map<String, String> params = new HashMap<>();
            params.put("uuid", CommonUtil.GetDevicesUUID(context));
            params.put("registration_id", mFcmToken);
            params.put("model", Build.MODEL);
            params.put("platform", Constant.MEMBER_JOIN_USER_TYPE_OS);
            params.put("version", Build.VERSION.RELEASE);
            params.put("manufacturer", Build.MANUFACTURER);
            params.put("serial", tm.getDeviceId());
            params.put("country_code", systemLocale.getLanguage());
            CommonApiCall.postDevceInfo(context, TAG, params);

        });
    }

    //앱버전 코드
    public int getAppVersionCode() {
        PackageInfo packageInfo = null;         //패키지에 대한 전반적인 정보

        //PackageInfo 초기화
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }

        return packageInfo.versionCode;
    }


    //crypto scan시 배경 컬러 변경
    private void cryptoScannedChangeBackgroundColor() {


        //allscan mode 일 경우
        if (toggleScanMode.getText().equals("all")) {

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            DateTime currentTime = new DateTime();
            boolean scanPeriodPastWarningNeeded = isScanPeriodPastWarningNeeded(currentTime.getMillis());
            int allScanDetectedCount = ScanDetected.getAllScanDetectedCount(context);

            if (hasPreviousAllScannedVirusResult()) {
                //바이러스가 1개라도 있을경우
                btnCryptoScan.setBackgroundResource(R.drawable.scan_round_background_red);
                frameSupportBg.setBackgroundResource(R.drawable.main_bacground_gradient_red_color);
                window.setStatusBarColor(0xFFFF1212);
                tvScan.setTextColor(0xFFFF1212);
                tvCurrentScanState.setText(allScanDetectedCount + " " + getString(R.string.msg_malicious_threats));


            } else if (scanPeriodPastWarningNeeded) {
                //스캔 7일 지난후
                btnCryptoScan.setBackgroundResource(R.drawable.scan_round_background_red);
                frameSupportBg.setBackgroundResource(R.drawable.main_bacground_gradient_orange_color);
                window.setStatusBarColor(0xFFff8A00);
                tvScan.setTextColor(0xFFff8A00);
                tvCurrentScanState.setText(getString(R.string.msg_malicious_required));


            } else if (allScanDetectedCount > 0) {
                btnCryptoScan.setBackgroundResource(R.drawable.scan_round_background_red);
                frameSupportBg.setBackgroundResource(R.drawable.main_bacground_gradient_red_color);
                window.setStatusBarColor(0xFFFF1212);
                tvScan.setTextColor(0xFFFF1212);
                tvCurrentScanState.setText(allScanDetectedCount + " " + getString(R.string.msg_malicious_threats));

            } else {
                btnCryptoScan.setBackgroundResource(R.drawable.scan_round_background);
                frameSupportBg.setBackgroundResource(R.drawable.main_bacground_gradient_color);
                window.setStatusBarColor(0xFF006CFF);
                tvScan.setTextColor(0xFF006CFF);
                tvCurrentScanState.setText(getString(R.string.msg_hidden_threats));
            }


            //클립토 스캔모드 일경우
        } else if (toggleScanMode.getText().equals("crypto")) {

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            DateTime currentTime = new DateTime();
            boolean scanPeriodPastWarningNeeded = isScanPeriodPastWarningNeeded(currentTime.getMillis());
            int scanDetectedCount = ScanDetected.getScanDetectedCount(context);
//            int scanAllDetectedCount = ScanDetected.getAllScanDetectedCount(context);

            if (hasPreviousCryptoScannedVirusResult()) {
                //바이러스가 1개라도 있을경우
                btnCryptoScan.setBackgroundResource(R.drawable.scan_round_background_red);
                frameSupportBg.setBackgroundResource(R.drawable.main_bacground_gradient_red_color);
                window.setStatusBarColor(0xFFFF1212);
                tvScan.setTextColor(0xFFFF1212);
                tvCurrentScanState.setText(scanDetectedCount + " " + getString(R.string.msg_malicious_threats));


            } else if (scanPeriodPastWarningNeeded) {
                //스캔 7일 지난후
                btnCryptoScan.setBackgroundResource(R.drawable.scan_round_background_red);
                frameSupportBg.setBackgroundResource(R.drawable.main_bacground_gradient_orange_color);
                window.setStatusBarColor(0xFFff8A00);
                tvScan.setTextColor(0xFFff8A00);
                tvCurrentScanState.setText(getString(R.string.msg_malicious_required));


            } else if (scanDetectedCount > 0) {
                btnCryptoScan.setBackgroundResource(R.drawable.scan_round_background_red);
                frameSupportBg.setBackgroundResource(R.drawable.main_bacground_gradient_red_color);
                window.setStatusBarColor(0xFFFF1212);
                tvScan.setTextColor(0xFFFF1212);
                tvCurrentScanState.setText(scanDetectedCount + " " + getString(R.string.msg_malicious_threats));

            } else {
                btnCryptoScan.setBackgroundResource(R.drawable.scan_round_background);
                frameSupportBg.setBackgroundResource(R.drawable.main_bacground_gradient_color);
                window.setStatusBarColor(0xFF006CFF);
                tvScan.setTextColor(0xFF006CFF);
                tvCurrentScanState.setText(getString(R.string.msg_hidden_threats));
            }
        }


    }


    private boolean isScanPeriodPastWarningNeeded(final long currentTime) {

        //test
//        PrefsUtils.setLong(context, Constant.PREF_KEY_LAST_SCANNED_TIME, (currentTime - (1000 * 60 * 60 * 24 * 15)));
//        PrefsUtils.setLong(context, Constant.PREF_KEY_LAST_SCANNED_TIME, (currentTime));
        long lastScannedTime = 0;
        String strScanType = "";
        strScanType = toggleScanMode.getText().toString();

        if (strScanType.equals("all")) {
            lastScannedTime = ScanDetected.getAllDetectedTimeForMain(context);


        } else if (strScanType.equals("crypto")) {
            lastScannedTime = ScanDetected.getDetectedTimeForMain(context);

        }

        DateTime date1 = new DateTime(lastScannedTime);
        DateTime date2 = new DateTime(currentTime);

        int notScanned = 0;
        int minutes = Math.abs(Minutes.minutesBetween(date1, date2).getMinutes());
        int hours = Math.abs(Hours.hoursBetween(date1, date2).getHours());
        int days = Math.abs(Days.daysBetween(date1, date2).getDays());
        int weeks = Math.abs(Weeks.weeksBetween(date1, date2).getWeeks());

        StringBuilder sb = new StringBuilder();

        // 한번도 스캔 하지 않았을 때(Not Scanned)
        if (lastScannedTime == 0) {
            sb.append("Not Scanned");
            tvNotScanned.setText(sb);
            return false;
        } else if (weeks > 0) {
            sb.append(weeks + " weeks ago");
            tvNotScanned.setText(sb);
        } else if (days > 1) {
            sb.append(days + " days ago");
            tvNotScanned.setText(sb);

        } else if (hours > 0) {
            sb.append(hours + " hours ago");
            tvNotScanned.setText(sb);
        } else if (minutes > 0) {
            sb.append(minutes + " minutes ago");
            tvNotScanned.setText(sb);
        } else { // 1분 이내
            sb.append("Just before");
            tvNotScanned.setText(sb);
        }

        Log.d(TAG, "lastScannedTime days: " + days);
//        tvLastScan.setText(String.valueOf(days));
        tvNotScanned.setText(sb.toString());

        return days >= SCAN_PERIOD_WARNING_DAYS;
    }

    private boolean hasPreviousAllScannedVirusResult() {
        try {

            HashMap<String, String[]> allScannedResults = CommonUtil.getScanResultsInPreference
                    (context, Constant.PREF_KEY_ALL_SCANNED_VIRUS_RESULT_DETECTED);
            Log.d(TAG, "hasPreviousAllScannedVirusResult: " + allScannedResults);

            return allScannedResults != null && allScannedResults.size() > 0;
        } catch (NoPreferenceValueException e) {
            e.printStackTrace();
            return false;
        }
    }


    private boolean hasPreviousCryptoScannedVirusResult() {
        try {
            HashMap<String, String[]> cryptoScannedResults = CommonUtil.getScanResultsInPreference
                    (context, Constant.PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_DETECTED);
//            tvCurrentScanState.setText(cryptoScannedResults.size()+ " " + getString(R.string.msg_malicious));

            Log.d(TAG, "hasPreviousCryptoScannedVirusResult: " + cryptoScannedResults);


            return cryptoScannedResults != null && cryptoScannedResults.size() > 0;
        } catch (NoPreferenceValueException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void showScanCategoryMenu() {

        final CategoryDropdownMenu menu = new CategoryDropdownMenu(this);
        menu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        menu.setWidth(getPxFromDp(200));
        menu.setOutsideTouchable(true);
        menu.setFocusable(true);
//        menu.showAsDropDown(frameMore);
        menu.showAtLocation(frameMore, Gravity.RIGHT, 100, 450);
        menu.setCategorySelectedListener(new CategoryDropdownAdapter.CategorySelectedListener() {
            @Override
            public void onCategorySelected(int position, Category category) {
                menu.dismiss();

                switch (position) {
                    case Constant.ALL_SCAN_LIST:
                        callAllScanHistoryActivity();
                        break;

                    case Constant.CRYPTO_SCAN_LIST:
                        callCryptoScanHistoryActivity();
                        break;
                }

            }

        });
    }


    private void callAllScanHistoryActivity() {
        Intent intent = new Intent(this, AllHistoryActivity.class);
        startActivity(intent);

    }

    private void callCryptoScanHistoryActivity() {
        Intent intent = new Intent(this, CryptoHistoryActivity.class);
        startActivity(intent);

    }

    //Convert DP to Pixel
    private int getPxFromDp(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void bottomSheet() {
        String[] strArray = {"support@cloudbric.com"};

        composeEmail(strArray, "");
    }

    public void composeEmail(String[] addresses, String subject) {


        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    //all SCAN
    private void allScanVirus(HashMap<String, Object> scanMap) {
        tvCurrentScanState.setText(R.string.msg_all_scan);
        isScanningMode = true;
        tvScan.setText("STOP");
        linearLastScan.setVisibility(View.GONE);
        tvLastScan.setVisibility(View.GONE);
        tvScanCount.setVisibility(View.VISIBLE);
        tvScanCount.setText(0 + "%");
        if (CommonUtil.isNetworkConnected(context)) {
            Set<String> packages = scanMap.keySet();
            for (String key : packages) {
                Log.d(TAG, "packages :" + packages);
                Log.d(TAG, "key :" + key);
            }

            if (virusScanner == null) {
                virusScanner = new VirusScanner(context);
            }

            try {
                Observable<HashMap<String, Object>> observable = Observable.fromCallable(() -> {
                    // handler start: virusScanner.getCurrent...
                    startCountdown(virusScanner);
                    return virusScanner.startVirusScan();
                });
                observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> postAllScanResult(result), Throwable::printStackTrace);
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        } else {

            AlertDialogUtil.showSingleDialog(context, getString(R.string.msg_not_network), new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    AlertDialogUtil.dismissDialog();
                }
            });

        }


    }


    //crypto SCAN
    private void cryptoScanVirus(HashMap<String, Object> scanMap) {
        tvCurrentScanState.setText(R.string.msg_crypto_scan);
        isScanningMode = true;
        if (CommonUtil.isNetworkConnected(context)) {
            Set<String> packages = scanMap.keySet();
            for (String key : packages) {
                Log.d(TAG, "packages :" + packages);
                Log.d(TAG, "key :" + key);
            }

            if (virusScanner == null) {
                virusScanner = new VirusScanner(context);
            }

            try {
                Observable<HashMap<String, Object>> observable = Observable.fromCallable(() -> {
                    // handler start: virusScanner.getCurrent...
                    startCountdown(virusScanner);

                    return virusScanner.startVirusScanWithList();
                });
                observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> postCryptoScanResult(result), Throwable::printStackTrace);

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        } else {

            AlertDialogUtil.showSingleDialog(context, getString(R.string.msg_not_network), new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    AlertDialogUtil.dismissDialog();
                }
            });

        }


    }


    @Override
    protected void onStop() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        stopCountdown();
        stopScan();
        tvScanCount.setVisibility(View.GONE);
        super.onStop();
    }

    private void stopScan() {
        if (virusScanner != null) {
            cryptoScannedChangeBackgroundColor();
            virusScanner.stopVirusScan();
            isScanningMode = false;
        }
    }

    //crypto scan 결과
    private void postCryptoScanResult(HashMap<String, Object> scanMap) {
        int status = (int) scanMap.get("status");
        if (status == VirusScanner.SCAN_SUCCESS) {
//            Toast.makeText(context, getString(R.string.msg_scan_success), Toast.LENGTH_SHORT).show();
            // 바이러스 검사 성공
            scanResultObj = (HashMap<String, String[]>) scanMap.get("scanResultObj");

            if (scanResultObj == null) {
                return;
            }

            HashMap<String, String[]> detectedResultObj = new HashMap<>();

            for (String key : scanResultObj.keySet()) {
                String[] mInfos = scanResultObj.get(key);
                String virusInfo = " ";
                if (mInfos[1].equals("DETECTED")) {
                    virusInfo = mInfos[2];
                    detectedResultObj.put(key, mInfos);
                }

                Log.d("VirusShield", "Package : " + mInfos[0] + ", Result : " + mInfos[1] + " " + virusInfo);
            }

            // cryptoscan 결과 저장
            CommonUtil.setScanResultsToPreference(context, scanResultObj, Constant.PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_ALL);
            //cryptoscan 에서 발견된 바이러스만 저장
            CommonUtil.setScanResultsToPreference(context, detectedResultObj, Constant.PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_DETECTED);
            ScanDetected.setScanDetectedTime(context, System.currentTimeMillis());
//            PrefsUtils.setLong(context, Constant.PREF_KEY_LAST_SCANNED_TIME, (System.currentTimeMillis() - (1000 * 60)));

            // cryptoscan 검출 count 저장
            ScanDetected.setScanDetectedCount(context, detectedResultObj.size());

            callCryptoScanResultListActivity(scanResultObj);

            cryptoScanResutObjConvert(Constant.RK_SCAN_STATUS_SCAN_SUCCESS);

        } else if (status == VirusScanner.SCAN_ERROR) {
            tvScan.setText("SCAN");
            linearLastScan.setVisibility(View.VISIBLE);

            cryptoScanResutObjConvert(Constant.RK_SCAN_STATUS_SCAN_ERROR);

            // 바이러스 검사 실패
            Log.d("VirusShield", "Failed to scan virus");
//            Toast.makeText(context, "검사실패", Toast.LENGTH_SHORT).show();


        } else if (status == VirusScanner.SCAN_SERVER_DOWN) {
            // 바이러스 검사 서버에 연결 실패
            tvScan.setText("SCAN");
            linearLastScan.setVisibility(View.VISIBLE);
            cryptoScanResutObjConvert(Constant.RK_SCAN_STATUS_SCAN_SERVER_DOWN);
            Log.d("VirusShield", "Failed to connect to virus scan server");
//            Toast.makeText(context, "서버연결 실패", Toast.LENGTH_SHORT).show();
            AlertDialogUtil.showSingleDialog(context, getString(R.string.msg_not_network), new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    AlertDialogUtil.dismissDialog();
                }
            });
        } else {
            AlertDialogUtil.showSingleDialog(context, getString(R.string.msg_failed), new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    AlertDialogUtil.dismissDialog();
                }
            });
        }

        isScanningMode = false;
    }


    //전체스캔 결과
    private void postAllScanResult(HashMap<String, Object> scanMap) {
        int status = (int) scanMap.get("status");
        if (status == VirusScanner.SCAN_SUCCESS) {
            tvScan.setText("STOP");
            linearLastScan.setVisibility(View.GONE);

//            Toast.makeText(context, getString(R.string.msg_scan_success), Toast.LENGTH_SHORT).show();
            // 바이러스 검사 성공
            scanResultObj = (HashMap<String, String[]>) scanMap.get("scanResultObj");

            if (scanResultObj == null) {
                return;
            }

            HashMap<String, String[]> detectedResultObj = new HashMap<>();

            for (String key : scanResultObj.keySet()) {
                String[] mInfos = scanResultObj.get(key);
                String virusInfo = " ";
                if (mInfos[1].equals("DETECTED")) {
                    virusInfo = mInfos[2];
                    detectedResultObj.put(key, mInfos);
                }

                Log.d("VirusShield", "Package : " + mInfos[0] + ", Result : " + mInfos[1] + " " + virusInfo);
            }

            //전체 결과 저장
            CommonUtil.setScanResultsToPreference(context, scanResultObj, Constant.PREF_KEY_ALL_SCANNED_VIRUS_RESULT_ALL);

            //전체 결과에서 발견된 바이러스만 저장
            CommonUtil.setScanResultsToPreference(context, detectedResultObj, Constant.PREF_KEY_ALL_SCANNED_VIRUS_RESULT_DETECTED);
            ScanDetected.setAllScanDetectedTime(context, System.currentTimeMillis());

            // all scan 검출 count 저장
            ScanDetected.setAllScanDetectedCount(context, detectedResultObj.size());


            callAllScanResultListActivity(scanResultObj);

            scanResutObjConvert(Constant.RK_SCAN_STATUS_SCAN_SUCCESS);

        } else if (status == VirusScanner.SCAN_ERROR) {

            scanResutObjConvert(Constant.RK_SCAN_STATUS_SCAN_ERROR);

            // 바이러스 검사 실패
            Log.d("VirusShield", "Failed to scan virus");
//            Toast.makeText(context, "검사실패", Toast.LENGTH_SHORT).show();


        } else if (status == VirusScanner.SCAN_SERVER_DOWN) {
            // 바이러스 검사 서버에 연결 실패
            tvScanCount.setVisibility(View.GONE);
            tvScan.setText("SCAN");
            linearLastScan.setVisibility(View.VISIBLE);
            scanResutObjConvert(Constant.RK_SCAN_STATUS_SCAN_SERVER_DOWN);
            Log.d("VirusShield", "Failed to connect to virus scan server");
//            Toast.makeText(context, "서버연결 실패", Toast.LENGTH_SHORT).show();
            AlertDialogUtil.showSingleDialog(context, getString(R.string.msg_not_network), new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    AlertDialogUtil.dismissDialog();
                }
            });
        } else {
            AlertDialogUtil.showSingleDialog(context, getString(R.string.msg_failed), new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    AlertDialogUtil.dismissDialog();
                }
            });
        }

        isScanningMode = false;
    }


    private void scanResutObjConvert(String scanStatusType) {

        List<String> resultItems = new ArrayList<>();
        for (String key : scanResultObj.keySet()) {
            String[] infoArray = scanResultObj.get(key);


            VirusScanData vScanData = new VirusScanData();
            vScanData.setPackageName(infoArray[0]);
            vScanData.setScanResult(infoArray[1]);
            vScanData.setVirusInfo(infoArray[2]);
            resultItems.add(String.valueOf(vScanData));
        }


        DateTime dt = new DateTime();
        String now = dt.toString("yyyy-MM-dd HH:mm:ss");


        Map<String, String> params = new HashMap<>();
        params.put("uuid", CommonUtil.GetDevicesUUID(context));
        params.put("user_email", PrefsUtils.getString(MainActivity.this,
                Constant.PREF_KEY_EMAIL_ADDRESS, ""));
        params.put("user_wallet_address", PrefsUtils.getString(MainActivity.this,
                Constant.PREF_KEY_WALLET_ADDRESS, ""));
        params.put("inspect_type", Constant.RK_INSPECT_TYPE_ALL);
        params.put("status", scanStatusType);
        params.put("scan_result", String.valueOf(resultItems));
        params.put("app_count", String.valueOf(resultItems.size()));
        params.put("scan_date", now);
        CommonApiCall.postScanResultList(context, TAG, params);


    }

    private void cryptoScanResutObjConvert(String scanStatusType) {

        List<String> resultItems = new ArrayList<>();
        for (String key : scanResultObj.keySet()) {
            String[] infoArray = scanResultObj.get(key);


            VirusScanData vScanData = new VirusScanData();
            vScanData.setPackageName(infoArray[0]);
            vScanData.setScanResult(infoArray[1]);
            vScanData.setVirusInfo(infoArray[2]);
            resultItems.add(String.valueOf(vScanData));
        }


        DateTime dt = new DateTime();
        String now = dt.toString("yyyy-MM-dd HH:mm:ss");


        Map<String, String> params = new HashMap<>();
        params.put("uuid", CommonUtil.GetDevicesUUID(context));
        params.put("user_email", PrefsUtils.getString(MainActivity.this,
                Constant.PREF_KEY_EMAIL_ADDRESS, ""));
        params.put("user_wallet_address", PrefsUtils.getString(MainActivity.this,
                Constant.PREF_KEY_WALLET_ADDRESS, ""));
        params.put("inspect_type", Constant.RK_INSPECT_TYPE_CRYPTO);
        params.put("status", scanStatusType);
        params.put("scan_result", String.valueOf(resultItems));
        params.put("app_count", String.valueOf(resultItems.size()));
        params.put("scan_date", now);
        CommonApiCall.postScanResultList(context, TAG, params);


    }

    private void callCryptoScanResultListActivity(HashMap<String, String[]> scanResultObj) {
        Intent intent = new Intent(context, CryptoResultListActivity.class);
        intent.putExtra("scanResultObj", scanResultObj);
        startActivity(intent);
    }


    private void callAllScanResultListActivity(HashMap<String, String[]> scanResultObj) {
        Intent intent = new Intent(context, AllResultListActivity.class);
        intent.putExtra("scanResultObj", scanResultObj);
        startActivity(intent);
    }


    private void callCryptoListActivity() {
        Intent intent = new Intent(context, VerificationListActivity.class);
        startActivity(intent);
    }


    private void callSettingsActivity() {
        Intent intent = new Intent(context, SettingsActivity.class);
        Bundle extras = new Bundle();
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    private void callAboutActiivty() {

        Intent intent = new Intent(context, AboutActivity.class);
        Bundle extras = new Bundle();
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvScan.setText("SCAN");
        tvScanCount.setText(0 + "%");
        tvLastScan.setVisibility(View.VISIBLE);
        linearLastScan.setVisibility(View.VISIBLE);
        cryptoScannedChangeBackgroundColor();
        tvScanCount.setVisibility(View.VISIBLE);
        tvScanCount.setVisibility(View.GONE);
        tvScan.setVisibility(View.VISIBLE);
    }

    public void startCountdown(VirusScanner virusScanner) {
        Log.d(TAG, "## startCountdown");
        tvScanCount.setVisibility(View.VISIBLE);
        tvScan.setText("STOP");
        linearLastScan.setVisibility(View.GONE);
//        mRingProgressBar2.setEnabled(true);
        tvScanCount.setText(0 + "%");

        mUpdateTime = new Runnable() {
            public void run() {
                Sprite doubleBounce = new DoubleBounce();
                progressBar.setIndeterminateDrawable(doubleBounce);
                int currentProgress = virusScanner.getCurrentProgress();
                Log.d(TAG, "## mUpdateTime > mCountdown: " + currentProgress);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvScanCount.setText(currentProgress + "%");
                    }
                });

                if (currentProgress == 100) {
                    stopCountdown();
//                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show());
                    return;
                }
                mHandler.postDelayed(this, 1000);
            }
        };

        mHandler = new Handler(Looper.getMainLooper());
        mHandler.removeCallbacks(mUpdateTime);
        mHandler.postDelayed(mUpdateTime, 0);
    }

    //
    public void stopCountdown() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mUpdateTime);

            tvLastScan.setVisibility(View.VISIBLE);
            tvScanCount.setVisibility(View.GONE);
            mUpdateTime = null;
            mHandler = null;
        }
    }

}

