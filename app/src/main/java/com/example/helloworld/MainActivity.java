package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.helloworld.utils.GetHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private GetHttpRequest getHR = new GetHttpRequest();
    private TextView txv;
    private RecyclerView txv2;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txv = findViewById(R.id.et_content);  // 根据ID找到对应的text对象
        txv2 = findViewById(R.id.chat);  // 根据ID找到对应的text对象

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setReverseLayout(true);
        txv2.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(new ArrayList<Map<String, String>>(),this,txv2);
        txv2.setAdapter(chatAdapter);
    }

    public void bigger(View v) {     // 按钮对应的 onclick 响应
        getHR.setResultHttpGet(new GetHttpRequest.ResultHttpGet() {
            @Override
            public void result(final String resultStr) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj = new JSONObject(resultStr); // 最外层的JSONObject对象
                            String result = obj.getString("content"); // 通过content字段获取其所包含的字符串
                            Map<String,String> map = new HashMap<>();
                            map.put("type","1");
                            map.put("data",result);
                            chatAdapter.addData(map);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        getHR.get(String.valueOf(txv.getText()));
        Map<String,String> map = new HashMap<>();
        map.put("type","0");
        map.put("data",String.valueOf(txv.getText()));
        chatAdapter.addData(map);
        txv.setText("");
    }

}
