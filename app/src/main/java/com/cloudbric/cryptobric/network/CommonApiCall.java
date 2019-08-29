package com.cloudbric.cryptobric.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.data.AppReportResponseData;
import com.cloudbric.cryptobric.data.UsertResponseData;
import com.cloudbric.cryptobric.listener.OnSingleClickListener;
import com.cloudbric.cryptobric.utils.AlertDialogUtil;
import com.cloudbric.cryptobric.utils.CommonUtil;
import com.cloudbric.cryptobric.utils.RetroUtil;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonApiCall {


    public static void postApplicationReport(Context context, String TAG, Map<String, String> callParams) {
        RestService service = RetroUtil.getService(RestService.class);
        final Call<AppReportResponseData> call = service.postApplicationReportList(callParams);
        call.enqueue(new Callback<AppReportResponseData>() {
            @Override
            public void onResponse(Call<AppReportResponseData> call, Response<AppReportResponseData> response) {
                if (response.isSuccessful()) {
                    final AppReportResponseData appReportResponseData = response.body();
                    if (appReportResponseData.isResult()) {
                        Log.d(TAG, "Application Report Success");
//                        Toast.makeText(context, "성공적으로 전송하였습니다.", Toast.LENGTH_SHORT).show();
                        AlertDialogUtil.appSendSuccessDialog(context, context.getString(R.string.msg_thankyou), null);
                    }

                } else {
                    Log.d(TAG, "Bad response code: " + response.code());
//                    Toast.makeText(context, "전송하는데 실패 하였습니다. 코드 " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AppReportResponseData> call, Throwable e) {
                Log.d(TAG, "Can't add app to user report", e);
//                Toast.makeText(context, "postApplicationReport network error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static void putUserInfo(Context context, String TAG, Map<String, String> callParams) {
        RestService service = RetroUtil.getService(RestService.class);
        final Call<UsertResponseData> call = service.putUserInfo(callParams);
        call.enqueue(new Callback<UsertResponseData>() {
            @Override
            public void onResponse(Call<UsertResponseData> call, Response<UsertResponseData> response) {
                if (response.isSuccessful()) {
                    final UsertResponseData usertResponseData = response.body();
                    if (usertResponseData.isResult()) {

                        Log.d(TAG, "user info Success");
                        Toast.makeText(context, "성공적으로 수정 하였습니다.", Toast.LENGTH_SHORT).show();
//                        AlertDialogUtil.showSingleDialog(context, "성공적으로 수정 하였습니다.", null);

                    }

                } else {
                    Log.d(TAG, "Bad response code: " + response.code());
//                    Toast.makeText(context, "수정하는데 실패 하였습니다. 코드 " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsertResponseData> call, Throwable e) {
                Log.d(TAG, "Can't modify user infomation", e);
//                Toast.makeText(context, "putUserInfo network error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static void postScanResultList(Context context, String TAG, Map<String, String> callParams) {
        RestService service = RetroUtil.getService(RestService.class);
        final Call<UsertResponseData> call = service.postScanResultList(callParams);
        call.enqueue(new Callback<UsertResponseData>() {
            @Override
            public void onResponse(Call<UsertResponseData> call, Response<UsertResponseData> response) {
                if (response.isSuccessful()) {
                    final UsertResponseData usertResponseData = response.body();

                    if (usertResponseData.isResult()) {

                    }

                    Log.d(TAG, "scan result send Success");
//                    Toast.makeText(context, "스캔 결과를 성공적으로 전송 하였습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d(TAG, "Bad response code: " + response.code());
//                    Toast.makeText(context, "스캔 결과 전송 실패 하였습니다. 코드 " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsertResponseData> call, Throwable e) {
                Log.d(TAG, "Can't modify user infomation", e);
//                Toast.makeText(context, "postScanResultList network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void postDevceInfo(Context context, String TAG, Map<String, String> callParams) {
        RestService service = RetroUtil.getService(RestService.class);
        final Call<UsertResponseData> call = service.postDevceInfo(callParams);
        call.enqueue(new Callback<UsertResponseData>() {
            @Override
            public void onResponse(Call<UsertResponseData> call, Response<UsertResponseData> response) {
                if (response.isSuccessful()) {
                    final UsertResponseData usertResponseData = response.body();
                    if (usertResponseData.isResult()) {

                        Log.d(TAG, "device send Success");
//                        Toast.makeText(context, "디바이스 정보를  성공적으로 전송 하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Bad response code: " + response.code());
//                    Toast.makeText(context, "디바이스 전송 실패 하였습니다. 코드 " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsertResponseData> call, Throwable e) {
                Log.d(TAG, "Can't send user device infomation", e);
//                Toast.makeText(context, "postDevceInfo network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getAppversionCheck(Context context, String TAG, String appId, String platform, String currentVersion) {
        RestService service = RetroUtil.getService(RestService.class);
        final Call<UsertResponseData> call = service.getAppversionCheck(appId, platform, currentVersion);
        call.enqueue(new Callback<UsertResponseData>() {
            @Override
            public void onResponse(Call<UsertResponseData> call, Response<UsertResponseData> response) {
                if (response.isSuccessful()) {
                    final UsertResponseData usertResponseData = response.body();

                    Log.d(TAG, "get Appversion  Success");
                    if ((usertResponseData.getResultInfo().getLatestVersion()).
                            equals(CommonUtil.getCurrentVersion(context))) {


                    } else {

                        AlertDialogUtil.showDoubleDialog(context, context.getString(R.string.msg_quest_patch), new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                AlertDialogUtil.dismissDialog();
                                try {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                                } catch (android.content.ActivityNotFoundException e) {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
                                }
                                ((Activity) context).finish();
                            }
                        }, new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                AlertDialogUtil.dismissDialog();
                                ((Activity) context).finish();
                            }
                        });

                    }


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



}
