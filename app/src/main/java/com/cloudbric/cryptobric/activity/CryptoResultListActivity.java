package com.cloudbric.cryptobric.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.adapter.CryptoResultListAdapter;
import com.cloudbric.cryptobric.data.VirusScanData;
import com.cloudbric.cryptobric.interfaces.PackageEventListener;
import com.cloudbric.cryptobric.utils.CommonUtil;
import com.cloudbric.cryptobric.utils.Constant;
import com.cloudbric.cryptobric.utils.PrefsUtils;
import com.cloudbric.cryptobric.utils.ScanDetected;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CryptoResultListActivity extends AppCompatActivity implements PackageEventListener {
    private static final String TAG = "CryptoResultListActivity";

    RecyclerView mRecyclerView;
    private AlertDialog dialog;
    HashMap<String, String[]> mScanResultMap;

    @Nullable
    @BindView(R.id.tv_title)
    TextView tvTitle;


    @Nullable
    @BindView(R.id.tv_scan_state)
    AppCompatTextView tvScanState;
    @Nullable
    @BindView(R.id.frame_topbar)
    FrameLayout frameTopbar;

    Toolbar mToolbar;
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    FrameLayout frameOk;

    @Nullable
    @BindView(R.id.frame_finish)
    FrameLayout frameFinish;

    @BindView(R.id.tv_scan_result_desc)
    AppCompatTextView tvScanResultDesc;


    private List<VirusScanData> vireusScanDataList = new ArrayList<>();
    private PackageEventReceiver mPackageEventReceiver;

    private LinearLayoutManager mLayoutManager;
    CryptoResultListAdapter mCryptoResultListAdapter;
    Context context;
    static final boolean GRID_LAYOUT = false;
    private int mUndetectedCount;
    private int mDetectedCount;
    private int mNoMatchCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result_list);
        context = this;
        ButterKnife.bind(this);

        setScannedTopColorChanged();
        tvTitle.setText("Crypto Results");

        frameFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPackageEventReceiver = new PackageEventReceiver();
        getIntentData();
        registerPackageEvent();
        scanResultList();
        sortScanDataList();
        initUI();

        String paidYn = PrefsUtils.getString(context, Constant.PREF_KEY_CLB_PAID_YN, "");
        if (paidYn.equals(Constant.Y)) {
            Log.d(TAG, "paidYn Y!!!");
//            clbPaymentsShowDialog();
        } else {
            clbPaymentsShowDialog();

        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        mScanResultMap =
                (HashMap<String, String[]>) intent.getSerializableExtra("scanResultObj");

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

                // remove from Detected
                CommonUtil.removeResultInPreference(context, Constant.PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_DETECTED, packageName);

                // remove from All
                CommonUtil.removeResultInPreference(context, Constant.PREF_KEY_CRYPTO_SCANNED_VIRUS_RESULT_ALL, packageName);

                onPackgeDeleted(packageName);
            }
        }
    }


    private List<VirusScanData> scanResultList() {

        for (String key : mScanResultMap.keySet()) {
            String[] infoArray = mScanResultMap.get(key);
            String virusInfo = " ";
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

            VirusScanData vScanData = new VirusScanData();
            vScanData.setPackageName(infoArray[0]);
            vScanData.setScanResult(infoArray[1]);
            vScanData.setVirusInfo(infoArray[2]);
            vScanData.setAppName(infoArray[3]);
            vScanData.setCompanyName(infoArray[4]);
            vScanData.order = order;


            vireusScanDataList.add(vScanData);

        }
        return vireusScanDataList;
    }

    private void resetScanResult() {
        mUndetectedCount = 0;
        mNoMatchCount = 0;
        mDetectedCount = 0;
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


    @SuppressLint("LongLogTag")
    public void clbPaymentsShowDialog() {
        closeDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light));
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_alert_clb_paid_popup, null);

        FrameLayout frameOk = v.findViewById(R.id.frame_ok);
        frameOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                PrefsUtils.setString(context, Constant.PREF_KEY_CLB_PAID_YN, Constant.Y);
            }
        });

        builder.setView(v);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();


    }


    private void removeDeleteApp(String packageName) {
        int removeIndex = -1;
        for (int i = 0; i < vireusScanDataList.size(); i++) {
            VirusScanData vScanData = vireusScanDataList.get(i);
            if (packageName.equals(vScanData.getPackageName())) {
                removeIndex = i;
                break;
            }
        }

        vireusScanDataList.remove(removeIndex);
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

    }


    private void initUI() {
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setHasFixedSize(true);
        mCryptoResultListAdapter = new CryptoResultListAdapter(this, vireusScanDataList, mDetectedCount);
        mRecyclerView.setAdapter(mCryptoResultListAdapter);


        if (isDetected()) {
//            tvScanState.setText("RISK");
//            tvScanState.setTextColor(Color.WHITE);
//            tvScanResultDesc.setText(mDetectedCount + " malicious codes");
            frameTopbar.setBackgroundColor(0xFFFF1212);

        } else {
//            tvScanState.setText("SAFE");
//            tvScanState.setTextColor(Color.WHITE);
//            tvScanResultDesc.setText("No Threats detected.");
            frameTopbar.setBackgroundColor(0xFF29DB00);

        }
        ScanDetected.setScanDetectedCount(context, mDetectedCount);
    }

    private void refreshResultData(String packageName) {
        removeDeleteApp(packageName);
        updateDetectCount();
        mCryptoResultListAdapter.setData(vireusScanDataList, mDetectedCount);
        mCryptoResultListAdapter.notifyDataSetChanged();
        setScannedTopColorChanged();

        // cryptoscan 검출 count 저장
        ScanDetected.setScanDetectedCount(context, mDetectedCount);
    }

    private void updateDetectCount() {
        resetScanResult();

        for (int i = 0; i < vireusScanDataList.size(); i++) {
            VirusScanData vScanData = vireusScanDataList.get(i);
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
        Collections.sort(vireusScanDataList, new Comparator<VirusScanData>() {
            @Override
            public int compare(VirusScanData b1, VirusScanData b2) {
                return Integer.compare(b1.getOrder(), b2.getOrder());
            }
        });
    }


    private boolean isDetected() {
        return mDetectedCount > 0;
    }


    private int getScanTotalCount() {
        return mDetectedCount + mNoMatchCount + mUndetectedCount;
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
        Log.d(TAG, "onPackgeDeleted :" + pacakageName);
        refreshResultData(pacakageName);

    }
}
