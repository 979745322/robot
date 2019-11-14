package com.example.helloworld.service;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static android.hardware.Camera.Parameters.FLASH_MODE_ON;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback,Camera.PreviewCallback {

    private static final String TAG = "CameraSurfaceView";
    public static final String MODE_FLASH_OFF = FLASH_MODE_TORCH;
    public static final String MODE_FLASH_ON = FLASH_MODE_ON;

    private Context mContext;
    private SurfaceHolder holder;
    private Camera mCamera;

    private int mScreenWidth;
    private int mScreenHeight;

    /**
     * 拍照时的监听器
     */
    private OnPictureCallbackListener onPictureCallbackListener;
    public interface OnPictureCallbackListener{
        void onPictureCallback(Bitmap bitmap);
    }
    public void setOnPictureCallbackListener(OnPictureCallbackListener onPictureCallbackListener) {
        this.onPictureCallbackListener = onPictureCallbackListener;
    }

    // 拍照瞬间调用
    private Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            Log.i(TAG,"shutter");
        }
    };

    // 获得没有压缩过的图片数据
    private Camera.PictureCallback raw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            Log.i(TAG, "raw");
        }
    };

    //创建jpeg图片回调数据对象
    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            Log.i(TAG,"拍摄了一张照片");
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

            //让保存的图片旋转90度
            Matrix m = new Matrix();
            m.setRotate(90,(float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            Bitmap bm0 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            //释放原先的图片
            if (bm.isRecycled())
                bm.recycle();

            if(onPictureCallbackListener != null)
                onPictureCallbackListener.onPictureCallback(bm0);


            mCamera.stopPreview();// 关闭预览
            mCamera.startPreview();// 开启预览

            /*BufferedOutputStream bos = null;
            Bitmap bm = null;
            try {
                // 获得图片
                bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Log.i(TAG, "Environment.getExternalStorageDirectory()="+Environment.getExternalStorageDirectory());
                    File path = new File(Environment.getExternalStorageDirectory()+"/pppppppp");
                    if (!path.exists())
                        path.mkdir();
                    String filePath = path.getAbsolutePath()+"/"+System.currentTimeMillis()+".jpg";//照片保存路径
                    File file = new File(filePath);
                    if (!file.exists()){
                        file.createNewFile();
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
                }else{
                    Toast.makeText(mContext,"没有检测到内存卡", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) {
                        bos.flush();//输出
                        bos.close();//关闭
                        bm.recycle();// 回收bitmap空间
                    }
                    mCamera.stopPreview();// 关闭预览
                    mCamera.startPreview();// 开启预览
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }
    };
    //////////////////////////////////////////////////////////////////////////////////////////////
    public void takePicture(){
        //设置参数,并拍照
        //setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        // 当调用camera.takePiture方法后，camera关闭了预览，这时需要调用startPreview()来重新开启预览
        mCamera.takePicture(null, null, jpeg);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getScreenMetrix(context);
        initView();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取屏幕宽高
     */
    private void getScreenMetrix(Context context) {
        try {
            WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            WM.getDefaultDisplay().getMetrics(outMetrics);
            mScreenWidth = outMetrics.widthPixels;
            mScreenHeight = outMetrics.heightPixels;
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void initView() {
        holder = getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型
    }

    /**
     * 创建相机
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//开启相机
            try {
                mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
        setCameraParams(null,mScreenWidth,mScreenHeight);
        mCamera.startPreview();
        //holder.removeCallback(this);
        mCamera.setPreviewCallback(this);
    }

    /*
      停止相机
      1. 具体原因
     是因为在之前调用mCamera.startPreview()方法之前，调用了mCamera.setPreviewCallback(this)
     导致在手动调用上面stopPreview()的时候，this 实现的PreviewCallback接口onPreviewFrame方法还在不停调用，
     具体调用频率就是当前预览的FrameRate，当stopPreview()执行完mCamera.release()时，onPreviewFrame再次被调用时就出现了该异常。
     2. 解决办法
     在自定义的stopPreview()里面调用mCamera.release()之前，先调用一次mCamera.setPreviewCallback(null);
     这样在执行完_mCamera.release()，因为指定的PreviewCallback为null
     因而就不会再调用onPreviewFrame，进而也不会再引用到camera和调用到其任何方法。
     注：onPreviewFrame第二个参数引用了camera。
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        mCamera.stopPreview();//停止预览
        mCamera.setPreviewCallback(null);
        mCamera.release();//释放相机资源
        mCamera = null;
        holder = null;
    }

    @Override
    public void onAutoFocus(boolean success, Camera Camera) {
        if (success) {
            Log.i(TAG, "onAutoFocus success="+success);
        }
    }

    /**
     * 让相机显示正常
     */
    private void setCameraParams(Camera camera, int width, int height) {
        Log.i(TAG,"setCameraParams  width="+width+"  height="+height);
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        //从列表中选取合适的分辨率
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }
        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
//        float w = picSize.width;
//        float h = picSize.height;
        parameters.setPictureSize(picSize.width,picSize.height);
//        this.setLayoutParams(new FrameLayout.LayoutParams((int) (height*(h/w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);//获取最大图片
        //Camera.Size preSize = getSuitableSize(previewSizeList);//获取何时上传的大小
        if (null != preSize) {
            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS) && parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        Camera.Size suitableSize = getSuitableSize(pictureSizeList);
        parameters.setPictureSize(suitableSize.width, suitableSize.height);//如果不设置会按照系统默认配置最低160x120分辨率

        //parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//设置拍照时的闪光灯
/*        if(parameters.getMaxNumFocusAreas() != 0){
            //最大对焦区域数
            Camera.Area area1 = new Camera.Area(new Rect(-1000,-1000,-900,-900),1);
            List<Camera.Area> list = new ArrayList<>();
            list.add(area1);
            parameters.setFocusAreas(list);
        }*/

        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS))
            mCamera.cancelAutoFocus();//自动对焦。
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);
    }

    /**
     * 设置闪光灯状态
     * @return 设置是否成功
     */
    public boolean setFlashMode(String mode){
        Log.i(TAG,"打开闪光灯："+mode);
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(mode);
            mCamera.setParameters(parameters);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    /**
     * 获得闪光灯状态
     */
    public String getFlashMode(){
        Camera.Parameters parameters = mCamera.getParameters();
        return parameters.getFlashMode();
    }


    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     *         h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (int i = pictureSizeList.size()-1;i>0;i--) {
            float currentRatio = ((float) pictureSizeList.get(i).width) / pictureSizeList.get(i).height;
            if (currentRatio - screenRatio == 0) {
                result = pictureSizeList.get(i);
                break;
            }
        }
        if (null == result) {
            for (int i = pictureSizeList.size()-1;i>0;i--) {
                float curRatio = ((float) pictureSizeList.get(i).width) / pictureSizeList.get(i).height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = pictureSizeList.get(i);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 获取适合保存的图片大小
     * @param pictureSizeList 手机像素列表
     * @return 适合上传的大小
     */
    private Camera.Size getSuitableSize(List<Camera.Size> pictureSizeList){
        Collections.reverse(pictureSizeList);     //实现list集合逆序排列
        for (Camera.Size size : pictureSizeList) {
            if (size.width>=1088 && size.height>=1088){
                return size;
            }
        }
        return pictureSizeList.get(pictureSizeList.size()-1);
    }

    /////////////////////////////////////预览相关///////////////////////////////////
    /**
     * 发生预览
     */
    private int previewInterval = 1000;//预览间隔（s）
    private long time1 = System.currentTimeMillis();
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if(this.onBitmapGenerateListener == null)
            return;

        long time2 = System.currentTimeMillis();

        if(time2-time1 >= previewInterval){
            Log.i(TAG,"一帧:"+time2);
            time1 = time2;
            runInPreviewFrame(bytes,camera);
        }
    }
    /**
     * 设置预览时间间隔
     * @param previewInterval 预览间隔（s）
     */
    public void setPreviewInterval(int previewInterval) {
        this.previewInterval = previewInterval;
    }

    /**
     * 将预览数据转成bitmap
     * @param data 预览产生的的数据
     */
    public void runInPreviewFrame(byte[] data, Camera camera) {
        //camera.setOneShotPreviewCallback(null);
        //处理data
        Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(data,ImageFormat.NV21, previewSize.width, previewSize.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);//JPG图片的质量[0-100],100最高
        byte[] rawImage = baos.toByteArray();
        //将rawImage转换成bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
        if(this.onBitmapGenerateListener != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
            Camera.Size preSize = getProperSize(previewSizeList, ((float) mScreenHeight) / mScreenWidth);
            onBitmapGenerateListener.bitmapGenerate(data, bitmap,preSize);
        }
    }

    /**
     * 预览事件的监听器
     */
    private OnBitmapGenerateListener onBitmapGenerateListener;
    public interface OnBitmapGenerateListener{
        void bitmapGenerate(byte[] data, Bitmap bitmap, Camera.Size preSize);
    }
    public void setOnBitmapGenerateListener(OnBitmapGenerateListener onBitmapGenerateListener) {
        this.onBitmapGenerateListener = onBitmapGenerateListener;
    }
    /////////////////////////////////////预览相关///////////////////////////////////
}