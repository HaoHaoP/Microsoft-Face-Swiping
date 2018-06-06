package com.HaoHaoP.MicrosoftFaceSwiping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Camera myCamera;

    public CameraPreview(Context context, Camera camera, SurfaceView surfaceView) {
        super(context);
        myCamera = camera;
        SurfaceHolder mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //一直保持屏幕常亮
        surfaceView.getHolder().setKeepScreenOn(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        //设置参数并开始预览
        if (myCamera != null) {
            Camera.Parameters params = myCamera.getParameters();
            params.setPictureFormat(PixelFormat.JPEG);
            myCamera.setDisplayOrientation(90);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            Camera.Size preSize = getCloselyPreSize(true, dm.widthPixels, dm.heightPixels, params.getSupportedPreviewSizes());
            params.setPreviewSize(preSize.width, preSize.height);
            params.set("orientation", "portrait");
//             params.set("rotation", 90);
            myCamera.setParameters(params);
            myCamera.startPreview();
        }
    }

    public static  Camera.Size getCloselyPreSize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for(Camera.Size size : preSizeList){
            if((size.width == reqTmpWidth) && (size.height == reqTmpHeight)){
                return size;
            }
        }

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启相机
        try {
            if (myCamera != null) {
                myCamera.setPreviewDisplay(holder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void release(){
        if (myCamera != null) {
            //关闭预览并释放资源
            myCamera.setPreviewCallback(null);
            myCamera.stopPreview();
            myCamera.release();
            myCamera = null;
        }
    }
}
