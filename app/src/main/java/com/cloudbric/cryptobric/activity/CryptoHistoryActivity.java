package com.cloudbric.cryptobric.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.adapter.CryptoHistoryListAdapter;
import com.cloudbric.cryptobric.data.VirusScanData;
import com.cloudbric.cryptobric.exceptions.NoPreferenceValueException;
import com.cloudbric.cryptobric.interfaces.PackageEventListener;
import com.cloudbric.cryptobric.utils.CommonUtil;
import com.cloudbric.cryptobric.utils.Constant;
import com.cloudbric.cryptobric.utils.ScanDetected;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

public class CryptoHistoryActivity extends AppCompatActivity implements PackageEventListener {
    private static final String TAG = "CryptoHistoryActivity";

    private int mUndetectedCount;
    private int mDetectedCount;
    private int mNoMatchCount;
    TextView tvScanCount;
    TextView tvScanState;
    private Context mContext;
    private RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    CryptoHistoryListAdapter mHistorytAdapter;
    AppCompatTextView tvScanResultDesc;

    TextView tvTitle;


    FrameLayout frameFinish;
    FrameLayout frameTopbar;

    List<VirusScanData> scannedAndInstalledList = new ArrayList<>();
    private PackageEventReceiver mPackageEventReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_crypto_history_list);
        mContext = this;
        initUI();
        mRecyclerView = findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        scanHistoryList();

        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("Crypto Results");

        mHistorytAdapter = new CryptoHistoryListAdapter(mContext, scannedAndInstalledList, mDetectedCount);
        mRecyclerView.setAdapter(mHistorytAdapter);

        mPackageEventReceiver = new PackageEventReceiver();
        registerPackageEvent();
        setScannedTopColorChanged();

        frameFinish = findViewById(R.id.frame_finish);
        frameFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class PackageEventReceiver extends BroadcastReceiver {

        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "intent : " + intent);
            Log.d(TAG, "action : " + intent.getAction());
            Log.d(TAG, "data : " + intent.getData());

            if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
                Uri uri = intent.getData();
                String packageName = uri != null ? uri.getSchemeSpecificPart() : null;

                Log.d(TAG, "packageName : " + packageName);

                if (packageName == null) {
                    return;
                }

                // remove from Crypto Detected
                CommonUtil.removeResultInPreference(context, Constant.PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_DETECTED, packageName);

                // remove from Crypto
                CommonUtil.removeResultInPreference(context, Constant.PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_ALL, packageName);

                // remove from Detected
                CommonUtil.removeResultInPreference(context, Constant.PREF_KEY_ALL_SCANNED_VIRUS_RESULT_DETECTED, packageName);

                // remove from All
                CommonUtil.removeResultInPreference(context, Constant.PREF_KEY_ALL_SCANNED_VIRUS_RESULT_ALL, packageName);

                // update All detected count
                CommonUtil.updateAllScanDetectedCount(context);

                onPackgeDeleted(packageName);
            }
        }
    }

    private void initUI() {
        tvScanState = findViewById(R.id.tv_scan_state);
        tvScanCount = findViewById(R.id.tv_scan_count);
        frameTopbar = findViewById(R.id.frame_topbar);
        tvScanResultDesc = findViewById(R.id.tv_scan_result_desc);

    }

    private void setScannedTopColorChanged() {

        if (isDetected()) {
//            tvScanState.setText("RISK");
//            tvScanState.setTextColor(Color.WHITE);
            frameTopbar.setBackgroundColor(0xFFFF1212);
//            tvScanResultDesc.setText(mDetectedCount + " malicious codes");

        } else {
//            tvScanState.setText("SAFE");
//            tvScanState.setTextColor(Color.WHITE);
//            tvScanResultDesc.setText("No Threats detected.");
//            frameTopbar.setBackgroundResource(R.drawable.main_bacground_top_green_color);
            frameTopbar.setBackgroundColor(0xFF29DB00);

        }
        ScanDetected.setScanDetectedCount(mContext, mDetectedCount);

    }

    private void resetScanResult() {
        mUndetectedCount = 0;
        mNoMatchCount = 0;
        mDetectedCount = 0;
        scannedAndInstalledList.clear();
    }

    @SuppressLint("LongLogTag")
    private List<VirusScanData> scanHistoryList() {
        HashMap<String, String[]> scannedResultsAll = null;

        try {
            scannedResultsAll = CommonUtil.getScanResultsInPreference(this, Constant.PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_ALL);
        } catch (NoPreferenceValueException e) {
            e.printStackTrace();
        }


        if (scannedResultsAll == null || scannedResultsAll.isEmpty()) {
            return new ArrayList<>();
        } else {
            resetScanResult();
        }


        List<PackageInfo> pkgAppsList = getPackageManager().getInstalledPackages(PackageManager.PERMISSION_GRANTED);
        for (PackageInfo pack : pkgAppsList) {


            String[] infoArray = scannedResultsAll.get(pack.packageName);
            if (infoArray != null) {
                int order = 0;

                if ("UNDETECTED".equals(infoArray[1])) {
                    mUndetectedCount++;
                    order = 3;
                } else if ("DETECTED".equals(infoArray[1])) {
                    mDetectedCount++;
                    order = 1;
                } else if ("NO_MATCH".equals(infoArray[1])) {
                    mNoMatchCount++;
                    order = 2;
                }

                Log.d(TAG, "" + infoArray[0]);
                Log.d(TAG, "" + infoArray[1]);
                Log.d(TAG, "" + infoArray[2]);
                Log.d(TAG, "" + infoArray[3]);
                Log.d(TAG, "" + infoArray[4]);


                VirusScanData vScanData = new VirusScanData();
                vScanData.setPackageName(infoArray[0]);
                vScanData.setScanResult(infoArray[1]);
                vScanData.setVirusInfo(infoArray[2]);
                vScanData.setAppName(infoArray[3]);
                vScanData.setCompanyName(infoArray[4]);
                vScanData.order = order;

                scannedAndInstalledList.add(vScanData);
            }
            sortScanDataList();
        }

        // cryptoscan 검출 count 저장
        ScanDetected.setScanDetectedCount(mContext, mDetectedCount);

        return scannedAndInstalledList;
    }

    @Override
    public void registerPackageEvent() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");

        registerReceiver(mPackageEventReceiver, intentFilter);
    }

    @Override
    public void unregisterPackageEvent() {
        if (mPackageEventReceiver != null) {
            unregisterReceiver(mPackageEventReceiver);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onPackgeDeleted(String pacakageName) {
        Log.d(TAG, "onPackgeDeleted: " + pacakageName);
        refreshResultData();

    }

    private void refreshResultData() {
        updateDetectCount();
        scanHistoryList();
        mHistorytAdapter.setData(scannedAndInstalledList, mDetectedCount);
        setScannedTopColorChanged();

    }

    private void updateDetectCount() {
        resetScanResult();

        for (int i = 0; i < scannedAndInstalledList.size(); i++) {
            VirusScanData vScanData = scannedAndInstalledList.get(i);
            String scanResult = vScanData.getScanResult();
            if ("UNDETECTED".equals(scanResult)) {
                mUndetectedCount++;
            } else if ("DETECTED".equals(scanResult)) {
                mDetectedCount++;
            } else if ("NO_MATCH".equals(scanResult)) {
                mNoMatchCount++;
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterPackageEvent();

        super.onDestroy();
    }


    private void sortScanDataList() {
        Collections.sort(scannedAndInstalledList, new Comparator<VirusScanData>() {
            @Override
            public int compare(VirusScanData b1, VirusScanData b2) {
                return Integer.compare(b1.getOrder(), b2.getOrder());
            }
        });
    }


    private boolean isDetected() {
        return mDetectedCount > 0;
    }


}
