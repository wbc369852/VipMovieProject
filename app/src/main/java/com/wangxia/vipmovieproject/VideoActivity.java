package com.wangxia.vipmovieproject;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.boredream.bdvideoplayer.BDVideoView;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class VideoActivity extends AppCompatActivity {

    private BDVideoView videoView;
    private String vedioTitle;
    private String vedioUrl;
    private JCVideoPlayerStandard video_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_video);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        vedioTitle = getIntent().getStringExtra("VedioTitle");
        vedioUrl = getIntent().getStringExtra("VedioUrl");
        videoView = (BDVideoView) findViewById(R.id.bd_vedioView);
        video_view = (JCVideoPlayerStandard) findViewById(R.id.video);
    }

    private void initListener() {
    }

    private void initData() {
        video_view.setUp(String.valueOf(Uri.parse(vedioUrl)),vedioTitle);


//        final VideoDetailInfo info = new VideoDetailInfo();
//        info.title = vedioTitle;
//        info.videoPath ="http://www.qianx.wang/qianx.php?url=https://m.v.qq.com/x/page/j/j/u/j0025twqeju.html";
//
//        //设置监听回调
//        videoView.setOnVideoControlListener(new SimpleOnVideoControlListener() {
//            @Override
//            public void onRetry(int errorStatus) {
//                //调用业务接口重新获取数据
//                // get info and call method "videoView.startPlayVideo(info);"
//                //可根据错误类型重新加载 --- 必须要写,不然加载失败,则点击无效 --重新执行一起开始播放即可
//                videoView.startPlayVideo(info);
//            }
//            @Override
//            public void onBack() { //返回键
//                onBackPressed();
//            }
//            @Override
//            public void onFullScreen() { //全屏键
//                //切换屏幕
//                DisplayUtils.toggleScreenOrientation(VideoActivity.this);
//            }
//        });
//        videoView.startPlayVideo(info);
    }
//    /**
//     * 横竖屏切换
//     */
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        videoView.onStop();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        videoView.onStart();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        videoView.onDestroy();
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        if (!DisplayUtils.isPortrait(this)) { //横屏
//            if(!videoView.isLock()) {  //没锁屏,则切换方向
//                DisplayUtils.toggleScreenOrientation(this);
//            }
//        } else { //返回
////            hideSoftKeyBoard(); //隐藏软键盘
//            finish();
//            overridePendingTransition(0,0);
//        }
//    }
}
