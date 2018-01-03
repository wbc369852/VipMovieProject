package com.wangxia.vipmovieproject.bean;

import com.boredream.bdvideoplayer.bean.IVideoInfo;

/**
 * 作者：吴冰川 on 2017/12/23 0023
 * 邮箱：mrwubingchuan@163.com
 * 说明：
 */

public class VideoDetailInfo implements IVideoInfo {

    public String title;   //标题
    public String videoPath; //视频地址

    @Override
    public String getVideoTitle() {
        return title;
    }

    @Override
    public String getVideoPath() {
        return videoPath;
    }
}
