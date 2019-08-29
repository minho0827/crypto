package com.cloudbric.cryptobric.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.FrameLayout;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.network.CommonApiCall;
import com.cloudbric.cryptobric.utils.CommonUtil;
import com.cloudbric.cryptobric.utils.Constant;
import com.cloudbric.cryptobric.utils.PrefsUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyInfomationActivity extends AppCompatActivity {
    Context mContext;
    private static final String TAG = "MyInfomationActivity";

    AppCompatEditText mEtEmailAddress;
    AppCompatEditText mEtWalletAddress;
    AppCompatTextView tvModify;
    AppCompatTextView tvInfomationModified;
    String mStrEmailAddress;
    String mStrWalletAddress;


    @BindView(R.id.frame_finish)
    FrameLayout frameFinish;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_my_infomation);
        ButterKnife.bind(this);
        initUI();
        setUserInfomationData();
        mEtWalletAddress.clearFocus();
        frameFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvInfomationModified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet();
            }
        });


        tvModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    PrefsUtils.setString(mContext, Constant.PREF_KEY_EMAIL_ADDRESS, mEtEmailAddress.getText().toString());
                    PrefsUtils.setString(mContext, Constant.PREF_KEY_WALLET_ADDRESS, mEtWalletAddress.getText().toString());

                    Map<String, String> params = new HashMap<>();
                    params.put("uuid", CommonUtil.GetDevicesUUID(mContext));
                    params.put("user_email", mEtEmailAddress.getText().toString().trim());
                    params.put("user_wallet_address", mEtWalletAddress.getText().toString().trim());
                    CommonApiCall.putUserInfo(mContext, TAG, params);
                    mEtWalletAddress.clearFocus();

                } else {
//                    ToastUtil.showToastAsShort(mContext, "이메일 또는 지갑주소 형식을 확인해주세요.");
                }

            }
        });

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


    private void setUserInfomationData() {

        mStrEmailAddress = PrefsUtils.getString(mContext, Constant.PREF_KEY_EMAIL_ADDRESS, mEtEmailAddress.getText().toString());
        mStrWalletAddress = PrefsUtils.getString(mContext, Constant.PREF_KEY_WALLET_ADDRESS, mEtWalletAddress.getText().toString());

        if (StringUtils.isEmpty(mStrEmailAddress)
                || StringUtils.isEmpty(mStrWalletAddress)) {

        } else {

            mEtEmailAddress.setText(mStrEmailAddress);
            mEtWalletAddress.setText(mStrWalletAddress);


        }

    }



    private void bottomSheet() {
        String[] strArray = {"support@cloudbric.com"};

        composeEmail(strArray, " ");
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


    private void initUI() {
        tvInfomationModified = findViewById(R.id.tv_infomation_modified);

        mEtEmailAddress = findViewById(R.id.et_email_address);
        mEtWalletAddress = findViewById(R.id.et_wallet_address);
        tvModify = findViewById(R.id.tv_modify);
    }

}
