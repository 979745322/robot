package com.example.helloworld.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtil {
    /**
     * 对图片进行旋转，拍照后应用老是显示图片横向，而且是逆时针90度，现在给他设置成显示顺时针90度
     *
     * @param bitmap 图片
     * @param degree 顺时针旋转的角度
     * @return 返回旋转后的位图
     */
    public static Bitmap rotateImage(Bitmap bitmap, float degree) {
        //create new matrix
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bmp;
    }
}
