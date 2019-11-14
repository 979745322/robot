package com.example.helloworld.chat;

import com.example.helloworld.utils.GetHttpRequest;

public class Chat {
    GetHttpRequest getHR = new GetHttpRequest();

    public String chat(String s) {
//        try {
//            JSONObject obj = new JSONObject(getHR.get(s)); // 最外层的JSONObject对象
//            String result = obj.getString("content"); // 通过content字段获取其所包含的字符串
//            return result;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("错误信息", "返回的json格式出错");
//        }
//        return getHR.get(s);
        return "";
    }
}
