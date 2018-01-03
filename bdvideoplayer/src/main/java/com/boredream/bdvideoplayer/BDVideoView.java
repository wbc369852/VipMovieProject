package com.boredream.bdvideoplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.boredream.bdvideoplayer.bean.IVideoInfo;
import com.boredream.bdvideoplayer.listener.OnVideoControlListener;
import com.boredream.bdvideoplayer.listener.SimplePlayerCallback;
import com.boredream.bdvideoplayer.utils.NetworkUtils;
import com.boredream.bdvideoplayer.utils.spUtil;
import com.boredream.bdvideoplayer.view.VideoBehaviorView;
import com.boredream.bdvideoplayer.view.VideoControllerView;
import com.boredream.bdvideoplayer.view.VideoProgressOverlay;
import com.boredream.bdvideoplayer.view.VideoSystemOverlay;

/**
 * 视频播放器View
 */
public class BDVideoView extends VideoBehaviorView {

    private SurfaceView mSurfaceView;
    private View mLoading;
    private VideoControllerView mediaController;
    private VideoSystemOverlay videoSystemOverlay;
    private VideoProgressOverlay videoProgressOverlay;
    private BDVideoPlayer mMediaPlayer;

    private int initWidth;
    private int initHeight;
    private boolean iszhixing = false;
    private int position = 0;
    private Context context;

    /**
     * 是否锁屏
     */
    public boolean isLock() {
        return mediaController.isLock();
    }

    public BDVideoView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BDVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public BDVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    //返回视频播放器各种控制键的实例,共外部设置
    public VideoControllerView getControllerView(){
        return mediaController;
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.video_view, this);

        mSurfaceView = (SurfaceView) findViewById(R.id.video_surface);
        mLoading = findViewById(R.id.video_loading);
        mediaController = (VideoControllerView) findViewById(R.id.video_controller);
        videoSystemOverlay = (VideoSystemOverlay) findViewById(R.id.video_system_overlay);
        videoProgressOverlay = (VideoProgressOverlay) findViewById(R.id.video_progress_overlay);

