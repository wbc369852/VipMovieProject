package com.wangxia.vipmovieproject;

/**
 * 作者：吴冰川 on 2017/12/15 0015
 * 邮箱：mrwubingchuan@163.com
 * 说明：项目中用到的常量
 */

public class CustomNormal {
    public static Integer[] webIcons = new Integer[]{
            R.drawable.qq,R.drawable.iqiyi,R.drawable.mgtv, R.drawable.pptv,R.drawable.souhu,R.drawable.baidu,
            R.drawable.book, R.drawable.live, R.drawable.douyu,R.drawable.panda,R.drawable.huya,R.drawable.longzhu,
            R.drawable.taobao,R.drawable.tmall,R.drawable.jd,R.drawable.vip,R.drawable.weibo,R.drawable.more
    };
    public static String[] webNames = new String[]{
            "腾讯","爱奇艺","芒果TV", "PPTV","搜狐","百度",
            "小说库","电视台", "斗鱼tv","熊猫tv","虎牙直播","龙珠直播",
            "淘宝","天猫","京东","唯品会","微博","更多"
    };

    public static String[] website = new String[]{
            "http://m.v.qq.com/","http://m.iqiyi.com/","http://m.mgtv.com/", "http://m.pptv.com/","https://m.tv.sohu.com","https://m.baidu.com/",
            "https://m.ranwena.com/?&falg=1", "http://m.zhiboba.org/dianshitai/?", "https://m.douyu.com","https://m.panda.tv","http://m.huya.com/", "http://m.longzhu.com/index",
            "https://m.taobao.com/","https://www.tmall.com/#/main","https://m.jd.com/","http://m.vip.com/","http://m.weibo.com/","http://www.sviphome.com/nav/index.html"
    };

    public static String[] newsTitle = new String[]{
//            "头条","社会","娱乐","军事","搞笑","体育","趣闻","健康","时尚","旅游"
            "游戏资讯","游戏评测","游戏视频","手游问答","新手攻略","高手进阶","游戏心得","辅助修改"
    };

    //资讯接口
    public static String NEW_LIST_SERVER = "http://btj.hackhome.com/app_api.asp?t=newlist&size=50&cl=";

    //文章接口 https://m.hackhome.com/app/a/442247.html?
    public static String ARTICLE = "https://m.hackhome.com/app/a/";

    public static String[] newsUrls = new String[]{
//            "http://www.sviphome.com/ad/qa.htm?eu=Xg1tVEUzX3QObTMQbQsNM1JeZA0yMlRlDjMcHAteCw1lUkRUX0YLMzIOY38af3BSDQQEMg0vVA5ECwQvM0QLVFIEDUVUDn5SM0YODTJRXzMLUVJJVHQOV2lLGhRwIiBXL04DZ3hkZVZ4SWNnbkt0FHgeY1Z3KGU%253D",
//            "http://www.sviphome.com/ad/qa.htm?eu=Xg1tVEUzX3QORmRUZBALUl5kDTIyVGUOMxwcC14LDWVSRFRfRgszMg5jfxp_cFINBAQyDS9UDkQLBC8zRAtUUgQNRVQOflIzRg4NMlFfMwtRUklUdA5XaUsaFHAiIFcvTgNneGRlVnhJY2duS3QUeB5jVncoZQ%253D%253D",
//            "http://www.sviphome.com/ad/qa.htm?eu=Xg1tVEUzX3QOdBBlVFJeZA0yMlRlDjMcHAteCw1lUkRUX0YLMzIOY38af3BSDQQEMg0vVA5ECwQvM0QLVFIEDUVUDn5SM0YODTJRXzMLUVJJVHQOV2lLGhRwIiBXL04DZ3hkZVZ4SWNnbkt0FHgeY1Z3KGU%253D",
//            "http://www.sviphome.com/ad/qa.htm?eu=Xg1tVEUzX3QODBAyRmQLUl5kDTIyVGUOMxwcC14LDWVSRFRfRgszMg5jfxp_cFINBAQyDS9UDkQLBC8zRAtUUgQNRVQOflIzRg4NMlFfMwtRUklUdA5XaUsaFHAiIFcvTgNneGRlVnhJY2duS3QUeB5jVncoZQ%253D%253D",
//            "http://www.sviphome.com/ad/qa.htm?eu=Xg1tVEUzX3QORQ0zFgsNM1JeZA0yMlRlDjMcHAteCw1lUkRUX0YLMzIOY38af3BSDQQEMg0vVA5ECwQvM0QLVFIEDUVUDn5SM0YODTJRXzMLUVJJVHQOV2lLGhRwIiBXL04DZ3hkZVZ4SWNnbkt0FHgeY1Z3KGU%253D",
//            "http://www.sviphome.com/ad/qa.htm?eu=Xg1tVEUzX3QObQt0EFJeZA0yMlRlDjMcHAteCw1lUkRUX0YLMzIOY38af3BSDQQEMg0vVA5ECwQvM0QLVFIEDUVUDn5SM0YODTJRXzMLUVJJVHQOV2lLGhRwIiBXL04DZ3hkZVZ4SWNnbkt0FHgeY1Z3KGU%253D",
//            "http://www.sviphome.com/ad/qa.htm?eu=Xg1tVEUzX3QOOBAPVDJSXmQNMjJUZQ4zHBwLXgsNZVJEVF9GCzMyDmN_Gn9wUg0EBDINL1QORAsELzNEC1RSBA1FVA5.UjNGDg0yUV8zC1FSSVR0DldpSxoUcCIgVy9OA2d4ZGVWeEljZ25LdBR4HmNWdyhl",
//            "http://www.sviphome.com/ad/qa.htm?eu=Xg1tVEUzX3QODAsNMkkNMkVSXmQNMjJUZQ4zHBwLXgsNZVJEVF9GCzMyDmN_Gn9wUg0EBDINL1QORAsELzNEC1RSBA1FVA5.UjNGDg0yUV8zC1FSSVR0DldpSxoUcCIgVy9OA2d4ZGVWeEljZ25LdBR4HmNWdyhl",
//            "http://www.sviphome.com/ad/qa.htm?eu=Xg1tVEUzX3QORmQLRmQNMkVSXmQNMjJUZQ4zHBwLXgsNZVJEVF9GCzMyDmN_Gn9wUg0EBDINL1QORAsELzNEC1RSBA1FVA5.UjNGDg0yUV8zC1FSSVR0DldpSxoUcCIgVy9OA2d4ZGVWeEljZ25LdBR4HmNWdyhl",
//            "http://www.sviphome.com/ad/qa.htm?eu=Xg1tVEUzX3QOZUR0MxBSXmQNMjJUZQ4zHBwLXgsNZVJEVF9GCzMyDmN_Gn9wUg0EBDINL1QORAsELzNEC1RSBA1FVA5.UjNGDg0yUV8zC1FSSVR0DldpSxoUcCIgVy9OA2d4ZGVWeEljZ25LdBR4HmNWdyhl"
            //换成网侠游戏攻略
            NEW_LIST_SERVER+246,
            NEW_LIST_SERVER+247,
            NEW_LIST_SERVER+261,
            NEW_LIST_SERVER+249,
            NEW_LIST_SERVER+245,
            NEW_LIST_SERVER+263,
            NEW_LIST_SERVER+244,
            NEW_LIST_SERVER+248,
    };
    //360搜索关键词
    public static String searchWords = "https://sug.so.360.cn/suggest?callback=suggest_so&encodein=utf-8&encodeout=utf-8&format=json&fields=word&word=";
}
