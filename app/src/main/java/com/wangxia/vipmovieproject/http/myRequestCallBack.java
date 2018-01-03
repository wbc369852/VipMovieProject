package com.wangxia.vipmovieproject.http;

/**
 * 作者：吴冰川 on 2017/11/9 0009
 * 邮箱：mrwubingchuan@163.com
 * 说明：请求结果回调接口
 */
public interface myRequestCallBack {
    /**
     * 请求成功回调事件处理,后面就是请求服务器要拿到的json字符串
     */
    void onSuccess(String jsons);

    /**
     * 请求失败回调事件处理,不需要处理.就提示请求失败就行了
     */
    void onFailure();
}
