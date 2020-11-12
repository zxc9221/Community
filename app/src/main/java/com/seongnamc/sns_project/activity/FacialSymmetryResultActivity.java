package com.seongnamc.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seongnamc.sns_project.R;

import static com.seongnamc.sns_project.Utility.INTENT_PATH;

public class FacialSymmetryResultActivity extends BasicActivity {
    private static final String TAG = "FacialSymmetryHomeActivity";
    private String FacePath;
    private ImageView profileView;
    private RelativeLayout buttonsBackgroundLayout;
    private ImageView faceView;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_symmetry_result);
        setToolbarTitle("안면 분석 결과");
        String path = getIntent().getExtras().getString("facepath");

        faceView = findViewById(R.id.face_view);
        textView = findViewById(R.id.pointvalue);
        Glide.with(this).load(path).centerCrop().override(300).into(faceView);
        textView.setText("100");
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
