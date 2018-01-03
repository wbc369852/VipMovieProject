package com.wangxia.vipmovieproject.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangxia.vipmovieproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：吴冰川 on 2017/12/14 0014
 * 邮箱：mrwubingchuan@163.com
 * 说明：
 */

public class Util {
    //加载图
    public static void setNormalPicasoImage(Context mContext, ImageView imageview, int icon){
        //加个判断,避免地址为空,picasso加载出错
        Glide.with(mContext)
                .load(icon)
                .asBitmap()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.aa_photo_empty)  //加载时
                .error(R.drawable.aa_photo_empty) //加载出错时,可能地址传进去是null
                .into(imageview);
    }

    public static void setImage(Context mContext, ImageView imageview, String imgUrl){
        //加个判断,避免地址为空,picasso加载出错
        Glide.with(mContext)
                .load(imgUrl)
                .asBitmap()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.aa_photo_empty)  //加载时
                .error(R.drawable.aa_photo_empty) //加载出错时,可能地址传进去是null
                .into(imageview);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //获取当前时间
    public static String getStringNowTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

}
