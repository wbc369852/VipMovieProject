/*
 * Tencent is pleased to support the open source community by making VasSonic available.
 *
 * Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 *
 */

package com.wangxia.vipmovieproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.wangxia.vipmovieproject.adapter.newsAdapter;
import com.wangxia.vipmovieproject.adapter.webGridViewAdapter;
import com.wangxia.vipmovieproject.bean.GameNewItemBean;
import com.wangxia.vipmovieproject.bean.GameNewListBean;
import com.wangxia.vipmovieproject.http.MyHttpClient;
import com.wangxia.vipmovieproject.http.myRequestCallBack;
import com.wangxia.vipmovieproject.util.MyToast;
import com.wangxia.vipmovieproject.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * main activity of this sample
 */
public class MainActivity extends AppCompatActivity {

    public static final int MODE_SONIC = 1;
    private GridView gv_webVideo;
    private SwipeRefreshLayout srf_main;
    private ListView mlistview;
    private RecyclerView rl_newsSelectTitle;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView lasttext;
    private newsAdapter listAdapter;
    private LinearLayout ll_home_search;
    private LinearLayout ll_huadong_yinc;
    public List<String> newstitelList = new ArrayList<>();
    private RecyclerView rl_yincangTitleSelect;
    private BaseRecyclerAdapter headerRecycleTitles;
    private int clickPosition = 0;
    private BaseRecyclerAdapter yinCangheaderRecycleTitles;
    private TextView yinclasttext;
    private int currentPositon = 1;
    private View newtitleView;
    private int newsHeight;
    private LinearLayout ll_headerTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        initView();
        initlistener();
        initdata();
    }

    private void initdata() {
        //网址导航设置数据适配器
        webGridViewAdapter gridAdapter = new webGridViewAdapter(this, CustomNormal.webIcons, CustomNormal.webNames);
        gv_webVideo.setAdapter(gridAdapter);
        //设置GridView的高度
        View itemview = View.inflate(this, R.layout.gridview_item,null);
        int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        itemview.measure(width,height);

        ViewGroup.LayoutParams params = gv_webVideo.getLayoutParams();
        params.height = (int) (itemview.getMeasuredHeight()*3.3);
        gv_webVideo.setLayoutParams(params);

        //头布局新闻标题选择
        Collections.addAll(newstitelList, CustomNormal.newsTitle); //直接把数组转成集合
        newstitleSelect();
        //隐藏的新闻标题
//        yinCangnewstitleSelect();
        //请求数据
        getdata(CustomNormal.newsUrls[0]);
    }

    private void getdata(String newsUrl) {
        //只要请求数据就显示刷新
        if (!srf_main.isRefreshing()){
            srf_main.setRefreshing(true);
        }
        MyHttpClient.get(newsUrl, new myRequestCallBack() {
            @Override
            public void onSuccess(String jsons) {
                if (srf_main!= null){
                    srf_main.setRefreshing(false);
                }
                pulldata(jsons);
            }
            @Override
            public void onFailure() {

            }
        });
    }

    private void pulldata(String jsons) {
        GameNewListBean newsdata = MyHttpClient.pulljsonData(jsons,GameNewListBean.class);
        if (newsdata == null || newsdata.items == null || newsdata.items.size() == 0){
            return;
        }
//        if (listAdapter == null){
//            listAdapter = new newsAdapter(this,newsdata.content);
//            mlistview.setAdapter(listAdapter);
//            mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    startBrowserActivity(listAdapter.newsList.get(position).url, CustomNormal.webNames[position]);
//                }
//            });
//        }else{
            listAdapter.gameArticleList = newsdata.items;
            listAdapter.notifyDataSetChanged();
//        }
    }

    private void initlistener() {
        //下拉刷新
        srf_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新当前页数据
                getdata(CustomNormal.newsUrls[clickPosition]);
            }
        });
        //设置开始自动刷新显示,数据加载完毕再次调用false即可
        srf_main.setRefreshing(true);
        //视频网站的点击事件 -- 把视频网址匹配字符传递过去,匹配到就显示播放器
        gv_webVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startBrowserActivity(CustomNormal.website[position],CustomNormal.webNames[position]);
            }
        });
        ll_home_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到搜索界面
