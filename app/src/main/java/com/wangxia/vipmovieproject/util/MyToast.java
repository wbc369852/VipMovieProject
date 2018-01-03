package com.wangxia.vipmovieproject.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.wangxia.vipmovieproject.MyApp;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 安全吐司的重要性
 * Toast 工具类.简化Toast的使用，并且不用关心线程的问题。
 * Toast的初始化会创建默认构造Handler。Handler默认构造会使用当前线程Looper。如果没有，抛异常。
 * 调用了Looper.prepare和Looper.loop以后Toast才可以生效。但是子线程调用了loop方法就阻塞了。
 * 所以选择抛到主线程执行。
 *
 * @author Seny
 */
public class MyToast {
    private static Toast mToast;

    /**
     * 安全弹出Toast。处理线程的问题。
     * 传递毫秒值,自定义Toast显示时长,这样好些
     */
    public static void safeShow(final Context context, final String text, final int lengthShort) {
        if (Looper.myLooper() != Looper.getMainLooper()) {//如果不是在主线程弹出吐司，那么抛到主线程弹
            new Handler(Looper.getMainLooper()).post(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (context == null){
                                showToast(MyApp.context, text, lengthShort);
                            }else{
                                showToast(context, text, lengthShort);
                            }
                        }
                    }
            );
        } else {
            if (context == null){
                showToast(MyApp.context, text, lengthShort);
            }else{
                showToast(context, text, lengthShort);
            }
        }
    }

    /**
     * 弹出Toast，处理单例的问题。----如果是在主线程,可以用这个,子线程就不要用这个,不安全
     *
     * @param context
     * @param text
     * @param lengthShort
     */
    public static void showToast(Context context, String text, int lengthShort) {
        if (context == null){
            context = MyApp.context;
        }
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        }
//        mToast.setDuration(lengthShort); //设置时长,但只能设置LONG和SHORT,无法自定义
        mToast.setText(text);
        mToast.setGravity(Gravity.CENTER,0,0);//屏幕中间显示
        mToast.show();
        //自定义显示时长
        showMyToast(mToast,lengthShort);
    }

    //正常
    public static void showNormalToast(String text) {
//        if (context == null){
//            context = MyApp.context;
//        }
        if (mToast == null) {
            mToast = Toast.makeText(MyApp.context, text, Toast.LENGTH_LONG);
        }
        mToast.setDuration(Toast.LENGTH_LONG); //设置时长,但只能设置LONG和SHORT,无法自定义
        mToast.setText(text);
        mToast.setGravity(Gravity.CENTER,0,0);//屏幕中间显示
        mToast.show();
    }

    //注意:该方法创建Toast对象的时候时长因该设置为 Toast.LENGTH_LONG,因为该他的时长就是3秒,与下面的延时时间对应
    //cnt:需要显示的时长,毫秒
    private static void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 2000);//每隔三秒调用一次show方法;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //停止显示
                toast.cancel();
                timer.cancel();
            }
        }, cnt );//经过多长时间关闭该任务
    }
}
