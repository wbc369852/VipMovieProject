package com.wangxia.vipmovieproject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.wangxia.vipmovieproject.bean.VedioUrlBean;
import com.wangxia.vipmovieproject.http.MyHttpClient;
import com.wangxia.vipmovieproject.http.myRequestCallBack;
import com.wangxia.vipmovieproject.util.MyToast;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * 使用了sonic快速浏览器框架的
 * 分5步走进行配置
 */
public class BrowserActivity extends Activity implements View.OnClickListener {

    public final static String PARAM_URL = "param_url";
    private SonicSession sonicSession;
    private WebView webView;
    private WebSettings settings;
    private SonicSessionClientImpl sonicSessionClient;
    private String url;
    private ImageView iv_web_back;
    private TextView tv_web_title;
    private ProgressBar web_progressBar;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout ll_tiaozhuan;
    private String webName = ""; //根据视频网站名来确定视频url匹配的关键词
    private boolean isvideoUrl;
    private boolean isfirstVideo = false;


    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        url = intent.getStringExtra(PARAM_URL);
        webName = intent.getStringExtra("webName");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //不要这个框架了,感觉真没什么太大的效果 -- 而且有时感觉让网页加载不出来
        // step 1: Initialize sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
        }
        sonicSessionClient = null;
        // step 2: Create SonicSession
        sonicSession = SonicEngine.getInstance().createSession(url,  new SonicSessionConfig.Builder().build());
        if (null != sonicSession) {
            sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
        } else {
            // this only happen when a same sonic session is already running,
            // u can comment following codes to feedback as a default mode.
            Log.i("DDD","create session fail!");
//            throw new UnknownError("create session fail!");
        }
        //布局初始化
        setContentView(R.layout.activity_browser);
        initview();
        intlistener();
        initdata();
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
        webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");
    }

    private void initview() {
        ll_tiaozhuan = (LinearLayout) findViewById(R.id.ll_tiaozhuan);
        webView = (WebView) findViewById(R.id.webview);
        //回到主页
        iv_web_back = (ImageView) findViewById(R.id.iv_web_back);
        tv_web_title = (TextView) findViewById(R.id.tv_web_title);
        tv_web_title.setText(url);
        web_progressBar = (ProgressBar) findViewById(R.id.web_progressBar);
        //下拉刷新效果控件
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshLayout);
        //自定义刷新颜色显示
        refreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        if (url.contains("baidu")){ //百度的都不刷新,其实只有视频才需要刷新
            refreshLayout.setEnabled(false);
        }
    }

    //监听参数
    @SuppressLint("SetJavaScriptEnabled")
    private void intlistener() {
        ll_tiaozhuan.setOnClickListener(this);
        iv_web_back.setOnClickListener(this);
        //得到webView的参数设置对象
        settings = webView.getSettings();
        //启用js功能---网页动态图等效果都要js
        settings.setJavaScriptEnabled(true); //支持动态页面,播放视频必须加
        settings.setPluginState(WebSettings.PluginState.ON); //支持视频播放
        // init webview settings
//        settings.setAllowContentAccess(true);
//        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(true);
        settings.setAppCacheEnabled(true);
//        settings.setSavePassword(false);
//        settings.setSaveFormData(false);
//        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.supportMultipleWindows();
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        //允许获取焦点
        webView.requestFocusFromTouch();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 一定要加,否则无法播放
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    web_progressBar.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    web_progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    web_progressBar.setProgress(newProgress);//设置进度值
                }
            }
        });

        //允许里面的点击链接事件
        // step 3: BindWebView for sessionClient and bindClient for SonicSession
        // in the real world, the init flow may cost a long time as startup
        // runtime、init configs....
        webView.setWebViewClient(new WebViewClient() {
            //覆写shouldOverrideUrlLoading实现内部显示网页
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //优酷点开后老加载失败,不是以http开头的
                //这里判断一定要以http开头的才去加载
                if (url.startsWith("http")){
                    view.loadUrl(url);
                }
//                Log.i("DDD","加载Url="+url);
                if (!TextUtils.isEmpty(webName) && getVedioKey(webName,url) ){ //&& !isvideoUrl
//                    MyToast.safeShow(BrowserActivity.this,"请下拉刷新观看视频",5000);
                    if (!isfirstVideo){ //第一次再次播放
                        isfirstVideo = true;
                        MyToast.safeShow(BrowserActivity.this,"视频加载中...若加载失败,请下拉刷新!",4000);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isvideoUrl){ //可能3秒过程中下拉刷新了
                                    getVIPVedio(webView.getTitle(),webView.getUrl());
                                }
                            }
                        },3000);
                    }
                }else{ //切到非视频播放地址,重置
                    isvideoUrl = false;
                    isfirstVideo = false;
                }
                //先让网页加载完毕,再去请求播放地址,不然,电视剧没办法选集
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (tv_web_title != null){
                    tv_web_title.setText(view.getTitle());
                }
                if (sonicSession != null) {
                    sonicSession.getSessionClient().pageFinish(url);
                }
                if (refreshLayout!= null){
                    refreshLayout.setRefreshing(false);
                }
                //这个地方不要立即去加载,否则会出问题,而且最好是第一次加载到播放器后,后面的都通过刷新操作来
                //但麻烦的是,点进来和选择剧集的url都含有cover,就一个x只差,先用刷新触发吧,没办法
                //这里只能设置一个时间间隔了,
//                Log.i("DDD","加载中url:"+url);
//                //这里是重复加载时,最开始加载之处-- 如果包含有视频地址,则直接跳转到安卓原生,否则继续webview加载
//                if (getVedioKey(webName,url)){ //跳转到原生播放界面,先用fl代替测试
//                    getVIPVedio(view.getTitle(),url);
//                }

                //是视频播放的,提示下拉刷新操作
                //isvideoUrl控制,以免操作时多次播放请求