//                Intent intent = ;
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
                overridePendingTransition(0,0);
            }
        });
        //滑动监听,当滑动多少距离然后隐藏
        mlistview.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int headerViewTopMargin;
            private View headerview;  //可以直接获取头布局实例的

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (yinCangheaderRecycleTitles == null || headerRecycleTitles == null){
//                    return;
//                }
                //这个判断非常重要,避免拉动过快,导致满足不了条件--滑动过快时,距离顶部的高度计算不准确
                if (mlistview.getFirstVisiblePosition() >= 1){ //说明头布局隐藏了(标题也在里面的)
                    if (currentPositon == 0){
                        ll_huadong_yinc.setVisibility(View.VISIBLE);
                        //第二种方式: 2个容器布局交换添加和去除标题滑动条子布局
                        if (ll_headerTitle.getChildCount() != 0){
                            ll_headerTitle.removeView(newtitleView);
                            ll_huadong_yinc.addView(newtitleView);
                        }
//                        yinCangheaderRecycleTitles.notifyDataSetChanged(); //只需更新适配器即可,更节约资源
//                        Log.i("DDD","111111111");
                    }
                }else{  //一进来就会执行这个的,第一个item为0
                    if (currentPositon > 0){
                        ll_huadong_yinc.setVisibility(View.GONE);
                        if (ll_huadong_yinc.getChildCount() != 0 ){
                            ll_huadong_yinc.removeView(newtitleView);
                            ll_headerTitle.addView(newtitleView);
                        }
//                        headerRecycleTitles.notifyDataSetChanged();//只需更新适配器即可
//                        Log.i("DDD","222222");
                    }
                }
                //巧妙处理,只让里面的逻辑只执行一次,否则就是一直执行的,非常消耗资源
                //你看---就直接判断listview滑动的item的位置,来显示隐藏就解决了问题,而不需要获取高度---前提是2个头布局
                //不完美之处---2个部分,滑动不一致 --  这种方法适合选项不多的,选项多的话就要考虑滑动位置一致了
                //(方法二:)解决---创建2处空布局作为容器,然后把新闻标题滑动条作为一个view来添加或移除即可
                //需要设置容器高度和滑动条一致即可
                currentPositon = mlistview.getFirstVisiblePosition();
            }
        });
        //先把适配器设置了,免得没有数据刷不出来界面
        listAdapter = new newsAdapter(this,kongnewsList);
        mlistview.setAdapter(listAdapter);
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >=2){ //有2个头布局,所以应该是从2开始
                    startBrowserActivity(CustomNormal.ARTICLE+listAdapter.gameArticleList.get(position-2).ID+".html?", null);
                }
            }
        });
    }
    List<GameNewItemBean> kongnewsList = new ArrayList<>();//空的,设置适配器用下

    private void initView() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorAccent));
        srf_main = (SwipeRefreshLayout) findViewById(R.id.srf_main);
        //自定义刷新颜色显示
        srf_main.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mlistview = (ListView) findViewById(R.id.lv_mainList);
        //搜索框
        ll_home_search = (LinearLayout) findViewById(R.id.ll_home_search);
        //隐藏的新闻标题容器
        ll_huadong_yinc = (LinearLayout) findViewById(R.id.ll_huadong_yinc);
//        rl_yincangTitleSelect = (RecyclerView) findViewById(R.id.rl_shuadongSelectTitle);
//        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        rl_yincangTitleSelect.setLayoutManager(mLayoutManager);

        //新闻标题布局--当做子布局--(第二种方式)
        newtitleView = View.inflate(this, R.layout.main_newtitle_recycler,null);
        //水平新闻标题
        rl_newsSelectTitle = (RecyclerView) newtitleView.findViewById(R.id.rl_newsSelectTitle);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rl_newsSelectTitle.setLayoutManager(mLayoutManager);
        //测量高度  -- 这里有点问题,无法赋值,可以直接给标题的item赋值一个高度就可以了
//        int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        newtitleView.measure(width,height);
//        //高度
//        newsHeight = newtitleView.getMeasuredHeight();
//        Log.i("DDD","高度= "+newsHeight);
        //赋值给容器高度
