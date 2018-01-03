package com.wangxia.vipmovieproject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;

/**
 * 使用了AgentWeb组件的webview
 * 连settings都不用设置的,如果要设置额外的,也可以获取并设置
 * 我觉得这个组件的价值不在简洁,而可能在于支付,web安全,下拉刷新结合.如果不需要这些功能,不需要用此组件,否则是增大apk体积
 *
 * 等下结合下拉刷新用用看
 */
public class AgentWebActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ll_agentParent;
    private ImageView iv_agentweb_back;
    private TextView tv_agentweb_title;
    private String url;
    private AgentWeb mAgentWeb;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_agent_web);
        initView();
        initlistener();
        initdata();
    }
    private void initView() {
        url = getIntent().getStringExtra("param_url");
        //父布局,用来添加agentweb的
        ll_agentParent = (LinearLayout) findViewById(R.id.ll_agentParent);
        iv_agentweb_back = (ImageView) findViewById(R.id.iv_agentweb_back);
        tv_agentweb_title = (TextView) findViewById(R.id.tv_agentweb_title);
    }

    private void initlistener() {
        iv_agentweb_back.setOnClickListener(this);
    }

    private void initdata() {
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(ll_agentParent,new LinearLayout.LayoutParams(-1,-1) )//
                .useDefaultIndicator()//
                .defaultProgressBarColor()
                .setReceivedTitleCallback(mCallback)  //web显示标题回调
//                .setWebChromeClient(mWebChromeClient)  //自定义WebChromeClient回调处理需要处理的事件
//                .setWebViewClient(mWebViewClient)       //自定义WebViewClient回调处理需要处理的事件
//                .setSecutityType(AgentWeb.SecurityType.strict)
//                .setWebLayout(new WebLayout(this))
//                .openParallelDownload()//打开并行下载 , 默认串行下载
//                .setNotifyIcon(R.mipmap.download) //下载图标
//                .setOpenOtherAppWays(DefaultWebClient.OpenOtherAppWays.ASK)//打开其他应用时，弹窗质询用户前往其他应用
//                .interceptUnkownScheme() //拦截找不到相关页面的Scheme
                .createAgentWeb()//
                .ready()
                .go(url);
        mWebView = mAgentWeb.getWebCreator().get();
    }

    //标题回调
    private ChromeClientCallbackManager.ReceivedTitleCallback mCallback = new ChromeClientCallbackManager.ReceivedTitleCallback() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (tv_agentweb_title != null)
                tv_agentweb_title.setText(title);
        }
    };

    private WebViewClient mWebViewClient=new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
            Log.i("DDD","BaseWebActivity onPageStarted");
        }
    };
    private WebChromeClient mWebChromeClient=new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //进度
            Log.i("DDD","progress:"+newProgress);
        }
    };

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        //网站后退不会记住堆栈,所以只要回到首页,按后退就直接退出即可
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {  //退出
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_agentweb_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
