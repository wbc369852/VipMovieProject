package com.boredream.bdvideoplayer.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.boredream.bdvideoplayer.BDVideoPlayer;
import com.boredream.bdvideoplayer.R;
import com.boredream.bdvideoplayer.bean.IVideoInfo;
import com.boredream.bdvideoplayer.listener.OnVideoControlListener;
import com.boredream.bdvideoplayer.listener.SimpleOnVideoControlListener;
import com.boredream.bdvideoplayer.utils.DisplayUtils;
import com.boredream.bdvideoplayer.utils.NetStateAndFailDialog;
import com.boredream.bdvideoplayer.utils.StringUtils;
import com.boredream.bdvideoplayer.utils.spUtil;

/**
 * 视频控制器，可替换或自定义样式
 */
public class VideoControllerView extends FrameLayout {

    public static final int DEFAULT_SHOW_TIME = 3000;  //下面控制框显示3秒自动隐藏

    private View mControllerBack;
    private View mControllerTitle;
    private TextView mVideoTitle;
    private View mControllerBottom;
    private SeekBar mPlayerSeekBar;
    private ImageView mVideoPlayState;
    private TextView mVideoProgress;
    private TextView mVideoDuration;
    private ImageView mVideoFullScreen;
    private ImageView mScreenLock;
    private VideoErrorView mErrorView;

    private boolean isScreenLock;
    private boolean mShowing;
    private boolean mAllowUnWifiPlay;
    private BDVideoPlayer mPlayer;
    private IVideoInfo videoInfo;
    private OnVideoControlListener onVideoControlListener;
    private Context context;

    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        this.onVideoControlListener = onVideoControlListener;
    }

    public VideoControllerView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public VideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public VideoControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    /**
     * 初始化布局
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.video_media_controller, this);

        initControllerPanel();
    }

    private int mode = 0;
    public void setMode (int mode){
        //mode为1,让返回键和标题在小窗口播放时隐藏
        //initControllerPanel控件初始化比你这个方法执行的早,所以不会隐藏
        this.mode = mode;
        if (mControllerBack!= null && mControllerTitle != null){
            mControllerBack.setVisibility(View.GONE);
            mControllerTitle.setVisibility(View.GONE);
        }
    }

    private void initControllerPanel() {
        // back
        mControllerBack = findViewById(R.id.video_back);
        mControllerBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVideoControlListener != null) {
                    onVideoControlListener.onBack();
                }
            }
        });
        // top
        mControllerTitle = findViewById(R.id.video_controller_title);
        mVideoTitle = (TextView) mControllerTitle.findViewById(R.id.video_title);

        // bottom
        mControllerBottom = findViewById(R.id.video_controller_bottom);
        mPlayerSeekBar = (SeekBar) mControllerBottom.findViewById(R.id.player_seek_bar);
        //暂停/播放键
        mVideoPlayState = (ImageView) mControllerBottom.findViewById(R.id.player_pause);
        mVideoProgress = (TextView) mControllerBottom.findViewById(R.id.player_progress);
        mVideoDuration = (TextView) mControllerBottom.findViewById(R.id.player_duration);
        mVideoFullScreen = (ImageView) mControllerBottom.findViewById(R.id.video_full_screen);
        mVideoPlayState.setOnClickListener(mOnPlayerPauseClick);
        mVideoPlayState.setImageResource(R.drawable.ic_video_pause);
        mPlayerSeekBar.setOnSeekBarChangeListener(mSeekListener);
        mVideoFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVideoControlListener != null) {
                    onVideoControlListener.onFullScreen();
                }
            }
        });

        // lock
        mScreenLock = (ImageView) findViewById(R.id.player_lock_screen);
        //锁屏按钮的点击事件,隐藏和显示各种控件
        mScreenLock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScreenLock) unlock();  //锁屏了,点击就解锁
                else lock();
                show();   //根据锁屏的状态,show里控制各控件的显示和隐藏
            }
        });

        // error
        mErrorView = (VideoErrorView) findViewById(R.id.video_controller_error);
        mErrorView.setOnVideoControlListener(new SimpleOnVideoControlListener() {
            @Override
            public void onRetry(int errorStatus) {
                retry(errorStatus);
            }
        });

        mPlayerSeekBar.setMax(1000);
    }

    public void setMediaPlayer(BDVideoPlayer player) {
        mPlayer = player;
        updatePausePlay();
    }

    public void setVideoInfo(IVideoInfo videoInfo) {
        this.videoInfo = videoInfo;
        mVideoTitle.setText(videoInfo.getVideoTitle());
    }

    //点击视频时,是显示还是隐藏各种控件
    public void toggleDisplay() {
        if (mShowing) {
            hide();
        } else {
            show();
        }
    }

    public void show() {
        show(DEFAULT_SHOW_TIME);
    }

    //显示各种控件---需要根据不同状态下做不同处理
    public void show(int timeout) {
        setProgress();

        if (!isScreenLock) { //没锁屏,默认是false
            //设置了mode=1让返回和标题不显示,如果是1,但横屏时还是要显示的
            if (mode != 1 || !DisplayUtils.isPortrait(getContext())){
                mControllerBack.setVisibility(VISIBLE);
                mControllerTitle.setVisibility(VISIBLE);
            }
            mControllerBottom.setVisibility(VISIBLE);
            mVideoPlayState.setVisibility(VISIBLE);
        } else { //锁屏状态
            if (!DisplayUtils.isPortrait(getContext())){ //横屏
                mControllerBack.setVisibility(GONE);
            }
            mControllerTitle.setVisibility(GONE);
            mControllerBottom.setVisibility(GONE);
            mVideoPlayState.setVisibility(GONE);
        }

        if (!DisplayUtils.isPortrait(getContext())) { //横屏时,锁屏按钮可见
            mScreenLock.setVisibility(VISIBLE);
        }

        mShowing = true;

        updatePausePlay();

        post(mShowProgress);

        if (timeout > 0) {
            removeCallbacks(mFadeOut);
            postDelayed(mFadeOut, timeout);
        }
    }

    /**
     * 主动隐藏各种控制键
     * 在后台切换回来要调用一次,不然不会消失
     */
    public void hide() {
        if (!mShowing) {
            return;
        }

        if (!DisplayUtils.isPortrait(getContext())) { //横屏
            mControllerBack.setVisibility(GONE);
        }
        mControllerTitle.setVisibility(GONE);
        mControllerBottom.setVisibility(GONE);
        mVideoPlayState.setVisibility(GONE);
        mScreenLock.setVisibility(GONE);

        removeCallbacks(mShowProgress);

        mShowing = false;
    }

    private final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private boolean mDragging;
    private long mDraggingProgress;
    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
