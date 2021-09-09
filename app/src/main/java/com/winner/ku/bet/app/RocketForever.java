package com.winner.ku.bet.app;

import android.net.Uri;
import android.webkit.ValueCallback;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class RocketForever {

    static int Code = 8888;
    static ValueCallback<Uri[]> CallBack;
    static Uri URL;
    static FirebaseRemoteConfig Firebase;

    public static void SetBase()
    {
        Firebase = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        Firebase.setDefaultsAsync(R.xml.rocket_url);
        Firebase.setConfigSettingsAsync(configSettings);
    }

    public static int getCode() {
        return Code;
    }

    public static void setCode(int code) {
        RocketForever.Code = code;
    }

    public static ValueCallback<Uri[]> getCallBack() {
        return CallBack;
    }

    public static void setCallBack(ValueCallback<Uri[]> callBack) {
        RocketForever.CallBack = callBack;
    }

    public static Uri getURL() {
        return URL;
    }

    public static void setURL(Uri URL) {
        RocketForever.URL = URL;
    }

    public static FirebaseRemoteConfig getFirebase() {
        return Firebase;
    }

    public static void setFirebase(FirebaseRemoteConfig firebase) {
        RocketForever.Firebase = firebase;
    }
}
