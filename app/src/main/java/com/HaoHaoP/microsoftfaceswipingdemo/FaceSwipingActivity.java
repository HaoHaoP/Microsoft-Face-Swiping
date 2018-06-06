package com.HaoHaoP.microsoftfaceswipingdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.HaoHaoP.MicrosoftFaceSwiping.FaceSwiping;

import static com.HaoHaoP.MicrosoftFaceSwiping.FaceSwiping.CALL_BACK_ERROR;
import static com.HaoHaoP.MicrosoftFaceSwiping.FaceSwiping.TIME_OUT;
import static com.HaoHaoP.MicrosoftFaceSwiping.FaceSwiping.VALUE_ERROR;

public class FaceSwipingActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private FaceSwiping faceSwiping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_face_swiping);

        Intent intent = getIntent();
        String baseUrl = intent.getStringExtra("baseUrl");
        String key = intent.getStringExtra("key");
        String groupName = intent.getStringExtra("groupName");
        surfaceView = findViewById(R.id.surface);
        faceSwiping = new FaceSwiping(this, surfaceView, baseUrl, key, groupName)
                .setConfidence(0.8)
                .setLoopTime(2000)
                .setTimes(10)
                .setFaceCallBack(new FaceSwiping.FaceCallBack() {
                    @Override
                    public void onError(int error) {
                        switch (error) {
                            case TIME_OUT:
                                Toast.makeText(FaceSwipingActivity.this,"识别超时",Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            case CALL_BACK_ERROR:
                                break;
                            case VALUE_ERROR:
                                break;
                        }
                    }

                    @Override
                    public void onResponse(byte[] img, String name, String userData) {
                        Intent intent = new Intent();
                        intent.putExtra("img", img);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).start();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        faceSwiping.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        faceSwiping.release();
    }

    public void backOnClick(View view) {
        finish();
    }
}
