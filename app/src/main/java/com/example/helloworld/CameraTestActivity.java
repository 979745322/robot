package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.helloworld.utils.Base64Util;
import com.example.helloworld.utils.FaceSearch;
import com.example.helloworld.utils.ProviderUtil;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CameraTestActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri ImageUri;
    private Button takePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        takePhoto = (Button) findViewById(R.id.take_photo);
        picture = (ImageView) findViewById(R.id.picture);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建File对象，用于存储拍照后的图片
                File outputImage = new File(getExternalCacheDir(), "outputImage.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    ImageUri = FileProvider.getUriForFile(CameraTestActivity.this,
                            ProviderUtil.getFileProviderName(CameraTestActivity.this), outputImage);
                } else {
                    ImageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra("android.intent.extras.CAMERA_FACING", 2); // 调用前置摄像头
                intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
//                        Bitmap bitmap = rotateImage(BitmapFactory.decodeStream(getContentResolver().openInputStream(ImageUri)), 90);
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(ImageUri));
                        picture.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FaceSearch.search(Base64Util.encode(Base64Util.InputStreamTOByte(Objects.requireNonNull(getContentResolver().openInputStream(ImageUri)))));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 对图片进行旋转，拍照后应用老是显示图片横向，而且是逆时针90度，现在给他设置成显示顺时针90度
     *
     * @param bitmap 图片
     * @param degree 顺时针旋转的角度
     * @return 返回旋转后的位图
     */
    public Bitmap rotateImage(Bitmap bitmap, float degree) {
        //create new matrix
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bmp;
    }
}
