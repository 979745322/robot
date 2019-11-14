package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MusicActicity extends AppCompatActivity {

    private TextView txv;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_acticity);
        txv = findViewById(R.id.et_content);  // 根据ID找到对应的text对象


    }

    public void bigger(View v) {     // 按钮对应的 onclick 响应

    }
    public void stop(View v) {     // 按钮对应的 onclick 响应
        mMediaPlayer.stop();
    }


}
