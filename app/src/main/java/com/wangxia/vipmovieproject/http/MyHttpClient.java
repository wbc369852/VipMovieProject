package com.wangxia.vipmovieproject.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

/**
 * 作者：吴冰川 on 2017/11/29 0029
 * 邮箱：mrwubingchuan@163.com
 * 说明：对okgo基本请求的一个封装
 */
public class MyHttpClient {
    private static Gson gson;

    static {  //静态代码块,初始化一些东西,比如要用到的gson
        if (gson == null){
            gson = new Gson();
        }
    }

    /**
     * 把json字符串传进去,返回解析后的javabean数据
     */
    public static <T> T pulljsonData(String jsons,Class<T> classOfT){
        T rrsult = null;
        try {
            rrsult = gson.fromJson(jsons, classOfT);
        } catch (JsonSyntaxException e){
            e.printStackTrace();
        }
        return rrsult;
    }

    /**
     * get请求---一般用的最多的就是这个
     */
    public static void get(String Url, final myRequestCallBack callback){
        OkGo.<String>get(Url)
//                .tag(context)  //主要用于取消请求,一般不需要
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //请求成功,直接返回解析出来的json数据
                        callback.onSuccess(response.body());
                    }
                    @Override
                    public void onError(Response<String> response) {
                        callback.onFailure();
                    }
                });

    }

}
