package com.example.helloworld.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PostHttpRequest {
    public interface ResultHttpPost {
        void result(String resultStr);
    }

    private ResultHttpPost resultHttpPost;

    public void setResultHttpPost(ResultHttpPost resultHttpPost) {
        this.resultHttpPost = resultHttpPost;
    }

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            resultHttpPost.result(msg.obj.toString());
        }
    };
    final String[] arrStr = new String[1];

    //请求数据
    public void sendRequestPost(final String image) {
        new Thread() {
            @Override
            public void run() {
                // 请求url
                String urlString = "https://aip.baidubce.com/rest/2.0/face/v3/search";
                try {
                    URL url = new URL(urlString);
                    Map<String, Object> map = new HashMap<>();
                    map.put("image", image);
                    map.put("liveness_control", "NONE");
                    map.put("group_id_list", "group_repeat,group_233");
                    map.put("image_type", "BASE64");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setInstanceFollowRedirects(true); //有误重定向
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("access_token", "24.cb09fee2f21676909ffd242e25a9d57e.2592000.1566025877.282335-16834599");
                    OutputStream outputStream = connection.getOutputStream();
                    PrintStream printStream = new PrintStream(outputStream);
                    Gson gson = new Gson();
                    String pram = gson.toJson(map);
                    printStream.print(pram); //向服务器提交数据
                    //接受数据
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                        StringBuffer buffer = new StringBuffer();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) { //不为空进行操作
                            buffer.append(line);
                        }
                        arrStr[0] = buffer.toString();
                        Message msg = new Message();
                        msg.obj = arrStr[0];
                        handler.handleMessage(msg);
//                        handler.sendEmptyMessage(0x01); //向主线程返送
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
