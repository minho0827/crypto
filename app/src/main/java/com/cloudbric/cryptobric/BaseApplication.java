package com.cloudbric.cryptobric;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.bumptech.glide.Glide;
import com.cloudbric.cryptobric.data.ConfigData;

import net.danlew.android.joda.JodaTimeAndroid;

public class BaseApplication extends MultiDexApplication {
    private static volatile BaseApplication instance = null;
    private final static String TAG = "BaseApplication";
    public final static String APP_NAME = "cryptobric";
    private AppStatus appStatus = AppStatus.FOREGROUND;

    /**
     * 현재 사용되는 Class Name
     ************************************************************************************************************************************************/
    public static String CLASS_NAME;                                                                                // App 현재 Class Name
    public static String APP_VERSION;
    public static ConfigData mConfigData;
    public static BaseApplication mContext;
    /**
     * 예외처리 ExceptionHandler
     ************************************************************************************************************************************************/

    /**
     *
     */


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        JodaTimeAndroid.init(this);


    }
    public static Context getContext() {
        return mContext;
    }


    /**
    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    /**
     * 현재 사용되는 Class Name
     ************************************************************************************************************************************************/
    public static void setClassName(String className) {
        CLASS_NAME = className;
    }

    /**
     * /**
     * singleton 애플리케이션 객체를 얻는다.
     *
     * @return singleton 애플리케이션 객체
     */
    public static BaseApplication getGlobalApplicationContext() {
        if (instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    /**
     * MultiDexApplication Get Context
     ************************************************************************************************************************************************/
    public BaseApplication getBaseApplcationContext(Context context) {
        return (BaseApplication) context.getApplicationContext();
    }

    /**
     * Enum Class :: App Status
     ************************************************************************************************************************************************/
    public enum AppStatus {
        BACKGROUND, RETURNED_TO_FOREGROUND, FOREGROUND
    }

    /**
     * Get AppStatus
     ************************************************************************************************************************************************/
    public AppStatus getAppStatus() {
        return appStatus;
    }

    /**
     * ActivityLifecycleCallbacks
     ************************************************************************************************************************************************/
    private class MyActivityLifeCycleCallbacks implements ActivityLifecycleCallbacks {

        private int runningCount = 0;

        @Override
        public void onActivityStarted(Activity activity) {
            if (++runningCount == 1) {
                appStatus = AppStatus.RETURNED_TO_FOREGROUND;
            } else if (runningCount > 1) {
                appStatus = AppStatus.FOREGROUND;
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (--runningCount == 0) {
                appStatus = AppStatus.BACKGROUND;
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }
}
