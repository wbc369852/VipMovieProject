package com.boredream.bdvideoplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 作者：吴冰川 on 2016/10/20 0020
 * 邮箱：mrwubingchuan@163.com
 * 说明：sp存值传值工具类,编码格式转换工具类
 */
public class spUtil {
    private static final String SHARE_PREFS_NAME = "config";
    public static void putString(Context ctx, String key, String value) {
        SharedPreferences pref = null;
        try {
            pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
                    Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //这里有极小可能出错
        if (pref == null || pref.edit() == null){
            return;
        }
        try {
            pref.edit().putString(key, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(Context ctx, String key, String defaultValue) {
        SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
                Context.MODE_PRIVATE);

        return pref.getString(key, defaultValue);
    }

    public static void putBoolean(Context ctx, String key, boolean value) {
        SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
                Context.MODE_PRIVATE);

        try {
            pref.edit().putBoolean(key, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getBoolean(Context ctx, String key,
                                     boolean defaultValue) {
        SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
                Context.MODE_PRIVATE);

        return pref.getBoolean(key, defaultValue);
    }

    public static void putInt(Context ctx, String key, int value) {
        SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
                Context.MODE_PRIVATE);

        try {
            pref.edit().putInt(key, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getInt(Context ctx, String key, int defaultValue) {
        SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
                Context.MODE_PRIVATE);

        return pref.getInt(key, defaultValue);
    }
}
