package com.wangxia.vipmovieproject.bean;

import java.util.List;

/**
 * 作者：吴冰川 on 2017/12/18 0018
 * 邮箱：mrwubingchuan@163.com
 * 说明：
 */

public class SearchWordsBean {

    /**
     * query : wode
     * ext : eci=ce3513bc61fdc476&nlpv=suggest_3.2.2
     * result : [{"word":"我的体育老师","resrc":"ori","source":""},{"word":"我的世界下载","resrc":"ori","source":""},{"word":"我的世界服务器","resrc":"ori","source":""},{"word":"我的钢铁网","resrc":"ori","source":""},{"word":"我的世界电脑版下载","resrc":"ori","source":""},{"word":"我的世界","resrc":"ori","source":""},{"word":"我的前半生","resrc":"ori","source":""},{"word":"我的世界小本","resrc":"ori","source":""},{"word":"我的体育老师电视剧","resrc":"ori","source":""},{"word":"我的世界中国版","resrc":"ori","source":""}]
     * version : 3.2.2
     */

    public String query;
    public String ext;
    public String version;
    public List<ResultBean> result; //列表

    public static class ResultBean {
        /**
         * word : 我的体育老师
         * resrc : ori
         * source :
         */
        public String word; //关键词
        public String resrc;
        public String source;

    }
}
