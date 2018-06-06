package com.HaoHaoP.microsoftfaceswipingdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText baseUrlEt, keyEt, groupNameEt;
    private Button testBtn;
    private ImageView imgIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseUrlEt = findViewById(R.id.et_base_url);
        keyEt = findViewById(R.id.et_key);
        groupNameEt = findViewById(R.id.et_group_name);
        testBtn = findViewById(R.id.btn_test);
        imgIv = findViewById(R.id.iv_img);

        baseUrlEt.setText("https://api.cognitive.azure.cn/face/v1.0");
        keyEt.setText("145c85d823524c4ba5332840df266468");
        groupNameEt.setText("nngeobdc");

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                            1);
                } else {
                    startTest();
                }
            }
        });
    }

    private void startTest() {
        Intent intent = new Intent(MainActivity.this, FaceSwipingActivity.class);
        intent.putExtra("baseUrl",baseUrlEt.getText().toString().trim());
        intent.putExtra("key",keyEt.getText().toString().trim());
        intent.putExtra("groupName",groupNameEt.getText().toString().trim());
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTest();
        } else {
            Toast.makeText(this, "请在应用管理中打开“相机”访问权限！", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            byte[] img = data.getByteArrayExtra("img");
            if (img.length != 0)
                imgIv.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
        }
    }
}
