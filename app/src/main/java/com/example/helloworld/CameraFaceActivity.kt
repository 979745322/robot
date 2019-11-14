package com.example.helloworld

import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import androidx.annotation.RequiresApi
import com.example.helloworld.utils.*
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.android.synthetic.main.activity_camera_face.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.nio.ByteBuffer

/**
 *                    .::::.
 *                  .::::::::.
 *                 :::::::::::
 *             ..:::::::::::'
 *           '::::::::::::'
 *             .::::::::::
 *        '::::::::::::::..
 *             ..::::::::::::.
 *           ``::::::::::::::::
 *            ::::``:::::::::'        .:::.
 *           ::::'   ':::::'       .::::::::.
 *         .::::'     :::::     .:::::::'::::.
 *        .:::'       :::::  .:::::::::' ':::::.
 *       .::'        :::::.:::::::::'      ':::::.
 *      .::'         ::::::::::::::'         ``::::.
 *  ...:::           ::::::::::::'              ``::.
 * ```` ':.          ':::::::::'                  ::::..
 *                    '.:::::'                    ':'````..
 */

class CameraFaceActivity : AppCompatActivity() {
    private var rotation = 0
    private var working = false
    private val detector = FirebaseVision.getInstance().getVisionFaceDetector(
            FirebaseVisionFaceDetectorOptions
                    .Builder()
                    .build()
    )
    private val ORIENTATIONS = SparseIntArray()

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRotationCompensation(cameraId: String): Int {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        val deviceRotation = this.windowManager.defaultDisplay.rotation
        var rotationCompensation = ORIENTATIONS.get(deviceRotation)
        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        val cameraManager = this.getSystemService(CAMERA_SERVICE) as CameraManager
        val sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION)!!
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360
        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        val result: Int
        when (rotationCompensation) {
            0 -> result = FirebaseVisionImageMetadata.ROTATION_0
            90 -> result = FirebaseVisionImageMetadata.ROTATION_90
            180 -> result = FirebaseVisionImageMetadata.ROTATION_180
            270 -> result = FirebaseVisionImageMetadata.ROTATION_270
            else -> {
                result = FirebaseVisionImageMetadata.ROTATION_0
                Log.e("Bad rotation value", " $rotationCompensation")
            }
        }
        Log.e("旋转参数", " $result")
        return result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_face)

        rotation = getRotationCompensation(getCameraFrontId().toString())

        camera.setPreviewInterval(300)
        camera.setOnBitmapGenerateListener { data, bitmap, size ->
            if (working)
                return@setOnBitmapGenerateListener

            working = true
            val metadata = FirebaseVisionImageMetadata.Builder()
                    .setWidth(size.width) // 480x360 is typically sufficient for
                    .setHeight(size.height) // image recognition
                    .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                    .setRotation(rotation)
                    .build()
            val image = FirebaseVisionImage.fromByteBuffer(ByteBuffer.wrap(data), metadata)

//            FileUtil.saveBitmap(image.bitmap,"/sdcard/erning"+System.currentTimeMillis()+".jpg")
            detector.detectInImage(image)
                    .addOnSuccessListener { faces ->
                        if (faces.isNotEmpty()) {
                            doAsync {
                                var result: String? = "noface"
//                            String result = FaceSearch.search(Base64Util.encode(BatyArry.decodeValue(bitmap)));
                                val resultMap = FaceSearch.search(Base64Util.encode(BatyArry.decodeValue(image.bitmap)))
                                if (resultMap != null) {
                                    result = resultMap["result"].toString()
                                }
                                assert(result != null)
                                if (result == "undefined") run {
                                    FileUtil.saveBitmap(BitmapUtil.rotateImage(image.bitmap, 270f), filesDir.absolutePath + "picture.png")
                                    uiThread {
                                        val intent = CameraRegistActivity.newIntent(this@CameraFaceActivity, "picture.png")
                                        startActivity(intent)
                                        finish()
                                    }
                                } else if (result != "noface") {
                                    var hello = "您好"
                                    val detectionResultMap = FaceSearch.detection(Base64Util.encode(BatyArry.decodeValue(image.bitmap)))
                                    var beauty = java.lang.Float.parseFloat(detectionResultMap!!["beauty"].toString())
                                    if (result == "王总") {
                                        beauty = 101f
                                    }
                                    if (detectionResultMap["gender"] == "male") {
                                        if (beauty >= 0 && beauty < 10) {
                                            hello = "您好"
                                        } else if (beauty >= 10 && beauty < 20) {
                                            hello = "您好帅啊！"
                                        } else if (beauty >= 20 && beauty < 30) {
                                            hello = "您太帅了！"
                                        } else if (beauty >= 30 && beauty < 40) {
                                            hello = "您真是眉目疏朗！"
                                        } else if (beauty >= 40 && beauty < 50) {
                                            hello = "您真是玉树临风！"
                                        } else if (beauty >= 50 && beauty < 60) {
                                            hello = "您真是英俊潇洒！"
                                        } else if (beauty >= 60 && beauty < 70) {
                                            hello = "您真是风流倜傥！"
                                        } else if (beauty >= 70 && beauty < 80) {
                                            hello = "您真是品貌非凡！"
                                        } else if (beauty >= 80 && beauty < 90) {
                                            hello = "您真是惊才风逸！"
                                        } else if (beauty >= 90 && beauty < 100) {
                                            hello = "您真是逸群之才！"
                                        } else if (beauty == 101f) {
                                            hello = "您简直是太帅了，五官清秀中带着一抹俊俏，帅气中又带着一抹温柔！您身上散发出来的气质好复杂，带有一丝懒散一丝坚毅，谜样般的男子啊，这，这根本就是童话中的白马王子嘛！"
                                        }
                                    } else {
                                        if (beauty >= 0 && beauty < 10) {
                                            hello = "您好"
                                        } else if (beauty >= 10 && beauty < 20) {
                                            hello = "您好美啊！"
                                        } else if (beauty >= 20 && beauty < 30) {
                                            hello = "您太美了！"
                                        } else if (beauty >= 30 && beauty < 40) {
                                            hello = "您真是闭月羞花！"
                                        } else if (beauty >= 40 && beauty < 50) {
                                            hello = "您真是国色天香！"
                                        } else if (beauty >= 50 && beauty < 60) {
                                            hello = "您真是倾国倾城！"
                                        } else if (beauty >= 60 && beauty < 70) {
                                            hello = "您真是貌美如花！"
                                        } else if (beauty >= 70 && beauty < 80) {
                                            hello = "您真是天生丽质！"
                                        } else if (beauty >= 80 && beauty < 90) {
                                            hello = "您真是秀色可餐！"
                                        } else if (beauty >= 90 && beauty < 100) {
                                            hello = "您真是秀丽端庄！"
                                        }
                                    }
                                    uiThread {
                                        val name = result + "," + hello + "\n" + "您" + detectionResultMap["expression"] + "," + "您好像很" + detectionResultMap["emotion"]
//                                        toast(name)
                                        val intent = yuyinChat.newIntent(this@CameraFaceActivity, name)
                                        startActivity(intent)
                                        finish()
                                    }
                                } else {
                                    working = false
                                }
                            }
                        } else {
                            working = false
                        }
                    }
                    .addOnFailureListener {
                        working = false
                    }
        }
    }

    private fun getCameraFrontId(): Int {
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0..numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return i
            }
        }
        return 1
    }
}