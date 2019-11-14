package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;

import com.example.helloworld.service.CameraSurfaceView;
import com.example.helloworld.utils.Base64Util;
import com.example.helloworld.utils.BatyArry;
import com.example.helloworld.utils.BitmapUtil;
import com.example.helloworld.utils.FaceSearch;
import com.example.helloworld.utils.FileUtil;

import java.util.Map;

public class CameraActivity extends AppCompatActivity {
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        final CameraSurfaceView csfv = findViewById(R.id.yulan);
        csfv.setPreviewInterval(1);
        csfv.setOnBitmapGenerateListener(new CameraSurfaceView.OnBitmapGenerateListener() {
            @Override
            public void bitmapGenerate(byte[] data, final Bitmap bitmap, Camera.Size preSize) {
                if (flag) {
                    flag = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result = "noface";
//                            String result = FaceSearch.search(Base64Util.encode(BatyArry.decodeValue(bitmap)));
                            Map<String, Object> resultMap = FaceSearch.search(Base64Util.encode(BatyArry.decodeValue(bitmap)));
                            if (resultMap != null) {
                                result = String.valueOf(resultMap.get("result"));
                            }
                            assert result != null;
                            if (result.equals("undefined")) {
                                FileUtil.saveBitmap(BitmapUtil.rotateImage(bitmap, 270), getFilesDir().getAbsolutePath() + "picture.png");
                                Intent intent = CameraRegistActivity.newIntent(CameraActivity.this, "picture.png");
                                startActivity(intent);
                                finish();
                            } else if (!result.equals("noface")) {
                                String hello = "您好";
                                Map<String, Object> detectionResultMap = FaceSearch.detection(Base64Util.encode(BatyArry.decodeValue(bitmap)));
                                float beauty = Float.parseFloat(String.valueOf(detectionResultMap.get("beauty")));
                                if (result.equals("王总")) {
                                    beauty = 101;
                                }
                                if (detectionResultMap.get("gender").equals("male")) {
                                    if (beauty >= 0 && beauty < 10) {
                                        hello = "您好";
                                    } else if (beauty >= 10 && beauty < 20) {
                                        hello = "您好帅啊！";
                                    } else if (beauty >= 20 && beauty < 30) {
                                        hello = "您太帅了！";
                                    } else if (beauty >= 30 && beauty < 40) {
                                        hello = "您真是眉目疏朗！";
                                    } else if (beauty >= 40 && beauty < 50) {
                                        hello = "您真是玉树临风！";
                                    } else if (beauty >= 50 && beauty < 60) {
                                        hello = "您真是英俊潇洒！";
                                    } else if (beauty >= 60 && beauty < 70) {
                                        hello = "您真是风流倜傥！";
                                    } else if (beauty >= 70 && beauty < 80) {
                                        hello = "您真是品貌非凡！";
                                    } else if (beauty >= 80 && beauty < 90) {
                                        hello = "您真是惊才风逸！";
                                    } else if (beauty >= 90 && beauty < 100) {
                                        hello = "您真是逸群之才！";
                                    } else if (beauty == 101) {
                                        hello = "您简直是太帅了，五官清秀中带着一抹俊俏，帅气中又带着一抹温柔！您身上散发出来的气质好复杂，带有一丝懒散一丝坚毅，谜样般的男子啊，这，这根本就是童话中的白马王子嘛！";
                                    }
                                } else {
                                    if (beauty >= 0 && beauty < 10) {
                                        hello = "您好";
                                    } else if (beauty >= 10 && beauty < 20) {
                                        hello = "您好美啊！";
                                    } else if (beauty >= 20 && beauty < 30) {
                                        hello = "您太美了！";
                                    } else if (beauty >= 30 && beauty < 40) {
                                        hello = "您真是闭月羞花！";
                                    } else if (beauty >= 40 && beauty < 50) {
                                        hello = "您真是国色天香！";
                                    } else if (beauty >= 50 && beauty < 60) {
                                        hello = "您真是倾国倾城！";
                                    } else if (beauty >= 60 && beauty < 70) {
                                        hello = "您真是貌美如花！";
                                    } else if (beauty >= 70 && beauty < 80) {
                                        hello = "您真是天生丽质！";
                                    } else if (beauty >= 80 && beauty < 90) {
                                        hello = "您真是秀色可餐！";
                                    } else if (beauty >= 90 && beauty < 100) {
                                        hello = "您真是秀丽端庄！";
                                    }
                                }
                                String name = result + "," + hello+"\n"+ "您在" +detectionResultMap.get("expression")+ "," +"您好像在"+detectionResultMap.get("emotion");
                                Intent intent = yuyinChat.newIntent(CameraActivity.this, name);
                                startActivity(intent);
                                finish();
                            } else {
                                flag = true;
                            }
                        }
                    }).start();
                }
            }
        });

    }

}
