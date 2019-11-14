package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CameraResultActivity extends AppCompatActivity {
    TextView faceName;
    String context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_result);
        faceName = findViewById(R.id.FaceName);
        context = getIntent().getStringExtra("CONTEXT");
        faceName.setText(context);
    }

    public static Intent newIntent(Context packageContext, String context) {
        Intent intent = new Intent(packageContext, CameraResultActivity.class);
        intent.putExtra("CONTEXT", context);
        return intent;
    }

    public void reBack(View view) {
        Intent intent = new Intent(CameraResultActivity.this, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}
