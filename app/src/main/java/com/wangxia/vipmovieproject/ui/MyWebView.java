package com.wangxia.vipmovieproject.ui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 作者：吴冰川 on 2018/1/3 0003
 * 邮箱：mrwubingchuan@163.com
 * 说明：还是没有卵用,不能解决百度里滑动冲突的问题
 */

public class MyWebView extends WebView{
    private SwipeRefreshLayout swipeRefreshLayout;
    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSwipView(SwipeRefreshLayout swipeRefreshLayout){
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN: //下滑
//                if (this.getScrollY() <= 0){ //顶部了,启用swip
//                    swipeRefreshLayout.setEnabled(true);
//                }else{
//                    swipeRefreshLayout.setEnabled(false);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//
//                break;
//            default:
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.getScrollY() == 0){
            swipeRefreshLayout.setEnabled(true);
        }else {
            swipeRefreshLayout.setEnabled(false);
        }
    }
}
