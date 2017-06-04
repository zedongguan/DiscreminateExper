package robot.tangwa.discreminateexper.FaceRecognizer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.util.Accelerometer;

import java.io.IOException;


import robot.tangwa.discreminateexper.MyApplication;
import robot.tangwa.discreminateexper.R;

/**开启摄像头，获取视频流，监测视频流，获取视频流中的正面人脸
 * Created by Administrator on 2017/3/20.
 */

public class GetFace extends Activity {

    // FaceDetector对象，集成了离线人脸识别：人脸检测、视频流检测功能
    private FaceDetector mFaceDetector;
    private Context context = GetFace.this;
    private Camera mCamera;
    private SurfaceView mPreviewSurface;
    private SurfaceView mFaceSurface;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private String TAG = MyApplication.TAG;
    // Camera nv21格式预览帧的尺寸，默认设置640*480
    private int PREVIEW_WIDTH = 640;
    private int PREVIEW_HEIGHT = 480;
    // 预览帧数据存储数组和缓存数组
    private byte[] nv21;
    private byte[] buffer;
    // 缩放矩阵
    private Matrix mScaleMatrix = new Matrix();
    // 加速度感应器，用于获取手机的朝向
    private Accelerometer mAcc;
    private int isAlign = 0;
    private boolean mStopTrack;
    private long mLastClickTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getface_activity);
        initUI();
        SpeechUtility speechUtility = SpeechUtility.createUtility(this, "appid=123465");
        Log.e(TAG,"speechUtility:"+speechUtility);

        nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        buffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        mAcc = new Accelerometer(context);
        mFaceDetector = FaceDetector.createDetector(context, null);
        Log.e(TAG,"加速度传感器："+mAcc);
        Log.e(TAG,"人脸识别类："+mFaceDetector);


    }

    private void initUI() {
        mPreviewSurface = (SurfaceView) findViewById(R.id.sfv_preview);
        mFaceSurface = (SurfaceView) findViewById(R.id.sfv_face);

        mPreviewSurface.getHolder().addCallback(mPreviewCallback);
        mPreviewSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mFaceSurface.setZOrderOnTop(true);//展示在窗口最前端
        mFaceSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // 点击SurfaceView，切换摄相头
        mFaceSurface.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 只有一个摄相头，不支持切换
                if (Camera.getNumberOfCameras() == 1) {
                    Log.e(TAG,"只有后置摄像头，不能切换");
                    return;
                }
                closeCamera();
                if (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
                    mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                } else {
                    mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                openCamera();
            }
        });
        RadioGroup alignGruop = (RadioGroup) findViewById(R.id.align_mode);
        alignGruop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                switch (arg1) {
                    case R.id.detect:
                        isAlign = 0;
                        break;
                    case R.id.align:
                        isAlign = 1;
                        break;
                    default:
                        break;
                }
            }
        });

        // 长按SurfaceView 500ms后松开，摄相头聚集
        mFaceSurface.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastClickTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - mLastClickTime > 500) {
                            Log.e(TAG,"调用摄像头自动聚焦！");
                            mCamera.autoFocus(null);
                            return true;
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        setSurfaceSize();
    }

    private void setSurfaceSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        //其实这里就是反着写，宽高，按比例缩放（之前的比例480*640）
        int height = (int) (width * PREVIEW_WIDTH/ (float)PREVIEW_HEIGHT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        mPreviewSurface.setLayoutParams(params);
        mFaceSurface.setLayoutParams(params);
    }

    private SurfaceHolder.Callback mPreviewCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            closeCamera();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            openCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            mScaleMatrix.setScale(width/(float)PREVIEW_HEIGHT, height/(float)PREVIEW_WIDTH);
        }
    };

    private void openCamera() {
        if (null != mCamera) {
            return;
        }

        if (!checkCameraPermission()) {
            Log.e(TAG,"摄像头权限未打开，请打开后再试");
            mStopTrack = true;
            return;
        }

        // 只有一个摄相头，打开后置
        if (Camera.getNumberOfCameras() == 1) {
            mCameraId = android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        try {
            mCamera = Camera.open(mCameraId);
            if (android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
                Log.e(TAG,"前置摄像头已开启，点击可切换");
            } else {
                Log.e(TAG,"后置摄像头已开启，点击可切换");
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeCamera();
            return;
        }

        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewFormat(ImageFormat.NV21);
        params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        mCamera.setParameters(params);

        // 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                System.arraycopy(data, 0, nv21, 0, data.length);
            }
        });

        try {
            mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(mFaceDetector == null) {
            /**
             * 离线视频流检测功能需要单独下载支持离线人脸的SDK
             * 请开发者前往语音云官网下载对应SDK
             */
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            Log.e(TAG,"创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化" );
        }
    }

    private void closeCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
    private boolean checkCameraPermission() {
        int status = checkPermission(Manifest.permission.CAMERA, Process.myPid(), Process.myUid());
        if (PackageManager.PERMISSION_GRANTED == status) {
            return true;
        }

        return false;
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (null != mAcc) {
            mAcc.start();
        }

        mStopTrack = false;
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!mStopTrack) {
                    if (null == nv21) {
                        continue;
                    }

                    synchronized (nv21) {
                        System.arraycopy(nv21, 0, buffer, 0, nv21.length);
                    }

                    // 获取手机朝向，返回值0,1,2,3分别表示0,90,180和270度
                    int direction = Accelerometer.getDirection();
                    boolean frontCamera = (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId);
                    // 前置摄像头预览显示的是镜像，需要将手机朝向换算成摄相头视角下的朝向。
                    // 转换公式：a' = (360 - a)%360，a为人眼视角下的朝向（单位：角度）
//                    Log.e(TAG,"direction1:"+direction);
                    if (frontCamera) {
                        // SDK中使用0,1,2,3分别表示0,90,180,270
                        direction = (4 - direction)%4;
                        //没有加速度传感器则输出为0
                    }
//                    Log.e(TAG,"direction2:"+direction);
                    if(mFaceDetector == null) {
                        /**
                         * 离线视频流检测功能需要单独下载支持离线人脸的SDK
                         * 请开发者前往语音云官网下载对应SDK
                         */
                        // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
                        Log.e(TAG, "创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化" );
                        break;
                    }
                    //这个参数只是给讯飞参照，看人应该从哪个方向进行解析人脸
                    //接入唐娃中需要使摄像头获取到的图片进行旋转
//                    direction =1;唐娃使用人脸识别方向
                    String result = mFaceDetector.trackNV21(buffer, PREVIEW_WIDTH, PREVIEW_HEIGHT, isAlign, direction);
//                    Log.e(TAG,"result:"+result);
                    FaceRect[] faces = ParseResult.parseResult(result);
//                    Log.e(TAG,"faces:"+faces);
                    Canvas canvas = mFaceSurface.getHolder().lockCanvas();
                    if (null == canvas) {
                        continue;
                    }

                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    canvas.setMatrix(mScaleMatrix);

                    if( faces == null || faces.length <=0 ) {
                        mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
                        continue;
                    }

                    /*绘制讯飞返回的关键点，
                        测试：没网情况下可以使用
                     */
                    if (null != faces) {
                        Log.e(TAG,"识别出人脸");
                        for (FaceRect face: faces) {
                            Log.e(TAG,"face.bound:"+face.bound);
                            face.bound = FaceUtil.RotateDeg90(face.bound, PREVIEW_WIDTH, PREVIEW_HEIGHT);
                            Log.e(TAG,"旋转后的face.bound:"+face.bound);
                            if (face.point != null) {
                                for (int i = 0; i < face.point.length; i++) {
                                    face.point[i] = FaceUtil.RotateDeg90(face.point[i], PREVIEW_WIDTH, PREVIEW_HEIGHT);
                                }
                            }
                            FaceUtil.drawFaceRect(canvas, face, PREVIEW_WIDTH, PREVIEW_HEIGHT,
                                    frontCamera, false);
                        }
                    } else {
                        Log.d(TAG, "faces:0");
                    }

                    mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
        if (null != mAcc) {
            mAcc.stop();
        }
        mStopTrack = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( null != mFaceDetector ){
            // 销毁对象
            mFaceDetector.destroy();
        }
    }


}
