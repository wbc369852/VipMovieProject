package com.wangxia.vipmovieproject;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wangxia.vipmovieproject.util.StatusBarUtil;

/**
 * 专门用来播放视频html的
 */
public class VideoWebActivity extends AppCompatActivity {

    private FrameLayout video_fullView;
    private WebView webview;
    private WebSettings settings;
    private myWebChromeClient xwebchromeclient;
    private String webUrl;
    private ProgressBar pb_webprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_video_web);
        initView();
        initlistener();
        initdata();
    }

    private void initView() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.black));
        //获取传递过来的视频html地址
        webUrl = getIntent().getStringExtra("webVideourl");
        //webview布局
        video_fullView = (FrameLayout) findViewById(R.id.video_fullView);
        webview = (WebView) findViewById(R.id.video_webview);
        pb_webprogress = (ProgressBar) findViewById(R.id.pb_webprogress);
        webview.loadUrl(webUrl); //直接加载视频
//        MyToast.safeShow(this,"加载完成后,请点击播放按钮观看!\n"+"若无法播放,请返回刷新重试!",6000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(VideoWebActivity.this, "加载完成后,点击播放按钮观看!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);//屏幕中间显示
                toast.show();
            }
        },1500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(VideoWebActivity.this,"如果无法播放,请返回下拉刷新重试!",Toast.LENGTH_LONG).show();
                Toast toast = Toast.makeText(VideoWebActivity.this, "如果无法播放,请返回下拉刷新重试!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);//屏幕中间显示
                toast.show();
            }
        },5000);
    }

    private void initlistener() {
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //如果仅仅是视频播放,这是没有卵用的
                webview.loadUrl(url);
                return true; //返回true表示这个事件在这里已经处理了
            }

            /**
             * 处理ssl请求
             */
            @Override
            public void onReceivedSslError(WebView view,SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) { //加载完成,可以加一个缓冲
                super.onPageFinished(view, url);
                //执行自动播放 -- 前提是js里要能解析和写了autoplay
//                view.loadUrl("javascript:try{autoplay();}catch(e){}");
                pb_webprogress.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initdata() {
        //得到webView的参数设置对象
        settings = webview.getSettings();
        //启用js功能---网页动态图等效果都要js
        settings.setJavaScriptEnabled(true); //支持动态页面,播放视频必须加
        settings.setPluginState(WebSettings.PluginState.ON); //支持视频播放
//        web_moretopics.setWebChromeClient(new WebChromeClient()); //一定要加,否则无法播放

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //一些设置
        settings.setDomStorageEnabled(true);
//        settings.setSupportMultipleWindows(true);// 新加
        settings.setUseWideViewPort(true);  //屏幕适配
        xwebchromeclient = new myWebChromeClient();
        webview.setWebChromeClient(xwebchromeclient);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //手机返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                // webViewDetails.loadUrl("about:blank");
                hideCustomView();
                return true;
            } else {
                //不是全屏,按返回键就退出
//                web_moretopics.loadUrl("about:blank"); //加载空白页也可以
                finish();
                overridePendingTransition(0,0);
            }
        }
        return false;
    }

    /**
     * 判断是否是全屏
     *
     * @return
     */
    public boolean inCustomView() {
        return (xCustomView != null);
    }

    /**
     * 全屏时按返回键执行退出全屏方法
     */
    public void hideCustomView() {
        if (xwebchromeclient!=null){
            xwebchromeclient.onHideCustomView();
        }
//        else{
//            MyToast.showToast(this,"xwebchromeclient为空", Toast.LENGTH_SHORT);
//        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private View xCustomView;
    private WebChromeClient.CustomViewCallback xCustomViewCallback;
    private class myWebChromeClient extends WebChromeClient {
        private View xprogressvideo;
        private Bitmap xdefaltvideo;

        // 播放网络视频时全屏会被调用的方法
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            webview.setVisibility(View.GONE);
            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (xCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            video_fullView.addView(view);
            xCustomView = view;
            xCustomViewCallback = callback;
            video_fullView.setVisibility(View.VISIBLE);
        }

        // 视频播放退出全屏会被调用的
        @Override
        public void onHideCustomView() {
            if (xCustomView == null)// 不是全屏播放状态
                return;

            try {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                xCustomView.setVisibility(View.GONE);
                video_fullView.removeView(xCustomView);
                xCustomView = null;
                video_fullView.setVisibility(View.GONE);
                xCustomViewCallback.onCustomViewHidden();
                webview.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 视频加载时进程loading -- 不想要loading,可以不要,感觉不太好
        @Override
        public View getVideoLoadingProgressView() {
            if (xprogressvideo == null) {
                LayoutInflater inflater = LayoutInflater.from(VideoWebActivity.this);
                //视频加载布局
                xprogressvideo = inflater.inflate(R.layout.video_loading_progress, null);
            }
            return xprogressvideo;
        }

        //视频加载添加默认图标
//        @Override
//        public Bitmap getDefaultVideoPoster() {
//            //Log.i(LOGTAG, "here in on getDefaultVideoPoster");
//            if (xdefaltvideo == null) {
//                xdefaltvideo = BitmapFactory.decodeResource(getResources(), R.drawable.seach_icon);
//            }
//            return xdefaltvideo;
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //记得释放资源
        video_fullView.removeAllViews();
        if (webview != null) {
            webview.stopLoading();
            ((ViewGroup) webview.getParent()).removeView(webview);
            webview.removeAllViews();
            webview.setWebChromeClient(null);
            webview.setWebViewClient(null);
            unregisterForContextMenu(webview);
            webview.destroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webview != null) {
            webview.resumeTimers();
            webview.onResume();
        }
        //继续播放
        try {
            webview.getClass().getMethod("onResume").invoke(webview,(Object[])null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onPause() {
        webview.reload ();
        super.onPause();
        if (webview != null) {
            webview.pauseTimers();
            webview.onPause();
        }
        //停止播放视频
        try {
            webview.getClass().getMethod("onPause").invoke(webview,(Object[])null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