        initPlayer();
        //管理SurfaceView的生命周期
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            //SurfaceView对象被创建时触发,每次界面重现时都会执行
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initWidth = getWidth();
                initHeight = getHeight();
                //从后台切换回来会执行这里的,不能直接执行openVideo(),这个是直接重新开始播放
                if (mMediaPlayer != null) {
                    mMediaPlayer.setDisplay(holder);
                    //后台切换回来,这里如果不执行openVideo(),只能听到声音和正确的进度条,但没有画面了
//                    if (!iszhixing){
//                        mMediaPlayer.openVideo();
//                        iszhixing = true;
//                    }else{
//                        if(position > 0){
                    //在startPlayVideo()方法里,已经通过setvideopath执行过一次openVideo()了,
				 // 所以不要再执行了,只需要判断position即可--这样画面,声音都会显示了
                            mMediaPlayer.jixubofang(position);
                            position = 0;
//                        }
//                    }
                }
            }

            //SurfaceView的大小发生变化时触发；
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            //SurfaceView对象被销毁时触发；
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        //注册网络变化监听
        registerNetChangedReceiver();
    }

    private void initPlayer() {
        //是new出来,没有布局
        mMediaPlayer = new BDVideoPlayer();
        mMediaPlayer.setCallback(new SimplePlayerCallback() {

            @Override
            public void onStateChanged(int curState) {
                switch (curState) {
                    case BDVideoPlayer.STATE_IDLE:
                        am.abandonAudioFocus(null);
                        break;
                    case BDVideoPlayer.STATE_PREPARING:
                        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                        break;
                }
            }

            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaController.updatePausePlay();
            }

            @Override
            public void onError(MediaPlayer mp, int what, int extra) {
                mediaController.checkShowError(false);
            }

            @Override
            public void onLoadingChanged(boolean isShow) {
                if (isShow) showLoading();
                else hideLoading();
            }

            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
                mediaController.show();
                mediaController.hideErrorView();
            }
        });
        mediaController.setMediaPlayer(mMediaPlayer);
    }

    private void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mLoading.setVisibility(View.GONE);
    }

    private boolean isBackgroundPause;

    public void onStop() {
//        if (mMediaPlayer.isPlaying()) {
//            // 如果已经开始且在播放，则暂停同时记录状态
//            isBackgroundPause = true;
//            mMediaPlayer.pause();
//        }
        mMediaPlayer.pause();
        //不管是不是在播放,有可能暂停了再切到后台,都记录停止播放的位置(记录的肯定是不缓冲时才有用,缓冲为0了)
        position = mMediaPlayer.getCurrentPosition();
		//记录下状态--避免在缓冲时切换到后台,导致继续在后台播放---解决问题的核心做法(在loading时,切换到后台,还会继续播放,而进度条里还会继续执行一会儿,那么可以在里面停止播放即可)
        spUtil.putBoolean(context,"video_ishide",true);
    }

    public void onStart() {
		//没有隐藏了--设置状态为false,避免在进度条那里去暂停播放了
        spUtil.putBoolean(context,"video_ishide",false);
//        if (isBackgroundPause) {
        // 如果切换到后台暂停，后又切回来，则继续播放
        isBackgroundPause = false;
        mMediaPlayer.start();
        //纠正按钮显示错误,隐藏控件按钮
        mediaController.updatePausePlay();
        mediaController.hide();
//        }
    }

    public void onDestroy() {
        //停止播放并销毁
        mMediaPlayer.stop();
        mediaController.release();
        unRegisterNetChangedReceiver();
    }

    /**
     * 开始播放
     */
    public void startPlayVideo(final IVideoInfo video) {
        if (video == null) {
            return;
        }

        mMediaPlayer.reset();

        String videoPath = video.getVideoPath();
        mediaController.setVideoInfo(video);
        mMediaPlayer.setVideoPath(videoPath);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mediaController.toggleDisplay();
        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (isLock()) {
            return false;
        }
        return super.onDown(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (isLock()) {
            return false;
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    protected void endGesture(int behaviorType) {
        switch (behaviorType) {
            case VideoBehaviorView.FINGER_BEHAVIOR_BRIGHTNESS:
            case VideoBehaviorView.FINGER_BEHAVIOR_VOLUME:
                Log.i("DDD", "endGesture: left right");
                videoSystemOverlay.hide();
                break;
            case VideoBehaviorView.FINGER_BEHAVIOR_PROGRESS:
                Log.i("DDD", "endGesture: bottom");
                mMediaPlayer.seekTo(videoProgressOverlay.getTargetProgress());
                videoProgressOverlay.hide();
                break;
        }
    }

    @Override
    protected void updateSeekUI(int delProgress) {
        videoProgressOverlay.show(delProgress, mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
    }

    @Override
    protected void updateVolumeUI(int max, int progress) {
        videoSystemOverlay.show(VideoSystemOverlay.SystemType.VOLUME, max, progress);
    }

    @Override
    protected void updateLightUI(int max, int progress) {
        videoSystemOverlay.show(VideoSystemOverlay.SystemType.BRIGHTNESS, max, progress);
    }

    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        mediaController.setOnVideoControlListener(onVideoControlListener);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getLayoutParams().width = initWidth;
            getLayoutParams().height = initHeight;
        } else {
            getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
            getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
        }

    }

    private NetChangedReceiver netChangedReceiver;

    //网络改变监听
    public void registerNetChangedReceiver() {
        if (netChangedReceiver == null) {
            netChangedReceiver = new NetChangedReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            activity.registerReceiver(netChangedReceiver, filter);
        }
    }

    public void unRegisterNetChangedReceiver() {
        if (netChangedReceiver != null) {
            activity.unregisterReceiver(netChangedReceiver);
        }
    }

    private class NetChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Parcelable extra = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (extra != null && extra instanceof NetworkInfo) {
                NetworkInfo netInfo = (NetworkInfo) extra;

                if (NetworkUtils.isNetworkConnected(context) && netInfo.getState() != NetworkInfo.State.CONNECTED) {
                    // 网络连接的情况下只处理连接完成状态
                    return;
                }

                mediaController.checkShowError(true);
            }
        }
    }
}
