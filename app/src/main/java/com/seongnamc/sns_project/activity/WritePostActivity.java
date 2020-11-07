package com.seongnamc.sns_project.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.seongnamc.sns_project.Memberinfo;
import com.seongnamc.sns_project.R;
import com.seongnamc.sns_project.Writeinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class WritePostActivity extends BasicActivity {
    private static final String TAG = "WtitePostActivity";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        findViewById(R.id.addpostButton).setOnClickListener(onClickListner);
    }

    View.OnClickListener onClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.addpostButton :
                    Log.d("등록","등록");
                    break;


            }
        }

    };

    private void saveprofile(){
        final String title = ((EditText) findViewById(R.id.titleeditText)).getText().toString();
        final String contents = ((EditText) findViewById(R.id.contentsMultiLine)).getText().toString();

        if(title.length()  > 0) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();


            final StorageReference mountainImagesRef = storageRef.child(user.getUid()+"/profileImage.jpg");


            Memberinfo info = new  Writeinfo(title, contents);

            upLoaderDb(info);

        }
        else{
            StartToast("정보를 입력해주세요.");
        }
    }

    private void upLoaderDb(Memberinfo info){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUid())
                .set(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        StartToast("회원정보가 등록 되었습니다.");
                        myStartActivity(MainActivity.class);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        StartToast("회원정보의 등록을 실패하였습니다 .");
                        Log.e("error", e + "");
                    }
                });

    }

    private void StartToast(String text){
        Toast.makeText(this, text , Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 0);
    }
}
