package com.example.helloworld.utils;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 人脸搜索
 */
public class FaceSearch {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static Map<String, Object>
    search(String image) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", image);
            map.put("liveness_control", "NORMAL");
            map.put("group_id_list", "user1");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = TokenService.getAuth();
            String result = HttpUtil.post(url, accessToken, "application/json", param);
            JSONObject json = new JSONObject(result);
            String jsonStr = json.getString("error_msg");
            if (jsonStr.equals("SUCCESS")) {
                JSONObject resultJson = new JSONObject(json.getString("result"));
                JSONArray user_list = new JSONArray(resultJson.getString("user_list"));
                JSONObject userJson = user_list.getJSONObject(0);
                String user_id = userJson.getString("user_id");
                String user_info = userJson.getString("user_info");
                Long score = userJson.getLong("score");
                if (score > 65) {
                    System.out.println("============================" + user_id);
                    System.out.println("============================" + user_info);
                    result = user_info;
                } else {
                    result = "undefined";
                }

//            }else if(jsonStr.equals("pic not has face")){
            } else {
                result = "noface";
            }

            System.out.println("============================" + result);
            resultMap.put("result", result);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 人脸检测
     *
     * @param image
     * @return
     */
    public static Map<String, Object> detection(String image) {
        // 请求url
        final String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", image);
            map.put("liveness_control", "NONE");
            map.put("image_type", "BASE64");
            map.put("max_face_num", "1");
            map.put("face_field", "age,gender,beauty,expression,emotion"); // 年龄，性别，美丑，微笑，表情

            String param = GsonUtils.toJson(map);

            String accessToken = TokenService.getAuth();

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println("============================" + result);
            JSONObject json = new JSONObject(result);
            String jsonStr = json.getString("error_msg");
            if (jsonStr.equals("SUCCESS")) {
                JSONObject resultJson = new JSONObject(json.getString("result"));
                JSONArray face_list = new JSONArray(resultJson.getString("face_list"));
                JSONObject userJson = face_list.getJSONObject(0);
                String location = userJson.getString("location");
                String age = userJson.getString("age");
                resultMap.put("age",age);
                String beauty = userJson.getString("beauty");
                resultMap.put("beauty",beauty);
                JSONObject expressionJson = userJson.getJSONObject("expression");
                String expression = expressionJson.getString("type");
                String expressionTran = expression;
                if(expression.equals("none")){
                    expressionTran = "没有微笑";
                }else if(expression.equals("smile")){
                    expressionTran = "在微笑";
                }else if(expression.equals("laugh")){
                    expressionTran = "在大笑";
                }
                resultMap.put("expression",expressionTran);
                JSONObject emotionJson = userJson.getJSONObject("emotion");
                String emotion = emotionJson.getString("type");
                String emotionTran = emotion;
                if(emotion.equals("angry")){
                    emotionTran = "愤怒";
                }else if(emotion.equals("disgust")){
                    emotionTran = "厌恶";
                }else if(emotion.equals("fear")){
                    emotionTran = "恐惧";
                }else if(emotion.equals("happy")){
                    emotionTran = "高兴";
                }else if(emotion.equals("sad")){
                    emotionTran = "伤心";
                }else if(emotion.equals("surprise")){
                    emotionTran = "惊讶";
                }else if(emotion.equals("neutral")){
                    emotionTran = "无聊";
                }
                resultMap.put("emotion",emotionTran);
                JSONObject genderJson = userJson.getJSONObject("gender");
                String gender = genderJson.getString("type");
                resultMap.put("gender",gender);
                System.out.println("============================" + location);
            }

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 人脸注册
     *
     * @return
     */
    public static String add(String image, String userName) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", image);
            map.put("group_id", "user1");
            map.put("user_id", simpleDateFormat.format(date));
            map.put("user_info", userName);
            map.put("image_type", "BASE64");

            String param = GsonUtils.toJson(map);

            String accessToken = TokenService.getAuth();

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            JSONObject json = new JSONObject(result);
            String jsonStr = json.getString("error_msg");
            if (jsonStr.equals("SUCCESS")) {
                result = "SUCCESS";
            }
            System.out.println("============================" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 活体检测
     *
     * @param image
     * @return
     */
    public static String faceVerify(String image) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceverify";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", image);
            map.put("image_type", "BASE64");

            String param = GsonUtils.toJson(map);

            String accessToken = TokenService.getAuth();

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}