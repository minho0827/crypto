package com.cloudbric.cryptobric.utils;

import com.cloudbric.cryptobric.BaseApplication;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroUtil {

    private static Retrofit retrofit;

    /**
     * Timeout
     ************************************************************************************************************************************************/
    private static final int RETROFIT_TIMEOUT = 5;                                                                 // 연결 제한 시간

    /**
     * OkHttp & Retrofit2 설정
     ************************************************************************************************************************************************/
    public static <T> T getService(final Class<T> service) {

        if (retrofit == null) {
            /**
             * okHttp3 LoggingInterceptor
             */
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            /**
             * OkHttp3 Bulider
             */
            OkHttpClient okHttpClient;

            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();


            okHttpClient = new OkHttpClient().newBuilder().addNetworkInterceptor(new StethoInterceptor())
                    .addInterceptor(new UserAgentInterceptor(BaseApplication.APP_NAME, BaseApplication.APP_VERSION))                                                                    // okHttp3 LoggingInterceptor
                    .connectTimeout(RETROFIT_TIMEOUT, TimeUnit.SECONDS)                                             // 기본 연결 제한 시간
                    .readTimeout(RETROFIT_TIMEOUT, TimeUnit.SECONDS)                                                // 쓰기 연결 제한 시간
                    .writeTimeout(RETROFIT_TIMEOUT, TimeUnit.SECONDS)                                               // 읽기 연결 제한 시간
                    .build();

            // Build

            /**
             * Retrofit2 Builder
             */
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.SERVER_ADDR)                                                                              // RequestApi
                    .client(okHttpClient)                                                                               // OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create(gson))                       // Gson Create
                    .build();                                                                                           // Build
        }

        return retrofit.create(service);
    }
}
