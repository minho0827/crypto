package com.cloudbric.cryptobric.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.contextwrapper.MyLanguageChangeWrapper;
import com.cloudbric.cryptobric.data.UserData;
import com.cloudbric.cryptobric.listener.OnSingleClickListener;
import com.cloudbric.cryptobric.utils.AlertDialogUtil;
import com.cloudbric.cryptobric.utils.Constant;

import butterknife.ButterKnife;

public class LanguageSelectActivity extends BaseActivity implements View.OnClickListener {


    /**
     * Application User Local DataBase
     ************************************************************************************************************************************************/
    private UserData mUserData;                                                                                      // 유저 정보

    /**
     * UI
     ************************************************************************************************************************************************/
    private ImageView ivSelectKorean, ivSelectEnglish;                                      // 체크박스 :: 한국어, 영어,
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);
        ButterKnife.bind(this);
        uiInit();
        settingUserLanguage();
        toolbarTitle.setText(R.string.str_language_select);

    }


    private void statusBarSetColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0xFFFFFFFF);


    }

    /**
     * View Resource Setting
     ************************************************************************************************************************************************/
    @Override
    protected void uiInit() {
        super.uiInit();

        // 뒤로가기 버튼 사용 조건
        isBackButtonNotice = false;

        // Application User Local DataBase
        mUserData = userDataManager.getUserData();

        RelativeLayout rlKorean = findViewById(R.id.rl_korean);
        RelativeLayout rlEnglish = findViewById(R.id.rl_english);
        FrameLayout frameFinish = findViewById(R.id.frame_finish);
        // UI resource
        ivSelectKorean = findViewById(R.id.iv_select_korean);
        ivSelectEnglish = findViewById(R.id.iv_select_english);
        toolbarTitle = findViewById(R.id.toolbar_title);
        // setOnClickListener
        rlKorean.setOnClickListener(this);
        rlEnglish.setOnClickListener(this);
        frameFinish.setOnClickListener(this);
    }

    /**
     * 언어 설정 초기화
     ************************************************************************************************************************************************/
    private void settingUserLanguage() {
        switch (mUserData.getUserLanguage()) {
            case Constant.K:
                ivSelectKorean.setBackgroundResource(R.drawable.img_checkbox_circle_on);
                ivSelectEnglish.setBackgroundResource(R.drawable.img_checkbox_circle_off);

                ivSelectKorean.setSelected(true);
                ivSelectEnglish.setSelected(false);
                break;

            case Constant.E:
                ivSelectKorean.setBackgroundResource(R.drawable.img_checkbox_circle_off);
                ivSelectEnglish.setBackgroundResource(R.drawable.img_checkbox_circle_on);

                ivSelectKorean.setSelected(false);
                ivSelectEnglish.setSelected(true);
                break;

        }
    }

    /**
     * 언어 설정 저장
     ************************************************************************************************************************************************/
    private void requestLanguage() {
        /**
         * 언어를 설정할지의 여부의 팝업을 띄워줍니다.
         * 설정하면 설정과 동시에 로그아웃 시켜줍니다.
         * 설정하지 않으면 팝업만 종료합니다.
         */
        AlertDialogUtil.showDoubleDialog(mContext, getString(R.string.msg_change_language), new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (ivSelectKorean.isSelected()) {
                    mUserData.setUserLanguage(Constant.K);
                    MyLanguageChangeWrapper.wrap(getBaseContext(), Constant.KOREAN_S);
                } else if (ivSelectEnglish.isSelected()) {
                    mUserData.setUserLanguage(Constant.E);
                    MyLanguageChangeWrapper.wrap(getBaseContext(), Constant.ENGLISH_S);
                }

                userDataManager.setUserData(mUserData);
                AlertDialogUtil.dismissDialog();

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AlertDialogUtil.dismissDialog();
            }
        });
    }


    /**
     * View ClickListener
     ************************************************************************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frame_finish:
                finish();
                break;


            case R.id.rl_korean:
                // 한국어 체크
                ivSelectKorean.setBackgroundResource(R.drawable.img_checkbox_circle_on);
                ivSelectEnglish.setBackgroundResource(R.drawable.img_checkbox_circle_off);

                ivSelectKorean.setSelected(true);
                ivSelectEnglish.setSelected(false);

                /**
                 * 같은 언어를 사용하는지 확인합니다.
                 * 언어를 선택함에 있어서 같은 언어를 사용중이라면 경고 팝업창을 띄워줍니다.
                 * 아니라면 선택할 언어를 설정할지 여부의 팝업을 띄워줍니다.
                 */
                if (mUserData.getUserLanguage().equals(Constant.K) && ivSelectKorean.isSelected()) {
                    AlertDialogUtil.showSingleDialog(mContext, getString(R.string.msg_current_already_language), new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            AlertDialogUtil.dismissDialog();
                        }
                    });
                    return;
                }

                if (mUserData.getUserLanguage().equals(Constant.E) && ivSelectEnglish.isSelected()) {
                    AlertDialogUtil.showSingleDialog(mContext, getString(R.string.msg_current_already_language), new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            AlertDialogUtil.dismissDialog();
                        }
                    });
                    return;
                }

                // 언어 변경 요청
                requestLanguage();
                break;

            case R.id.rl_english:
                // 영어 체크
                ivSelectKorean.setBackgroundResource(R.drawable.img_checkbox_circle_off);
                ivSelectEnglish.setBackgroundResource(R.drawable.img_checkbox_circle_on);

                ivSelectKorean.setSelected(false);
                ivSelectEnglish.setSelected(true);

                /**
                 * 같은 언어를 사용하는지 확인합니다.
                 * 언어를 선택함에 있어서 같은 언어를 사용중이라면 경고 팝업창을 띄워줍니다.
                 * 아니라면 선택할 언어를 설정할지 여부의 팝업을 띄워줍니다.
                 */
                if (mUserData.getUserLanguage().equals(Constant.K) && ivSelectKorean.isSelected()) {
                    AlertDialogUtil.showSingleDialog(mContext, getString(R.string.msg_current_already_language), new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            AlertDialogUtil.dismissDialog();
                        }
                    });
                    return;
                }

                if (mUserData.getUserLanguage().equals(Constant.E) && ivSelectEnglish.isSelected()) {
                    AlertDialogUtil.showSingleDialog(mContext, getString(R.string.msg_current_already_language), new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            AlertDialogUtil.dismissDialog();
                        }
                    });
                    return;
                }

                // 언어 변경 요청
                requestLanguage();
                break;
        }
    }
}
