package com.cloudbric.cryptobric.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.adapter.MyAppListAdapter;
import com.cloudbric.cryptobric.data.CryptoData;
import com.cloudbric.cryptobric.data.LocalAppData;
import com.cloudbric.cryptobric.network.CommonApiCall;
import com.cloudbric.cryptobric.network.RestService;
import com.cloudbric.cryptobric.utils.CbProgressBar;
import com.cloudbric.cryptobric.utils.CommonUtil;
import com.cloudbric.cryptobric.utils.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplicationListActivity extends AppCompatActivity implements MyAppListAdapter.OnCheckedListener, MyAppListAdapter.SendApplication {
    private Context mContext;
    @Nullable
    @BindView(R.id.toolbar_title)
    AppCompatTextView tvToolbarTitle;
    MyAppListAdapter mMyApptListAdapter;
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_send)
    AppCompatTextView tvSend;
    private PackageManager mPacakageManager;
    @BindView(R.id.frame_finish)
    FrameLayout frameFinish;
    @BindView(R.id.frame_send)
    FrameLayout frameSend;
    List<CryptoData.ResultInfo> mCryptoListDataList = new ArrayList<>();


    public ApplicationInfo applicationInfo;
    private static CbProgressBar mProgressBar = new CbProgressBar();
    private final static String TAG = "MyAppListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_app_list);
        ButterKnife.bind(this);
        mPacakageManager = getPackageManager();
        tvToolbarTitle.setText("Application");
        mContext = this;
        requesConfirmCryptoList();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager pm = getPackageManager();
        List<PackageInfo> pkgAppsList = mContext.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
        List<LocalAppData> localAppDataList = new ArrayList<>();


        for (PackageInfo pack : pkgAppsList) {


            Intent intent = getPackageManager().getLaunchIntentForPackage(pack.packageName);
            if (intent != null) {
                Log.d("TAG", "| name    : " + pack.packageName);

                Log.d("TAG", "| package : " + pack.packageName);

                Log.d("TAG", "| version : " + pack.versionName);

                String label = pack.applicationInfo.loadLabel(getPackageManager()).toString();
                LocalAppData localAppData = new LocalAppData(pack);
                localAppData.setLabel(label);
                localAppDataList.add(localAppData);
            }
//            Arrays.sort(localAppDataList, LocalAppData.AppNameComparator);

        }

        Collections.sort(localAppDataList, new Comparator<LocalAppData>() {
            @Override
            public int compare(LocalAppData b1, LocalAppData b2) {
                return b1.getLabel().toUpperCase().compareTo(b2.getLabel().toUpperCase());
            }
        });

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mMyApptListAdapter = new MyAppListAdapter(this, localAppDataList);
        mRecyclerView.setAdapter(mMyApptListAdapter);


        frameFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        frameSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<LocalAppData> appList = mMyApptListAdapter.getSelectedItems();

                List<String> selectedItems = new ArrayList<>();
                for (LocalAppData localAppData : appList) {
                    selectedItems.add("[" + localAppData.getPackageInfo().applicationInfo.packageName + "," +
                            localAppData.getPackageInfo().applicationInfo.loadLabel(mPacakageManager) + "]");
                }

                Map<String, String> params = new HashMap<>();
                params.put("uuid", CommonUtil.GetDevicesUUID(mContext));
                params.put("contributed_apps", String.valueOf(selectedItems));
                CommonApiCall.postApplicationReport(mContext, TAG, params);

            }
        });

    }


    @Override
    public void onItemChecked() {
        List<LocalAppData> selectedList = mMyApptListAdapter.getSelectedItems();
        int selectedSize = selectedList.size();

        if (selectedSize == 0) {
            tvSend.setTextColor(Color.GRAY);
        } else {
            tvSend.setTextColor(0XFF0C6CEE);

        }


    }


    private void requesConfirmCryptoList() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Constant.RK_LANGUAGE, "en");

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.SERVER_ADDR)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestService service = retrofit.create(RestService.class);
        final Call<CryptoData> versionCheckVOCall = service.requeConfirmCryptoList(params);


        versionCheckVOCall.enqueue(new Callback<CryptoData>() {
            @Override
            public void onResponse(Call<CryptoData> call, Response<CryptoData> response) {

                Log.d(TAG, "onResponse: " + response.body());

                final CryptoData cryptoData = response.body();

                if (response.isSuccessful()) {
                    if (cryptoData.isResult()) {

                        mCryptoListDataList = cryptoData.getResultInfo();

//                        mVerificationAdapter = new VerificationAdapter(getApplicationContext(), mCryptoListDataList);
//                        mRecyclerView.setAdapter(mVerificationAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<CryptoData> call, final Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    public List<LocalAppData> getSelectedItems() {
        List<LocalAppData> scanList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(mCryptoListDataList)) {
            for (CryptoData.ResultInfo resultInfo : mCryptoListDataList) {
                if (resultInfo.getAppName().equals("")) {
//                    scanList.add(localAppData);


                }
            }
        }
        return scanList;

    }


    @Override
    public void SendApplication(List<String> appList) {

    }
}

