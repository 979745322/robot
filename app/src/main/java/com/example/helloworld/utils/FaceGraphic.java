// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.helloworld.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FaceGraphic extends GraphicOverlay.Graphic {
    private static final String TAG = "FaceGraphic";
    private static final int TEXT_COLOR = Color.RED;
    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;

    private final Paint rectPaint;
    private final Paint textPaint;
    private final Paint pointPaint;
    private final FirebaseVisionFace face;

    private int width;
    private int height;
    private int scaleX;
    private int scaleY;

    public FaceGraphic(GraphicOverlay overlay, FirebaseVisionFace face) {
        super(overlay);
        width = overlay.getLayoutParams().width;
        height = overlay.getLayoutParams().height;

        this.face = face;

        rectPaint = new Paint();
        rectPaint.setColor(TEXT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        textPaint = new Paint();
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(TEXT_SIZE);

        pointPaint = new Paint();
        pointPaint.setStrokeWidth(5);
        pointPaint.setColor(Color.GREEN);
        pointPaint.setTextSize(TEXT_SIZE);

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect box = face.getBoundingBox();
        box.left *= scaleX;
        box.right *= scaleX;
        box.top *= scaleY;
        box.bottom *= scaleY;
        RectF rect = new RectF(box);
        // 位置
        canvas.drawRect(rect, rectPaint);
        // ID
//        if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
//            int id = face.getTrackingId();
//            canvas.drawText("ID："+id,rect.left,rect.top,textPaint);
//        }
        // 微笑
//        if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
//            double smileProb = face.getSmilingProbability();
//            smileProb=((int)(smileProb*100))/100.0;
//            canvas.drawText("微笑概率："+smileProb,rect.left,rect.top+TEXT_SIZE,textPaint);
//        }

        // 点
//        List<FirebaseVisionPoint> faceContour = face.getContour(FirebaseVisionFaceContour.ALL_POINTS).getPoints();
//        for (FirebaseVisionPoint contour : faceContour){
//            canvas.drawPoint(contour.getX()*scaleX,contour.getY()*scaleY,pointPaint);
//        }
    }

    public void setCameraInfo(int width, int height) {
        scaleX = this.width/width;
        scaleY = this.height/height;
    }
}
