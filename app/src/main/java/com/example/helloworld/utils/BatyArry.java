package com.example.helloworld.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class BatyArry {
    public static byte[] decodeValue(Bitmap mRBmp) {
//        int len = bytes.limit() - bytes.position();
//        byte[] bytes1 = new byte[len];
//        bytes.get(bytes1);
//        return bytes1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mRBmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();
        return data;
    }


}
