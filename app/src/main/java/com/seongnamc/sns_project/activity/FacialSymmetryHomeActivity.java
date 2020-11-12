package com.seongnamc.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.seongnamc.sns_project.R;

import static com.seongnamc.sns_project.Utility.INTENT_PATH;

public class FacialSymmetryHomeActivity extends BasicActivity {
    private static final String TAG = "FacialSymmetryHomeActivity";
    private String FacePath;
    private ImageView profileView;
    private RelativeLayout buttonsBackgroundLayout;
    private ImageView faceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_symmetry_home);
        setToolbarTitle("안면 분석");

        findViewById(R.id.pictureSelectButton).setOnClickListener(onClickListner);
        findViewById(R.id.measureButton).setOnClickListener(onClickListner);
        findViewById(R.id.captureSelect).setOnClickListener(onClickListner);
        findViewById(R.id.gallerySelcet).setOnClickListener(onClickListner);
        buttonsBackgroundLayout = findViewById(R.id.ButtonsBackgroundLayout);
        faceView = findViewById(R.id.face_view);
        buttonsBackgroundLayout.setOnClickListener(onClickListner);
    }


    View.OnClickListener onClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pictureSelectButton:
                    buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.measureButton:

                    if(FacePath != null) {
                        Log.e("inininin","inmininin");
                        myStartActivity(FacialSymmetryResultActivity.class);
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.gallerySelcet:
                    PostActivity(GalleryActivity.class,"image");
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.captureSelect:
                    CameraActivity(CameraActivity.class, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private void PostActivity(Class c, String media) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("media",media);
        startActivityForResult(intent, 0);
    }

    private void CameraActivity(Class c, int camera) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("camera", camera);
        startActivityForResult(intent, 0);
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("facepath",FacePath);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    FacePath = data.getStringExtra(INTENT_PATH);
                    Log.e("path",FacePath+"");
                    /*Bitmap bmp = BitmapFactory.decodeFile(ProfilePath);
                    profileView.setImageBitmap(bmp);*/
                    Glide.with(this).load(FacePath).centerCrop().override(300).into(faceView);
                }
                break;
        }
    }
}
