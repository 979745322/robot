package com.example.helloworld.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 采用get方法进行对服务器的访问，以字符拼接的形式发送请求
 */
public class GetHttpRequest {
    public interface ResultHttpGet{
        void result(String resultStr);
    }

    private ResultHttpGet resultHttpGet;

    public void setResultHttpGet(ResultHttpGet resultHttpGet) {
        this.resultHttpGet = resultHttpGet;
    }

    private final String urlAddress = "https://api.qingyunke.com/api.php?key=free&appid=0&msg=";//服务器地址
    private String getUrl;
    final String[] arrStr = new String[1];
    @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            resultHttpGet.result(msg.obj.toString());
        }
    };
    public void get(String s) {

        getUrl = urlAddress + s;//构造getUrl
        new Thread(new Runnable() {
            @Override

            public void run() {
                try {
                    URL url = new URL(getUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();//开启连接
                    connection.connect();//连接服务器

                    if (connection.getResponseCode() == 200) {
                        //使用字符流形式进行回复
                        InputStream is = connection.getInputStream();
                        //读取信息BufferReader
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        StringBuffer buffer = new StringBuffer();
                        String readLine = "";
                        while ((readLine = reader.readLine()) != null) {
                            buffer.append(readLine);
                        }
                        is.close();
                        reader.close();
                        connection.disconnect();

                        Log.d("Msg", buffer.toString());
                        arrStr[0] = buffer.toString();

                    } else {
                        Log.d("TAG", "ERROR CONNECTED");
                    }
                    Message msg = new Message();
                    msg.obj = arrStr[0];
                    handler.handleMessage(msg);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
