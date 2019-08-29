package com.cloudbric.cryptobric.contextwrapper;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Locale;

/**
 * 앱에서 언어를 저장하여 설정하기 위한 Wraapper
 * @author 유민호
 * @since 2018.07.04
 */
public class MyLanguageChangeWrapper extends ContextWrapper {

    /**
     * 생성자
     ************************************************************************************************************************************************/
    public MyLanguageChangeWrapper(Context context) {
        super(context);
    }

    /**
     * ContextWraapper
     ************************************************************************************************************************************************/
    public static ContextWrapper wrap(Context context, String language) {
        Configuration configuration = context.getResources().getConfiguration();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            setSystemLocale(configuration, locale);
        else
            setSystemLocaleLegacy(configuration, locale);

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        return new MyLanguageChangeWrapper(context);
    }

    /**
     * Set Setting Locale < Android OS 7.0
     ************************************************************************************************************************************************/
    @SuppressWarnings("deprecation")
    private static void setSystemLocaleLegacy(Configuration config, Locale locale) {
        config.locale = locale;
    }

    /**
     * Set Setting Locale >= Android OS 7.0
     ************************************************************************************************************************************************/
    @RequiresApi(Build.VERSION_CODES.N)
    private static void setSystemLocale(Configuration config, Locale locale) {
        config.setLocale(locale);
        config.setLayoutDirection(locale);
    }
}
