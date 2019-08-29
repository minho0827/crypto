package com.cloudbric.cryptobric.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.data.UserData;
import com.cloudbric.cryptobric.utils.Constant;
import com.cloudbric.cryptobric.utils.PrefsUtils;
import com.cloudbric.cryptobric.utils.RequestCodeUtil;
import com.cloudbric.cryptobric.utils.UserDataManager;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.commons.lang3.StringUtils;

import static android.support.v4.app.NotificationCompat.BADGE_ICON_LARGE;


public class FitmanagerFcmService extends com.google.firebase.messaging.FirebaseMessagingService {

    /**
     * Application User Local DataBase
     ************************************************************************************************************************************************/
    private UserDataManager userDataManager;
    private UserData mUserData;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d("FitmanagerFcmService", "## onNewToken: " + s);

        userDataManager = new UserDataManager(getApplicationContext());
        mUserData = userDataManager.getUserData();

        // FCM Token 저장
        mUserData.setUserFirebaseToken(s);
        PrefsUtils.setString(getApplicationContext(), s, Constant.PREF_KEY_USER_FIREBASE_TOKEN);

//        memberManager.setMemberData(mMemberVO);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null) {
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    /**
     * Notification을 클라이언트에 전송합니다.
     ************************************************************************************************************************************************/
    private void sendNotification(String messageTitle, String messageBody) {
        userDataManager = new UserDataManager(getApplicationContext());
        mUserData = userDataManager.getUserData();

        PendingIntent pendingIntentSuccess = PendingIntent.getActivity(this, RequestCodeUtil.FCMReqCode.REQ_SEND_MESSAGE, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

        // Notification Manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification Id/Name
        String notification_id = getString(R.string.notification_id);
        CharSequence notification_name = getString(R.string.notification_name);

        // 알림 채널 숏컷 -> 당장 만들 필요는 없지만 알림 채널과 매니페스트등... 추가적으로 관리햐야한다.
        // Background에서 이미지를 받거나 데이터를 확인하고 싶은 경우 getTitle이 아닌 getData로 메세지를 받아야 한다(?).

        // 오레오 이상인 경우 알림 채널을 생성한다. 현재 알림 채널에 중요도는 기본이다. 헤드업 알림창을 사용 할 수 없다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (StringUtils.isEmpty(mUserData.getUserNotiChannel()) || StringUtils.equals(Constant.N, mUserData.getUserNotiChannel())) {
                NotificationChannel mChannel = new NotificationChannel(notification_id, notification_name, NotificationManager.IMPORTANCE_DEFAULT);
                mChannel.setDescription("fitmanager");
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200});
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                mChannel.setShowBadge(true);

                notificationManager.createNotificationChannel(mChannel);

                mUserData.setUserNotiChannel(Constant.Y);
                userDataManager.setUserData(mUserData);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, notification_id)
                    .setContentIntent(pendingIntentSuccess)
                    .setChannelId(notification_id)                                                          // 알림 채널 ID
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))                   // 알림 텍스트 많은 내용을 담는 Style
                    .setContentTitle(messageTitle)                                                          // 알림 타이틀
                    .setContentText(messageBody)                                                            // 알림 내용
                    .setWhen(System.currentTimeMillis())                                                    // 알림 시간
                    .setTicker(messageBody)                                                                 // 알림 잠깐 나오는 내용
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))    // 알림 큰 아이콘
                    .setSmallIcon(R.mipmap.ic_launcher)                                                     // 알림 작은 아이콘
                    .setBadgeIconType(BADGE_ICON_LARGE)                                                     // 알림 채널 아이콘
//                    .setFullScreenIntent(pendingIntentSuccess, true)                             // 알림 헤드업 (중요도가 높은 경우)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)                  // 알림 기본 설정
                    .setPriority(NotificationCompat.PRIORITY_MAX)                                           // 알림 중요도 설정
                    .setAutoCancel(true);                                                                   // 알림을 슬라이드 없애는 여부

            notificationManager.notify(RequestCodeUtil.FCMReqCode.REQ_SEND_MESSAGE, notificationBuilder.build());
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, notification_id)
                    .setContentIntent(pendingIntentSuccess)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))       // 알림 텍스트 많은 내용을 담는 Style
                    .setContentTitle(messageTitle)                                              // 알림 타이틀
                    .setContentText(messageBody)                                                // 알림 내용
                    .setWhen(System.currentTimeMillis())                                        // 알림 시간
                    .setTicker(messageBody)                                                     // 알림 잠깐 나오는 내용
                    .setSmallIcon(R.mipmap.ic_launcher)                                         // 알림 아이콘
//                    .setFullScreenIntent(pendingIntentSuccess, true)                 // 알림 헤드업 (중요도가 높은 경우)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)      // 알림 기본 설정
                    .setPriority(NotificationCompat.PRIORITY_MAX)                               // 알림 중요도 설정
                    .setChannelId(notification_id)                                              // 알림 채널
                    .setAutoCancel(true);                                                       // 알림을 슬라이드 없애는 여부

            notificationManager.notify(RequestCodeUtil.FCMReqCode.REQ_SEND_MESSAGE, notificationBuilder.build());
        }
    }
}
