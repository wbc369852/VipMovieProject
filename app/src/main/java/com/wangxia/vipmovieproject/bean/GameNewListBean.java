package com.wangxia.vipmovieproject.bean;


import java.util.List;

/**
 * 作者：吴冰川 on 2018/1/3 0003
 * 邮箱：mrwubingchuan@163.com
 * 说明：网侠游戏攻略列表数据
 */

public class GameNewListBean {

    public boolean success;
    public int pagesize;
    public int size;
    public int pagecount;
    public int curpage;
    public List<GameNewItemBean> items;
}
