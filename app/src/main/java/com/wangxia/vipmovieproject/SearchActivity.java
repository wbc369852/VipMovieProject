package com.wangxia.vipmovieproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wangxia.vipmovieproject.adapter.searchAdapter;
import com.wangxia.vipmovieproject.bean.SearchWordsBean;
import com.wangxia.vipmovieproject.http.MyHttpClient;
import com.wangxia.vipmovieproject.http.myRequestCallBack;

/**
 * 搜索界面
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView search_list;
    private EditText et_text;
    private ImageView iv_delete;
    private TextView tv_search;
    private String searchUrl;
    private searchAdapter listadapter;
    private String showurl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_search);
        initView();
        initLisener();
        initData();
    }

    private void initView() {
        showurl = getIntent().getStringExtra("theurl");
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorAccent));
        search_list = (ListView) findViewById(R.id.search_list);
        et_text = (EditText) findViewById(R.id.et_text);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        //搜索或取消
        tv_search = (TextView) findViewById(R.id.tv_search);
        // 获取编辑框焦点
        et_text.setFocusable(true);
//        et_text.setSelectAllOnFocus(true); //全选
        if (!TextUtils.isEmpty(showurl)){
            et_text.setText(showurl);
            et_text.setSelection(showurl.length());
        }
        if (TextUtils.isEmpty(et_text.getText().toString())){
            tv_search.setText("取消");
            iv_delete.setVisibility(View.GONE);
        }else{ //不为空
            tv_search.setText("搜索");
            iv_delete.setVisibility(View.VISIBLE);
        }
    }

    private void initLisener() {
        et_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    tv_search.setText("取消");
                    iv_delete.setVisibility(View.GONE);
                }else{ //不为空
                    tv_search.setText("搜索");
                    iv_delete.setVisibility(View.VISIBLE);
                }
                //当变化后请求联想词
//                if (!TextUtils.isEmpty(s.toString())){
                    String url = CustomNormal.searchWords+s.toString();
                    getsearchData(url);
//                }
            }
        });
        iv_delete.setOnClickListener(this);
        tv_search.setOnClickListener(this);
    }

    private void getsearchData(String url) {
//        Log.i("DDD","URL="+url);
        MyHttpClient.get(url, new myRequestCallBack() {
            @Override
            public void onSuccess(String jsons) {
                pullwordsSeach(jsons);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void pullwordsSeach(String jsons) {
        //只能是截取
//        Log.i("DDD","jsons1="+jsons.substring(12,jsons.length()-2));
        SearchWordsBean wordbean = MyHttpClient.pulljsonData(jsons.substring(12,jsons.length()-2),SearchWordsBean.class);
        if (wordbean == null || wordbean.result == null ||  wordbean.result.size() == 0){
            search_list.setVisibility(View.GONE);
            return;
        }
//        Log.i("DDD","jsons2="+jsons.substring(12,jsons.length()-2));
        search_list.setVisibility(View.VISIBLE);
        if (listadapter == null){
            listadapter = new searchAdapter(this,wordbean.result);
            search_list.setAdapter(listadapter);
            search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    finish();
                    startBrowserActivity("http://m.baidu.com/s?wd="+listadapter.datalist.get(position).word);
                }
            });
        }else{
            listadapter.datalist = wordbean.result;
            listadapter.notifyDataSetChanged();
        }

    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_delete:
                et_text.setText("");
                break;
            case R.id.tv_search:
                if (TextUtils.equals("取消",tv_search.getText().toString())){ //取消,则直接退出
                    onBackPressed();
                }else{
                    searchUrl = et_text.getText().toString();
                    //就是判断是否包含后缀就可以了,这里简单就列2个
                    if (searchUrl.contains("com") || searchUrl.contains("cn") ){
                        //跳转到浏览器完事
                        if (searchUrl.contains("http")){
                            startBrowserActivity(searchUrl);
                        }else{
                            startBrowserActivity("http://"+searchUrl);
                        }
                    }else{ //360搜索或百度搜索接口
                        searchUrl = "http://m.baidu.com/s?wd="+searchUrl;
                        startBrowserActivity(searchUrl);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);
    }

    /**
     * 根据url跳转浏览器
     */
    private void startBrowserActivity(String url) {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(BrowserActivity.PARAM_URL, url); //只传递过去url即可
//        intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //顶层
//        startActivityForResult(intent, -1);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}
