package com.wangxia.vipmovieproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangxia.vipmovieproject.R;
import com.wangxia.vipmovieproject.util.Util;

/**
 * 作者：吴冰川 on 2017/12/14 0014
 * 邮箱：mrwubingchuan@163.com
 * 说明：
 */

public class webGridViewAdapter extends BaseAdapter {

    public Context mcontext;
    public Integer[] webIconList;
    public String[] webnameList;
    public LayoutInflater mInflater;
    public webGridViewAdapter(Context mcontext,Integer[] webIconList,String[] webnameList) {
        this.mcontext = mcontext;
        this.webIconList = webIconList;
        this.webnameList = webnameList;
        mInflater = LayoutInflater.from(mcontext);
    }

    @Override
    public int getCount() {
        return webIconList.length;
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
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.gridview_item,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Util.setNormalPicasoImage(mcontext,holder.iv_webicon,webIconList[position]);
        holder.tv_webtitle.setText(webnameList[position]);
        return convertView;
    }

    private class ViewHolder{

        private final ImageView iv_webicon;
        private final TextView tv_webtitle;

        public ViewHolder(View view) {
            iv_webicon = (ImageView) view.findViewById(R.id.iv_webicon);
            tv_webtitle = (TextView) view.findViewById(R.id.tv_webtitle);
        }
    }
}
