package com.wangxia.vipmovieproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangxia.vipmovieproject.R;
import com.wangxia.vipmovieproject.bean.GameNewItemBean;
import com.wangxia.vipmovieproject.util.Util;

import java.util.List;

/**
 * 作者：吴冰川 on 2017/12/15 0015
 * 邮箱：mrwubingchuan@163.com
 * 说明：
 */

public class newsAdapter extends BaseAdapter {
    public Context mcontext;
//    public List<ContentBean> newsList;
    public LayoutInflater mInflater;
//    private ContentBean contentBean;
    //调用网侠游戏文章吧,不然很多广告,体验不好
    public List<GameNewItemBean> gameArticleList;
    private GameNewItemBean gameArticle;

    public newsAdapter(Context mcontext, List<GameNewItemBean> gameArticleList) {
        this.mcontext = mcontext;
        this.gameArticleList = gameArticleList;
        mInflater = LayoutInflater.from(mcontext);
    }

    @Override
    public int getCount() {
        return gameArticleList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    ViewHolder holder;
    private String[] imageStrings;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.news_list_item,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        //data
        gameArticle = gameArticleList.get(position);
        //区分多张图
        imageStrings = gameArticle.piclist.split(",");
        if (imageStrings.length == 3){  //要么3张,要么一张
            holder.rl_news.setVisibility(View.GONE);
            holder.ll_duotu.setVisibility(View.VISIBLE);
            holder.tv_newstitle2.setText(gameArticle.title);
            holder.tv_newsSource2.setText(gameArticle.realtime);
            Util.setImage(mcontext,holder.iv_newsDuotu1,imageStrings[0]);
            Util.setImage(mcontext,holder.iv_newsDuotu2,imageStrings[1]);
            Util.setImage(mcontext,holder.iv_newsDuotu3,imageStrings[2]);
        }else{
            holder.rl_news.setVisibility(View.VISIBLE);
            holder.ll_duotu.setVisibility(View.GONE);
            holder.tv_newstitle.setText(gameArticle.title);
            holder.tv_newsSource.setText(gameArticle.realtime);
            Util.setImage(mcontext,holder.iv_newsimg,imageStrings[0]);
        }
//        if (contentBean.images.size() == 3){
//            holder.rl_news.setVisibility(View.GONE);
//            holder.ll_duotu.setVisibility(View.VISIBLE);
//            holder.tv_newstitle2.setText(contentBean.title);
//            holder.tv_newsSource2.setText(contentBean.source);
//            Util.setImage(mcontext,holder.iv_newsDuotu1,contentBean.images.get(0));
//            Util.setImage(mcontext,holder.iv_newsDuotu2,contentBean.images.get(1));
//            Util.setImage(mcontext,holder.iv_newsDuotu3,contentBean.images.get(2));
//        }else{ //单图
//            holder.rl_news.setVisibility(View.VISIBLE);
//            holder.ll_duotu.setVisibility(View.GONE);
//            holder.tv_newstitle.setText(contentBean.title);
//            holder.tv_newsSource.setText(contentBean.source);
//            Util.setImage(mcontext,holder.iv_newsimg,contentBean.images.get(0));
//        }
        return convertView;
    }

    private class ViewHolder{
        private final RelativeLayout rl_news;
        private final ImageView iv_newsimg;
        private final TextView tv_newstitle;
        private final TextView tv_newsSource;
        private final LinearLayout ll_duotu;
        private final TextView tv_newstitle2;
        private final ImageView iv_newsDuotu1;
        private final ImageView iv_newsDuotu2;
        private final ImageView iv_newsDuotu3;
        private final TextView tv_newsSource2;

        public ViewHolder(View view) {
            rl_news = (RelativeLayout) view.findViewById(R.id.rl_news);
            iv_newsimg = (ImageView) view.findViewById(R.id.iv_newsimg);
            tv_newstitle = (TextView) view.findViewById(R.id.tv_newstitle);
            tv_newsSource = (TextView) view.findViewById(R.id.tv_newsSource);
            //多图
            ll_duotu = (LinearLayout) view.findViewById(R.id.ll_duotu);
            tv_newstitle2 = (TextView) view.findViewById(R.id.tv_newstitle2);
            iv_newsDuotu1 = (ImageView) view.findViewById(R.id.iv_newsDuotu1);
            iv_newsDuotu2 = (ImageView) view.findViewById(R.id.iv_newsDuotu2);
            iv_newsDuotu3 = (ImageView) view.findViewById(R.id.iv_newsDuotu3);
            tv_newsSource2 = (TextView) view.findViewById(R.id.tv_newsSource2);
        }
    }
}
