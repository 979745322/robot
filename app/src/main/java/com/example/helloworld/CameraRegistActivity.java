package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helloworld.utils.Base64Util;
import com.example.helloworld.utils.BatyArry;
import com.example.helloworld.utils.FaceSearch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CameraRegistActivity extends AppCompatActivity {

    ImageView photo;
    String imgName;
    Bitmap bitmap;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_regist);
        photo = findViewById(R.id.photo);
        userName = findViewById(R.id.userName);
        imgName = getIntent().getStringExtra("IMGNAME");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(getFilesDir().getAbsolutePath() + imgName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream(fis);
        photo.setImageBitmap(bitmap);
    }

    public static Intent newIntent(Context packageContext, String imgName) {
        Intent intent = new Intent(packageContext, CameraRegistActivity.class);
        intent.putExtra("IMGNAME", imgName);
        return intent;
    }

    public void reBack(View view) {
        Intent intent = new Intent(CameraRegistActivity.this, CameraFaceActivity.class);
        startActivity(intent);
        finish();
    }

    public void regist(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String resultStr = FaceSearch.add(Base64Util.encode(BatyArry.decodeValue(bitmap)), String.valueOf(userName.getText()));
                if (resultStr.equals("SUCCESS")) {
                    Intent intent = yuyinChat.newIntent(CameraRegistActivity.this, String.valueOf(userName.getText()));
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(CameraRegistActivity.this, CameraFaceActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }

}
