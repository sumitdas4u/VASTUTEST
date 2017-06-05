package com.utsavmobileapp.utsavapp.service;

import android.app.Application;

import com.utsavmobileapp.utsavapp.BuildConfig;

import net.gotev.uploadservice.UploadService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Sumit on 12-08-2016.
 */
public class Initializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // setup the broadcast action namespace string which will
        // be used to notify upload status.
        // Gradle automatically generates proper variable as below.
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        // and then you can simply un-comment the following line. Read the wiki for more info.
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)
                .build();
        UploadService.HTTP_STACK = new OkHttpStack(client);


    }
}