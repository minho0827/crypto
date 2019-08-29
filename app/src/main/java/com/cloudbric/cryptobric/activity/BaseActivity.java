package com.cloudbric.cryptobric.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.cloudbric.cryptobric.BaseApplication;
import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.data.UserData;
import com.cloudbric.cryptobric.listener.OnSingleClickListener;
import com.cloudbric.cryptobric.utils.AlertDialogUtil;
import com.cloudbric.cryptobric.utils.CommonUtil;
import com.cloudbric.cryptobric.utils.Constant;
import com.cloudbric.cryptobric.utils.ToastUtil;
import com.cloudbric.cryptobric.utils.UserDataManager;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    private com.cloudbric.cryptobric.data.UserData mUserData;
    protected UserDataManager userDataManager;

    /**
     * Top Area Layout
     ************************************************************************************************************************************************/
    protected AppCompatTextView tvCenterTitle;                                                                    // Top Area CenterTitle TextView
    protected AppCompatImageView ivBack;                                                // Top Area Image Back, Refresh, Completion
    protected RelativeLayout layoutBack, layoutRefresh;                                                           // Top Area Layout Common Left, Right


    /**
     * Application Back Key 관련
     ************************************************************************************************************************************************/
    protected boolean isBackButtonNotice = true;                                                                    // Back Key 눌렀을 때 App 종료 문구 출력 여부
    private boolean isBackPressed = false;                                                                          // Back Key 눌렀는지 여부


    /**
     * Application 전역 정보 인터페이스
     ************************************************************************************************************************************************/
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
//        ButterKnife.bind(this);
        // Application 전역 정보 인터페이스
        mContext = this;
        userDataManager = new UserDataManager(mContext);
        // Application User Local DataBase

        BaseApplication.setClassName(mContext.getClass().getCanonicalName());

        // Application StatusBar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (Constant.DEBUG_MODE)
            ToastUtil.showToastAsShort(mContext, "현재 디버그 모드입니다.");
    }


    /**
     * Get UserDataManager
     ************************************************************************************************************************************************/
    public UserDataManager getUserDataManager() {
        return userDataManager;
    }


    /**
     * Common UI 초기화 :: TopArea
     ************************************************************************************************************************************************/
    protected void uiInit() {
        tvCenterTitle = findViewById(R.id.txt_centerTitle);
        ivBack = findViewById(R.id.iv_back);
        layoutBack = findViewById(R.id.layout_back);
        layoutRefresh = findViewById(R.id.layout_refresh);

        if (!CommonUtil.isNetworkConnected(mContext)) {
            AlertDialogUtil.showSingleDialog(mContext, getString(R.string.msg_not_internet), new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    AlertDialogUtil.dismissDialog();
                    finish();
                    return;
                }
            });
        }
    }

    /**
     * Get UserData
     ************************************************************************************************************************************************/
    public UserData getUserData() {
        return mUserData;
    }

    /**
     * Configuration Get Locale :: High version
     ************************************************************************************************************************************************/
    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }


    /**
     * Configuration Get Locale :: Low version
     ************************************************************************************************************************************************/
    @SuppressWarnings("deprecation")
    public static Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    /**
     * 현재 Activity를 종료하지 않고 새로운 Activity를 start할 때 사용.
     ************************************************************************************************************************************************/
    protected void openActivity(Class<?> cls) {
        Intent intent = new Intent(mContext, cls);
        startActivity(intent);
    }


    /**
     * 현재 Activity를 종료하지 않고 새로운 Activity를 start할 때 사용하며 Intent와 함께 새로운 Activity를 실행함.
     ************************************************************************************************************************************************/
    protected void openActivityIntent(Class<?> cls, String key, String data) {
        Intent intent = new Intent(mContext, cls);
        intent.putExtra(key, data);
        startActivity(intent);
    }

}