//                if (!TextUtils.isEmpty(webName) && getVedioKey(webName,url) && !isvideoUrl){
//                    Toast.makeText(BrowserActivity.this,"请下拉刷新,解析视频播放地址!",Toast.LENGTH_LONG).show();
//                    isvideoUrl = true;
//                }else{ //不包含就设置为false,免得一直提示
//                    isvideoUrl = false;
//                }
            }
            @TargetApi(21)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (sonicSession != null) {
                    //step 6: Call sessionClient.requestResource when host allow the application
                    // to return the local data .
                    return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
                }
                return null;
            }
        });
        //下拉刷新 -- 可触发请求视频的操作
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                Log.i("DDD","加载中url:"+webView.getUrl()+"==>"+ Util.getStringNowTime());
                //这里是重复加载时,最开始加载之处-- 如果包含有视频地址,则直接跳转到安卓原生,否则继续webview加载
                if (!TextUtils.isEmpty(webName) && getVedioKey(webName,webView.getUrl())){ //跳转到原生播放界面,先用fl代替测试
                    refreshLayout.setRefreshing(false);
                    isvideoUrl = true;
                    getVIPVedio(webView.getTitle(),webView.getUrl());
                }else{ //重新加载页面
                    webView.reload();
                    isvideoUrl = false;
                    refreshLayout.setRefreshing(false);
                }
            }
        });
        //设置开始自动刷新显示,数据加载完毕再次调用false即可
        refreshLayout.setRefreshing(true);
    }

    //根据url来请求正确的播放地址
    private void getVIPVedio(final String title, final String url) {
//        Log.i("DDD","地址:"+"http://api.30pan.com/api/api.php?url=" + url);
        MyHttpClient.get("http://api.30pan.com/api/api.php?url=" + url, new myRequestCallBack() {
            @Override
            public void onSuccess(String jsons) {
                final VedioUrlBean bean = MyHttpClient.pulljsonData(jsons, VedioUrlBean.class);
                if (bean == null || bean.url == null){
                    return;
                }
                //重置
                isvideoUrl = false;
                isfirstVideo = false;
                //跳到另一个webveiw播放视频html
                Intent intent = new Intent(BrowserActivity.this,VideoWebActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //可能会多次跳转,则只保留一个
                intent.putExtra("webVideourl",bean.url);
                startActivity(intent);
                overridePendingTransition(0,0);

                //如果能解析到视频源URL地址,跳转到安卓里播放肯定是最好的
//                Intent intent = new Intent(BrowserActivity.this,VideoActivity.class);
//                intent.putExtra("VedioTitle",title);
//                intent.putExtra("VedioUrl",bean.url);
//                startActivity(intent);
//                overridePendingTransition(0,0);
            }

            @Override
            public void onFailure() {
                MyToast.safeShow(BrowserActivity.this,"视频地址解析失败,请下拉刷新重试!",5000);
            }
        });
    }

    //加载网页数据
    private void initdata() {
        // step 5: webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(webView);
            sonicSessionClient.clientReady();
        } else { // default mode
            webView.loadUrl(url);
        }
//        webView.loadUrl(url);
    }


    @Override
    public void onBackPressed() {
        //网站后退不会记住堆栈,所以只要回到首页,按后退就直接退出即可
        if (webView.canGoBack() && !TextUtils.equals(webView.getUrl(),URL)) {
//            Log.i("DDD","回退:"+webView.getUrl());
            webView.goBack();
//            if (isbofang){
//                isbofang = false;
//            }
        } else {  //退出
            finish();
            overridePendingTransition(R.anim.enteranim_left_to_right,R.anim.exitanim_left_to_right);
        }
    }

    @Override
    protected void onDestroy() {
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
        if (webView != null) {
            webView.stopLoading();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.removeAllViews();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            unregisterForContextMenu(webView);
            webView.destroy();
        }
        super.onDestroy();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (webView != null) {
//            webView.resumeTimers();
//            webView.onResume();
//            try {
//                webView.getClass().getMethod("onResume").invoke(webView, (Object[]) null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    @Override
//    protected void onPause() {
//        webView.reload();
//        super.onPause();
//        if (webView != null) {
//            webView.pauseTimers();
//            webView.onPause();
//        }
//        try {
//            webView.getClass().getMethod("onPause").invoke(webView, (Object[]) null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_web_back: //回到主页
                startActivity(new Intent(this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.enteranim_left_to_right,R.anim.exitanim_left_to_right);
                break;
            case R.id.ll_tiaozhuan:
                //跳转搜索
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("theurl",webView.getUrl());
                startActivity(intent);
                overridePendingTransition(0,0);
                break;
            default:
                break;
        }
    }

    /**
     * 有的视频还不止一个匹配词,用逗号分隔
     */
    private boolean getVedioKey(String webName,String url) {
        switch (webName) {
            //含有page是片段的意思,就不管了,而cover才是正片
            //还有一点,最好是点进去加载完整个页面后再去加载请求正确的播放地址
            //不然直接就匹配播放,那么电视剧怎么办? 无法选择剧集了
            case "腾讯":
                return (url.contains("cover"));
//            case "优酷":
//                return (url.contains("video"));
            case "爱奇艺":
                return (url.contains("/v_"));
            case "芒果TV":
                return (url.contains("/b/"));
            case "PPTV":
                return (url.contains("/show/"));
            case "搜狐":
                return (url.contains("aid") || url.contains("vid") );
        }
        return false;
    }
}
