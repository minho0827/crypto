package com.cloudbric.cryptobric.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.adapter.VerificationAdapter;
import com.cloudbric.cryptobric.data.CryptoData;
import com.cloudbric.cryptobric.data.UserData;
import com.cloudbric.cryptobric.listener.OnSingleClickListener;
import com.cloudbric.cryptobric.network.RestService;
import com.cloudbric.cryptobric.utils.AlertDialogUtil;
import com.cloudbric.cryptobric.utils.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerificationListActivity extends BaseActivity {
    private final static String TAG = "VerificationListActivity";
    private Context mContext;
    private RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    VerificationAdapter mVerificationAdapter;
    List<CryptoData.ResultInfo> mCryptoListDataList = new ArrayList<>();
    FrameLayout frameCryptoQuestion;
    FloatingActionButton mFab;

    FrameLayout frameFinish;
    AppCompatTextView toolbarTitle;


    private UserData mUserData;                                                                                      // 유저 정보


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_crypto_list);
        mContext = this;
        initUI();
        mRecyclerView = findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);


        requesConfirmCryptoList();




        //크립토앱 물음표 클릭시
        frameCryptoQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogUtil.showCryptoQuestion(mContext, getString(R.string.msg_certifiable_title),
                        getString(R.string.msg_certifiable_crypto), new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                AlertDialogUtil.dismissDialog();
                            }
                        });
            }
        });

        //크립토앱 제보시
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogUtil.showAppSendDialog(mContext, getString(R.string.msg_verification),
                        getString(R.string.msg_verification_body), new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                AlertDialogUtil.dismissDialog();
                                callMyApplicationListActivity();
                                recreate();
                            }

                        });
            }
        });

        frameFinish = findViewById(R.id.frame_finish);
        frameFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void statusBarSetColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


    }

    private void initUI() {
        mUserData = userDataManager.getUserData();
        mFab = findViewById(R.id.fab);
        frameCryptoQuestion = findViewById(R.id.frame_crypto_question);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Crypto Apps List");
    }


    private void callMyApplicationListActivity() {
        Intent intent = new Intent(mContext, MyApplicationListActivity.class);
        startActivity(intent);

    }


    private void requesConfirmCryptoList() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Constant.RK_LANGUAGE, mUserData.getUserLanguage());

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
                        mVerificationAdapter = new VerificationAdapter(getApplicationContext(), mCryptoListDataList);
                        mRecyclerView.setAdapter(mVerificationAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<CryptoData> call, final Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
