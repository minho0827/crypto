package com.cloudbric.cryptobric.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.data.UserData;
import com.cloudbric.cryptobric.data.UsertResponseData;
import com.cloudbric.cryptobric.network.RestService;
import com.cloudbric.cryptobric.utils.RetroUtil;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TermsOfUseActivity extends BaseActivity {
    private Context mContext;
    private final static String TAG = "TermsOfUseActivity";
    AppCompatTextView tvToolbarTitle;

    AppCompatTextView tvTermsOfUse;

    FrameLayout frameFinish;

    UserData mUserData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_terms_of_use);
        mContext = this;
        initUI();
        mUserData = userDataManager.getUserData();
        getAboutData(this, TAG, "terms_of_use", mUserData.getUserLanguage());
        tvToolbarTitle.setText(R.string.title_terms_of_use);

        frameFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUI() {
        tvTermsOfUse = findViewById(R.id.tv_terms_of_use);
        frameFinish = findViewById(R.id.frame_finish);
        tvToolbarTitle = findViewById(R.id.toolbar_title);
    }


    public void getAboutData(Context context, String TAG, String type, String lang) {
        RestService service = RetroUtil.getService(RestService.class);
        final Call<UsertResponseData> call = service.getAboutData(type, lang);
        call.enqueue(new Callback<UsertResponseData>() {
            public void onResponse(Call<UsertResponseData> call, Response<UsertResponseData> response) {
                if (response.isSuccessful()) {
                    final UsertResponseData usertResponseData = response.body();

                    tvTermsOfUse.setText(usertResponseData.getResultInfo().getValue());

                } else {
                    Log.d(TAG, "Bad response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UsertResponseData> call, Throwable e) {
                Log.d(TAG, "Can't get Appversion ", e);
//                Toast.makeText(context, "getAppversionCheck network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void statusBarSetColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0xFFFFFFFF);


    }

}