//            System.out.println("进度执行....");
            if (spUtil.getBoolean(context,"video_ishide",false)){
			//隐藏了,就赶快暂停,避免loading时切换到后台,导致还继续播放的问题
                mPlayer.pause();
                spUtil.putBoolean(context,"video_ishide",false);
                return;
            }
            int pos = setProgress();
            if (!mDragging && mShowing && mPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mPlayerSeekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mPlayerSeekBar.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mPlayerSeekBar.setSecondaryProgress(percent * 10);
        }

        mVideoProgress.setText(StringUtils.stringForTime(position));
        mVideoDuration.setText(StringUtils.stringForTime(duration));

        return position;
    }

    /**
     * 判断显示错误类型
	   这个方法是一直都在监听网络状态的,没网,网络切换都会执行这里
     */
    public void checkShowError(boolean isNetChanged) {
        Log.i("DDD","111111111111111");
        //网络连接类型
        boolean isConnect = NetStateAndFailDialog.isNetworkConnected(getContext());
        boolean isMobileNet = NetStateAndFailDialog.isMobileConnected(getContext());
        boolean isWifiNet = NetStateAndFailDialog.isWifiConnected(getContext());

        if (isConnect) {
            // 如果已经联网
            if (mErrorView.getCurStatus() == VideoErrorView.STATUS_NO_NETWORK_ERROR && !(isMobileNet && !isWifiNet)) {
                // 如果之前是无网络 ---应该提示“网络已经重新连上，请重试”，这里暂不处理
            }
            //这个方法是在setVideoInfo之前的,所以一开始的确是为null,但后面一直在监听执行,比如网络切换,没网络都会走这里
            //所以,还不能判断刚开始的网络状态,主要是流量播放状态
            else if (videoInfo == null) {
                // 优先判断是否有video数据
                showError(VideoErrorView.STATUS_VIDEO_DETAIL_ERROR);
            } else if (isMobileNet && !isWifiNet && !mAllowUnWifiPlay) {
                // 如果是手机流量，且未同意过播放，且非本地视频，则提示错误
                mErrorView.showError(VideoErrorView.STATUS_UN_WIFI_ERROR);
                mPlayer.pause();
            } else if (isWifiNet && isNetChanged && mErrorView.getCurStatus() == VideoErrorView.STATUS_UN_WIFI_ERROR) {
                // 如果是wifi流量，且之前是非wifi错误，则恢复播放
                playFromUnWifiError();
            } else if (!isNetChanged) {
                showError(VideoErrorView.STATUS_VIDEO_SRC_ERROR);
            }
        } else {
            mPlayer.pause();
            showError(VideoErrorView.STATUS_NO_NETWORK_ERROR);
        }
    }

    public void hideErrorView() {
        mErrorView.hideError();
    }

    private void reload() {
        mPlayer.restart();
    }

    public void release() {
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
    }

	/**
     * 对一些错误进行重试请求
     */
    private void retry(int status) {
        Log.i("DDD", "retry " + status);

        switch (status) {
            case VideoErrorView.STATUS_VIDEO_DETAIL_ERROR:
                // 传递给activity
                if (onVideoControlListener != null) {
                    onVideoControlListener.onRetry(status);
                }
                break;
            case VideoErrorView.STATUS_VIDEO_SRC_ERROR:
                reload();
                break;
            case VideoErrorView.STATUS_UN_WIFI_ERROR:
                allowUnWifiPlay();
                break;
            case VideoErrorView.STATUS_NO_NETWORK_ERROR: //没有网
                // 无网络时
                if (NetStateAndFailDialog.isNetworkConnected(getContext())) {
                    if (videoInfo == null) {
                        // 如果video为空，重新请求详情 ---重新请求详情就不需要了,会有监听隐藏
                        retry(VideoErrorView.STATUS_VIDEO_DETAIL_ERROR);
                    } else if (mPlayer.isInPlaybackState()) {
                        // 如果有video，可以直接播放的直接恢复
                        mPlayer.start();
                        //还要隐藏 -- 不然点击了的确是再继续播放,但错误框没有隐藏
                        hideErrorView();
                    } else {
                        // 视频未准备好，重新加载
                        reload();
                    }
                } else {
                    Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);

            mDragging = true;

            removeCallbacks(mShowProgress);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                return;
            }

            long duration = mPlayer.getDuration();
            mDraggingProgress = (duration * progress) / 1000L;

            if (mVideoProgress != null) {
                mVideoProgress.setText(StringUtils.stringForTime((int) mDraggingProgress));
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mPlayer.seekTo((int) mDraggingProgress);
            play();
            mDragging = false;
            mDraggingProgress = 0;

            post(mShowProgress);
        }
    };

    private void showError(int status) {
        mErrorView.showError(status);
        hide();

        // 如果提示了错误，则看需要解锁
        if (isScreenLock) {
            unlock();
        }
    }

    public boolean isLock() {
        return isScreenLock;
    }

    /**
     * 锁屏
     */
    private void lock() {
        Log.i("DDD", "lock");
        isScreenLock = true;
        mScreenLock.setImageResource(R.drawable.video_locked);
    }

    private void unlock() {
        Log.i("DDD", "unlock");
        isScreenLock = false;
        mScreenLock.setImageResource(R.drawable.video_unlock);
    }

    private void allowUnWifiPlay() {
        Log.i("DDD", "allowUnWifiPlay");

        mAllowUnWifiPlay = true;

        playFromUnWifiError();
    }

    private void playFromUnWifiError() {
        Log.i("DDD", "playFromUnWifiError");
        if (mPlayer.isInPlaybackState()) {
            mPlayer.start();
        } else {
            mPlayer.restart();
        }
    }

    private OnClickListener mOnPlayerPauseClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
        }
    };

    /**
     * 暂停和播放按钮,根据视频播放状态切换显示
     */
    public void updatePausePlay() {
        if (mPlayer.isPlaying()) { //播放中
            mVideoPlayState.setImageResource(R.drawable.ic_video_pause);
        } else {
            mVideoPlayState.setImageResource(R.drawable.ic_video_play);
        }
    }

    /**
     * 让视频暂停还是播放,也是根据视频播放状态
     */
    private void doPauseResume() {
        if (mPlayer.isPlaying()) { //正在播放
            pause();
        } else {
            play();
        }
    }

    private void pause() {
        mPlayer.pause();
        updatePausePlay();
        removeCallbacks(mFadeOut);
    }

    private void play() {
        mPlayer.start();
        show();
    }

    /**
     * 手机横竖屏切换时执行此方法
     */
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggleVideoLayoutParams();
    }

    //根据手机横竖屏状态执行
    void toggleVideoLayoutParams() {
        //获取当前屏幕是否是竖屏
        final boolean isPortrait = DisplayUtils.isPortrait(getContext());

        if (isPortrait) { //竖屏状态
            if (mode != 1){ //设置了mode=1让返回和标题不显示,默认是显示的,在外部不设置mode就会显示了
                mControllerBack.setVisibility(VISIBLE);
                mControllerTitle.setVisibility(VISIBLE);
            }else{
                mControllerBack.setVisibility(GONE);
                mControllerTitle.setVisibility(GONE);
            }
            mVideoFullScreen.setVisibility(View.VISIBLE);
            mScreenLock.setVisibility(GONE);
        } else { //横屏
            if (mode == 1){
                mControllerBack.setVisibility(VISIBLE);
                mControllerTitle.setVisibility(VISIBLE);
            }
            mVideoFullScreen.setVisibility(View.GONE);
            if (mShowing) {
                mScreenLock.setVisibility(VISIBLE);
            }
        }
    }

}
