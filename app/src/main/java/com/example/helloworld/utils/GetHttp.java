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

public class GetHttp {
    public interface ResultHttpGet{
        void result(String resultStr);
    }

    private GetHttpRequest.ResultHttpGet resultHttpGet;

    public void setResultHttpGet(GetHttpRequest.ResultHttpGet resultHttpGet) {
        this.resultHttpGet = resultHttpGet;
    }

    private String getUrl;
    final String[] arrStr = new String[1];
    @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            resultHttpGet.result(msg.obj.toString());
        }
    };
    public void get(final String urlAddress) {

        new Thread(new Runnable() {
            @Override

            public void run() {
                try {
                    URL url = new URL(urlAddress);
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
