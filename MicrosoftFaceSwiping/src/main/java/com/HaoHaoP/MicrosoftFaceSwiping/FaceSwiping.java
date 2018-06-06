package com.HaoHaoP.MicrosoftFaceSwiping;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.HaoHaoP.MicrosoftFaceSwiping.bean.DetectBean;
import com.HaoHaoP.MicrosoftFaceSwiping.bean.IdentifyBean;
import com.HaoHaoP.MicrosoftFaceSwiping.bean.IdentifyPostBody;
import com.HaoHaoP.MicrosoftFaceSwiping.bean.PersonGroupsBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * MIT License
 * <p>
 * Copyright (c) 2018 皓皓P
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class FaceSwiping {

    private Camera myCamera;
    private SurfaceView mySurfaceView;
    private CameraPreview cameraPreview;
    private static String BASE_URL;
    private static String KEY;
    private static String GROUP_NAME;
    private FaceCallBack faceCallBack;
    private int loopTime = 2000;
    private int times = 10;
    private int temp = 0;
    private double confidence = 0.8;
    private long firstTime = 0;
    private Context context;

    public static final int CALL_BACK_ERROR = -1;
    public static final int VALUE_ERROR = -2;
    public static final int TIME_OUT = 0;

    /**
     * FaceSwiping
     *
     * @param context     context
     * @param surfaceView surfaceView
     * @param url         https://[location].api.cognitive.microsoft.com/face/v1.0/
     * @param key         Subscription key which provides access to Face API.
     * @param name        compare GroupName
     */
    public FaceSwiping(Context context, SurfaceView surfaceView, String url, String key, String name) {
        this.context = context;
        this.mySurfaceView = surfaceView;
        BASE_URL = url;
        KEY = key;
        GROUP_NAME = name;
    }

    public FaceSwiping setFaceCallBack(FaceCallBack faceCallBack) {
        this.faceCallBack = faceCallBack;
        return this;
    }

    public FaceSwiping setLoopTime(int time) {
        loopTime = time;
        return this;
    }

    public FaceSwiping setTimes(int times) {
        this.times = times;
        return this;
    }

    public FaceSwiping setConfidence(double confidence) {
        this.confidence = confidence;
        return this;
    }

    public FaceSwiping start() {
        if (BASE_URL.isEmpty() || KEY.isEmpty() || GROUP_NAME.isEmpty() || mySurfaceView == null) {
            faceCallBack.onError(VALUE_ERROR);
            return this;
        } else if (faceCallBack == null) {
            faceCallBack.onError(CALL_BACK_ERROR);
            return this;
        }
        initCameraData();
        return this;
    }

    public void release() {
        if (cameraPreview != null) {
            cameraPreview.release();
            cameraPreview = null;
        }
        if (myCamera != null) {
            myCamera.release();
            myCamera = null;
        }
    }

    private void initCameraData() {
        if (null == myCamera) {
            myCamera = getCameraInstance();
            cameraPreview = new CameraPreview(context, myCamera, mySurfaceView);
        }

        myCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] data, final Camera camera) {
                if (System.currentTimeMillis() - firstTime > loopTime && ++temp <= times) {
                    //处理data
                    Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
                    YuvImage yuvimage = new YuvImage(
                            data,
                            ImageFormat.NV21,
                            previewSize.width,
                            previewSize.height,
                            null);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);

                    //旋转270度
                    Bitmap bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
                    Matrix matrix = new Matrix();
                    matrix.preRotate(270);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap
                            .getHeight(), matrix, true);

                    ByteArrayOutputStream newBaos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, newBaos);
                    byte[] rawImage = newBaos.toByteArray();
                    compareNet(rawImage);
                    firstTime = System.currentTimeMillis();
                } else if (temp >= times) {
                    faceCallBack.onError(TIME_OUT);
                }
            }
        });

    }

    private Camera getCameraInstance() {
        Camera c = null;
        Camera.CameraInfo info = new Camera.CameraInfo();
        int count = Camera.getNumberOfCameras();

        try {
            for (int i = 0; i < count; i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    c = Camera.open(i);
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "打开相机失败", Toast.LENGTH_SHORT).show();
        }
        return c;
    }


    private OkHttpClient okHttpClient = new OkHttpClient();

    private void compareNet(final byte[] bytes) {
        if (bytes.length == 0) {
            return;
        }
        final String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
        Request request = new Request.Builder()
                .url(BASE_URL + "/detect")
                .post(body)
                .addHeader("Ocp-Apim-Subscription-Key", KEY)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Log.i("微软刷脸 compareNet", data);
                if (response.isSuccessful()) {
                    DetectBean[] detectBean = new Gson().fromJson(data, DetectBean[].class);
                    if (detectBean.length == 0) {
                        return;
                    }
                    String faceId = detectBean[0].getFaceId();
                    getPersonGroups(faceId, bytes);
                }

            }
        });
    }

    private void identify(String faceId, final String personGroupId, final byte[] bytes) {
        IdentifyPostBody postBody = new IdentifyPostBody();
        postBody.faceIds = new ArrayList<>();
        postBody.faceIds.add(faceId);
        postBody.personGroupId = personGroupId;
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(postBody));
        Request request = new Request.Builder()
                .url(BASE_URL + "/identify")
                .addHeader("Ocp-Apim-Subscription-Key", KEY)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Log.i("微软刷脸 identify", data);
                if (response.isSuccessful()) {
                    IdentifyBean[] identifyBean = new Gson().fromJson(data, IdentifyBean[].class);
                    for (IdentifyBean identify : identifyBean) {
                        if (identify.getCandidates().size() > 0) {
                            if (identify.getCandidates().get(0).getConfidence() > confidence)
                                persons(personGroupId, identify.getCandidates().get(0).getPersonId(), bytes);
                        }
                    }
                }
            }
        });
    }

    private String resultName;
    private String resultData;

    private void persons(String personGroups, String person, final byte[] bytes) {
        final Request request = new Request.Builder()
                .url(BASE_URL + "/persongroups/" + personGroups + "/persons/" + person)
                .addHeader("Ocp-Apim-Subscription-Key", KEY)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Log.i("微软刷脸 persons", data);
                if (response.isSuccessful()) {
                    PersonGroupsBean personGroupsBean = new Gson().fromJson(data, PersonGroupsBean.class);
                    resultName = personGroupsBean.getName();
                    resultData = personGroupsBean.getUserData();
                    faceCallBack.onResponse(bytes, resultName, resultData);
                }
            }
        });
    }

    private void getPersonGroups(final String faceId, final byte[] bytes) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/persongroups?top=1000")
                .addHeader("Ocp-Apim-Subscription-Key", KEY)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Log.i("微软刷脸 getPersonGroups", data);
                if (response.isSuccessful()) {
                    PersonGroupsBean[] personGroups = new Gson().fromJson(data, PersonGroupsBean[].class);
                    for (PersonGroupsBean groups : personGroups) {
                        if (groups.getName().equals(GROUP_NAME)) {
                            identify(faceId, groups.getPersonGroupId(), bytes);
                            return;
                        }
                    }
                }
            }
        });
    }

    public interface FaceCallBack {
        void onError(int error);

        void onResponse(byte[] img, String name, String userData);
    }
}
