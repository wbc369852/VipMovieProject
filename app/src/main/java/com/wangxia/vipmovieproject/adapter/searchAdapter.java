package com.wangxia.vipmovieproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wangxia.vipmovieproject.R;
import com.wangxia.vipmovieproject.bean.SearchWordsBean;

import java.util.List;

/**
 * 作者：吴冰川 on 2017/12/18 0018
 * 邮箱：mrwubingchuan@163.com
 * 说明：
 */

public class searchAdapter extends BaseAdapter {
    public Context mcontext;
    public List<SearchWordsBean.ResultBean> datalist;
    public LayoutInflater mInflater;
    public searchAdapter(Context mcontext, List<SearchWordsBean.ResultBean> datalist) {
        this.mcontext = mcontext;
        this.datalist = datalist;
        mInflater = LayoutInflater.from(mcontext);
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    dataHolder holder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.word_search_item,null);
            holder = new dataHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (dataHolder) convertView.getTag();
        }
        //设置数据
        holder.tv_wordsearch.setText(datalist.get(position).word);
        return convertView;
    }

    private class dataHolder{

        private final TextView tv_wordsearch;

        public dataHolder(View view) {
            tv_wordsearch = (TextView) view.findViewById(R.id.tv_wordsearch);
        }
    }
}
