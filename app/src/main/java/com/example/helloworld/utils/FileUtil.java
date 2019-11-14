package com.example.helloworld.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by encyc on 2017/11/11.
 */

public class FileUtil {
    /**
     * 根据uri获取文件
     */
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /** 保存方法 */
    public static boolean saveBitmap(Bitmap bm,String picName) {
        try {
            File f = new File(picName);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            if (picName.endsWith(".jpg") || picName.endsWith(".JPG") || picName.endsWith(".jpeg") || picName.endsWith(".JPEG"))
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            else
                bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean copyFile(File cacheFile, String localSavePath){
        if (cacheFile == null || localSavePath.isEmpty())
            return false;

        FileInputStream ins = null;
        FileOutputStream out = null;
        try {
            ins = new FileInputStream(cacheFile);
            out = new FileOutputStream(new File(localSavePath));
            byte[] b = new byte[1024];
            int n=0;
            while((n = ins.read(b))!=-1){
                out.write(b, 0, n);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }catch (Exception e){
            return false;
        }finally {
            try {
                if (ins!=null && out!=null) {
                    ins.close();
                    out.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }


        return true;
    }
}
