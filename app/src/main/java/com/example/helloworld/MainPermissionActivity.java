package com.example.helloworld;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

public class MainPermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_permission);
    }

    private List<String> list = new ArrayList<>();

    public void onStart() {
        super.onStart();
        getPermissions();
    }

    private void getPermissions() {
        list.add(Permission.CAMERA); // 相机
        list.add(Permission.WRITE_EXTERNAL_STORAGE); // 文件
        list.add(Permission.RECORD_AUDIO); // 录音
        list.add(Permission.ACCESS_COARSE_LOCATION); // 定位
        XXPermissions.with(this)
                .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                .permission(list) //不指定权限则自动获取清单中的危险权限
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            Log.d("Main", "获取权限成功");
//                            Intent intent = new Intent(MainPermissionActivity.this, CameraActivity.class);
//                            Intent intent = new Intent(MainPermissionActivity.this, yuyinChat.class);
//                            Intent intent = new Intent(MainPermissionActivity.this, DingWeiActivity.class);
                            Intent intent = new Intent(MainPermissionActivity.this, CameraFaceActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d("Main", "获取权限成功,部分权限未正常授予");
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            Log.d("Main", "被永久拒绝授权，请手动授予权限");
                            goSetting();
                        } else {
                            Log.d("Main", "获取权限失败");
                        }
                    }
                });
    }

    private void goSetting() {
        new AlertDialog.Builder(this).setTitle("为了爱与和平")
                .setMessage("请给我权限吧，让我们携手共进，创造美好未来")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        XXPermissions.gotoPermissionSettings(MainPermissionActivity.this);
                    }
                })
                .setCancelable(false).show();
    }
}