//        ViewGroup.LayoutParams params = ll_huadong_yinc.getLayoutParams();
//        params.height = newsHeight;
//        ll_huadong_yinc.setLayoutParams(params);

        addheader();
    }

    private void addheader() {
        //把头布局分成2个,便于滑动隐藏判断
        View header = View.inflate(this, R.layout.home_list_header, null);
        //视频网站导航
        gv_webVideo = (GridView) header.findViewById(R.id.gv_webVideo);
        mlistview.addHeaderView(header);

        //新闻标题头布局容器
        View header2 = View.inflate(this, R.layout.home_list_header2, null);
        //-- 容器不要用根布局的形式,否则拖动时去掉子布局,高度会变的,就算设置定值也不行
        ll_headerTitle = (LinearLayout) header2.findViewById(R.id.ll_headerTitle);

        //赋值给容器高度
//        ViewGroup.LayoutParams params = fl_newstitle.getLayoutParams();
//        params.height = newsHeight;
//        fl_newstitle.setLayoutParams(params);
        ll_headerTitle.addView(newtitleView);
        mlistview.addHeaderView(header2);
//        ll_huadong_yinc.addView(header2); //添加进来,看是否可以,然后只用控制ll_huadong_yinc的显示隐藏即可

    }

    /**
     * 头布局上的
     */
    private void newstitleSelect() {
//        if (headerRecycleTitles == null){  //对于这个需求而言,数据只需请求一次
            headerRecycleTitles = new BaseRecyclerAdapter<String>(this, newstitelList, R.layout.news_title_item) {
                @Override
                protected void convert(final BaseViewHolder helper, String newsTitle) {
                    //注意,这里想要记录状态,就不能写成全局变量替代
                    final TextView tv_news_titles = helper.getView(R.id.tv_news_title);
                    tv_news_titles.setText(newstitelList.get(helper.getPosition()));
                    if (helper.getPosition() == clickPosition){
                        tv_news_titles.setTextColor(Color.parseColor("#20acea"));
                        lasttext = tv_news_titles;
                    }else{
                        tv_news_titles.setTextColor(Color.parseColor("#666666"));
                    }

                    tv_news_titles.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("DDD","点击:"+clickPosition);
                            if (lasttext != null){
                                lasttext.setTextColor(Color.parseColor("#666666"));
                            }
                            tv_news_titles.setTextColor(Color.parseColor("#20acea"));
                            lasttext = tv_news_titles;
                            //刷新
                            clickPosition = helper.getPosition();
                            getdata(CustomNormal.newsUrls[clickPosition]);
                        }
                    });
                }
            };
            headerRecycleTitles.openLoadAnimation(false);
            rl_newsSelectTitle.setAdapter(headerRecycleTitles);
//        }else{
//            headerRecycleTitles.setData(newstitelList);
//        }
    }

//    /**
//     * 隐藏标题布局上的
//     */
//    private void yinCangnewstitleSelect() {
//        yinCangheaderRecycleTitles = new BaseRecyclerAdapter<String>(this, newstitelList, R.layout.news_title_item) {
//            @Override
//            protected void convert(final BaseViewHolder helper, String newsTitle) {
//                //注意,这里想要记录状态,就不能写成全局变量替代
//                final TextView tv_news_title = helper.getView(R.id.tv_news_title);
//                tv_news_title.setText(newstitelList.get(helper.getPosition()));
//                if (helper.getPosition() == clickPosition){
//                    tv_news_title.setTextColor(Color.parseColor("#20acea"));
//                    yinclasttext = tv_news_title;
//                }else{
//                    tv_news_title.setTextColor(Color.parseColor("#666666"));
//                }
//                tv_news_title.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.i("DDD","隐藏点击:"+clickPosition);
//                        if (yinclasttext != null){
//                            yinclasttext.setTextColor(Color.parseColor("#666666"));
//                        }
//                        tv_news_title.setTextColor(Color.parseColor("#20acea"));
//                        yinclasttext = tv_news_title;
//                        //刷新
//                        clickPosition = helper.getPosition();
//                        getdata(CustomNormal.newsUrls[clickPosition]);
//                    }
//                });
//            }
//        };
//        yinCangheaderRecycleTitles.openLoadAnimation(false);
//        rl_yincangTitleSelect.setAdapter(yinCangheaderRecycleTitles);
//    }


    /**
     * 根据url跳转浏览器
     * 根据网站名称设置视频页面匹配关键词
     */
    private void startBrowserActivity(String url, String webName) {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(BrowserActivity.PARAM_URL, url);
        if (webName != null){
            intent.putExtra("webName",webName);
        }
        intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
        startActivityForResult(intent, -1);
        overridePendingTransition(R.anim.enteranim_right_to_left,R.anim.exitanim_right_to_left);
    }

    private long exitTime = 0;
    @Override
    public void onBackPressed() {
        if((System.currentTimeMillis()-exitTime) > 3000){
            MyToast.safeShow(this,"再按一次离开网侠VIP浏览器", 2000);
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}
