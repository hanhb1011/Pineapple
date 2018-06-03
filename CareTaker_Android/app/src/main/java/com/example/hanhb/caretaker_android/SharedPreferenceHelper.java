package com.example.hanhb.caretaker_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hanhb on 2018-06-01.
 */
/*
    local storage에 정보 저장

    1. 앱 첫 실행 시 유저를 식별하는 unique key 생성
    2. 그 뒤엔 SharedPreference로 확인 후, 생성하지 않음

    [key list]

 */

public class SharedPreferenceHelper {

    private SharedPreferenceHelper(){}

    public static boolean getBoolean(Context context, String key){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    public static void putBoolean(Context context, String key, boolean value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public static void putString(Context context, String key, String value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();

    }
    public static long getLong(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, 0);
    }

    public static void putLong(Context context, String key, long value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(key, value);
        editor.commit();

    }

    public static int getInt(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
    }

    public static void putInt(Context context, String key, int value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }


}