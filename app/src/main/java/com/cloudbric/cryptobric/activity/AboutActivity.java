package com.cloudbric.cryptobric.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.utils.BackPressCloseHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {
    private final static String TAG = "CryptoAppListActivity";

    BackPressCloseHandler mBackPressCloseHandler;
    Context context;

    @Nullable
    @BindView(R.id.toolbar_title)
    AppCompatTextView tvToolbarTitle;
    @BindView(R.id.frame_finish)
    FrameLayout frameFinish;
    @BindView(R.id.tv_app_version)
    AppCompatTextView tvAppVersion;
    @BindView(R.id.tv_terms_of_use)
    AppCompatTextView tvTermsOfUse;
    @BindView(R.id.tv_privacy_policy)
    AppCompatTextView tvPrivacyPolicy;
    @BindView(R.id.tv_contact_us)
    AppCompatTextView tvContactUs;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        context = this;
        ButterKnife.bind(this);
        mBackPressCloseHandler = new BackPressCloseHandler(this);
        tvToolbarTitle.setText(getString(R.string.str_about));

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tvAppVersion.setText(version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tvPrivacyPolicy.setText(Html.fromHtml(getString(R.string.str_privacy_policy)));
        tvPrivacyPolicy.setTextColor(0xFF0c6cee);
        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPrivacyPolicyActivity();
            }
        });
        frameFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tvTermsOfUse.setText(Html.fromHtml(getString(R.string.str_terms_of_use)));
        tvTermsOfUse.setTextColor(0xFF0c6cee);
        tvTermsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callTermsOfUseActivity();
            }
        });


        tvContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet();
            }
        });

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


    public void callTermsOfUseActivity() {
        Intent intent = new Intent(this, TermsOfUseActivity.class);
        startActivity(intent);
    }

    public void callPrivacyPolicyActivity() {
        Intent intent = new Intent(this, PrivacyPolicyActivity.class);
        startActivity(intent);
    }

}