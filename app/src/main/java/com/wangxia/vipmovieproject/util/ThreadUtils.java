package com.wangxia.vipmovieproject.util;

import android.os.Handler;

/**
 * 作者：吴冰川 on 2016/12/7
 * 邮箱：mrwubingchuan@163.com
 * 说明：切换线程,处理子线程,主线程里不同业务的工具类
 */
public class ThreadUtils {
    /**
     * 要开启子线程,则很方便的调用
     * @param runnable
     */
    public static void runInThread(Runnable runnable){
        new Thread(runnable).start();
    }

    /**
     * 处理主线程里的内容,比如更新UI,用handler里的post即可,后面加时间就是延迟操作
     */
    public static Handler handler = new Handler();
    public static void runUIThread(Runnable r){
        handler.post(r);
    }

}