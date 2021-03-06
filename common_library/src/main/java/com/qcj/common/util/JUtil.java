package com.qcj.common.util;


import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 */
public class JUtil {

    /**
     * 解析从服务器拉下来的Json
     * 这个方法主要用于解析Response 中的数据，重点是data 下，并且将字符串型数据转化为Model 方便操作
     *
     * @param content 拉取下来的json 内容
     * @param cls     data下的数据形式，用于动态封装。
     * @return 封装过的 List 数据
     */
    public static <T> List<T> handleResponseList(final String content, Class<T> cls) throws Exception {
        List<T> list = JSON.parseArray(content, cls);
        return list;
    }

    /**
     * 解析从服务器拉下来的Json
     * 这个方法主要用于解析Response 中的数据，重点是data 下，并且将字符串型数据转化为Model 方便操作
     *
     * @param content 拉取下来的json 内容
     * @param cls     data下的数据形式，用于动态封装。
     * @return 解析后的object
     */
    public static <T extends Object> T handleResponseObject(final String content, Class<T> cls) throws Exception {
        T object = JSON.parseObject(content, cls);
        return object;
    }

    /**
     *
     * 对象专为jsonobject
     * @param o
     * @return
     */
    public static JSONObject object2Json(Object o){
        String s = JSON.toJSONString(o);
        try {
            return new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
