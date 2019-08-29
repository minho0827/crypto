package com.cloudbric.cryptobric.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.data.UserData;
import com.cloudbric.cryptobric.utils.BackPressCloseHandler;
import com.cloudbric.cryptobric.utils.Constant;

import org.apache.commons.lang3.StringUtils;

import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {
    private final static String TAG = "CryptoAppListActivity";

    BackPressCloseHandler mBackPressCloseHandler;
    Context context;

    private UserData mUserData;                                                                                      // 유저 정보
    private AlertDialog dialog;

    AppCompatTextView tvToolbarTitle;
    FrameLayout frameFinish;
    TextView tvLanguage;
    LinearLayout linearInfomation;
    LinearLayout linearLanguage;
    FrameLayout frameLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;
        ButterKnife.bind(this);
        initUI();

        tvToolbarTitle.setText(getString(R.string.str_settings));
        mBackPressCloseHandler = new BackPressCloseHandler(this);
        mUserData = userDataManager.getUserData();
        setLanguage();

        frameLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageNotSupportDialog(mContext, getString(R.string.msg_comming_soon),
                        getString(R.string.msg_language_not_support));//                Intent intent = new Intent(context, LanguageSelectActivity.class);
//                startActivity(intent);
            }
        });
        frameFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        tvLanguage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showLanguageNotSupportDialog(mContext, getString(R.string.msg_comming_soon),
//                        getString(R.string.msg_language_not_support));
//            }
//        });

//        linearLanguage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(context, LanguageSelectActivity.class);
//                startActivity(intent);
//            }
//        });


        linearInfomation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MyInfomationActivity.class);
                startActivity(intent);
            }
        });
    }


    private void statusBarSetColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0xFFFFFFFF);


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
     * 언어 지원 불가 다이얼로그 		:: 메세지 1개 / 버튼 1개
     ************************************************************************************************************************************************/
    public void showLanguageNotSupportDialog(Context context, String title, String body) {

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
            }
        });

        frameOk.setVisibility(View.VISIBLE);

        builder.setView(v);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


    private void initUI() {
        frameFinish = findViewById(R.id.frame_finish);
        tvLanguage = findViewById(R.id.tv_language);
        linearInfomation = findViewById(R.id.linear_infomation);
        tvToolbarTitle = findViewById(R.id.toolbar_title);
        frameLanguage = findViewById(R.id.frame_language);
    }

    private void setLanguage() {

        /**
         * 언어 설정 초기화
         ************************************************************************************************************************************************/
        switch (mUserData.getUserLanguage()) {
            case Constant.K:
                tvLanguage.setText(R.string.str_korean);

                break;

            case Constant.E:
                tvLanguage.setText(R.string.str_english);
                break;

        }
    }
}