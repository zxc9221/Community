 package com.seongnamc.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
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
import com.seongnamc.sns_project.Userinfo;
import com.seongnamc.sns_project.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.seongnamc.sns_project.Utility.INTENT_PATH;
import static com.seongnamc.sns_project.Utility.showToast;

 public class UserActivity extends BasicActivity {
    private static final String TAG = "MemberActivity";
    private FirebaseAuth mAuth;
    private ImageView profileView;
    private String ProfilePath;
    private RelativeLayout loaderLayout;
    private RelativeLayout buttonBackgroundLayout;
    final FirebaseUser  user = FirebaseAuth.getInstance().getCurrentUser();



     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_init);
        setToolbarTitle("회원 정보 등록");


         // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        profileView = findViewById(R.id.profileView);
        findViewById(R.id.saveButton).setOnClickListener(onClickListener);
        findViewById(R.id.galleryButton).setOnClickListener(onClickListener);
        findViewById(R.id.pictureBuutton).setOnClickListener(onClickListener);
        profileView.setOnClickListener(onClickListener);

        loaderLayout = findViewById(R.id.loaderLayout);

        buttonBackgroundLayout = findViewById(R.id.ButtonsBackgroundLayout);
        buttonBackgroundLayout.setOnClickListener(onClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
       // updateUI(currentUser);
    }
    public void onBackPressed(){
        super.onBackPressed();
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void updateUI(FirebaseUser currentUser) {
        myStartActivity(MainActivity.class);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.saveButton:
                    storageUploader();
                    break;
                case R.id.profileView:
                    buttonBackgroundLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.ButtonsBackgroundLayout:
                    buttonBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.galleryButton:
                    PostActivity(GalleryActivity.class,"image");
                    buttonBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.pictureBuutton:
                    myStartActivity(CameraActivity.class);
                    buttonBackgroundLayout.setVisibility(View.GONE);
                    break;

            }
        }
    };



    private void storageUploader(){
        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        final String phonenumber = ((EditText) findViewById(R.id.phonenumberEditText)).getText().toString();
        final String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();
        final String birthday = ((EditText) findViewById(R.id.birthdayEditText)).getText().toString();

        if(name.length() + phonenumber.length() + address.length() + birthday.length() > 0) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();
            final StorageReference mountainImagesRef = storageRef.child("user/"+user.getUid()+"/profileImage.jpg");

            loaderLayout.setVisibility(View.VISIBLE);

            if(ProfilePath == null) {
                Userinfo info = new Userinfo(name, phonenumber, address, birthday);

                storeUploader(info);
            }
            else {
                try {
                    InputStream stream = new FileInputStream(new File(ProfilePath));

                    UploadTask uploadTask = mountainImagesRef.putStream(stream);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();

                            }

                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();


                                Log.e("성공", "성공 : " + downloadUri);
                                // Access a Cloud Firestore instance from your Activity

                                Userinfo info = new Userinfo(name, phonenumber, address, birthday, downloadUri.toString());

                                storeUploader(info);


                            } else {

                                Log.e("실패", "회원 정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "error" + e.toString());
                }
            }


        }
        else{
            showToast(UserActivity.this , "정보를 입력해주세요.");
        }
    }

    private void storeUploader(Userinfo info){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUid())
                .set(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(UserActivity.this , "회원정보가 등록 되었습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        myStartActivity(MainActivity.class);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(UserActivity.this , "회원정보의 등록을 실패하였습니다 .");
                        loaderLayout.setVisibility(View.GONE);
                        Log.e("error", e + "");
                    }
                });

    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 0);
    }
     private void PostActivity(Class c, String media) {
         Intent intent = new Intent(this, c);
         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         intent.putExtra("media",media);
         startActivityForResult(intent, 0);
     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    ProfilePath = data.getStringExtra(INTENT_PATH );
                    /*Log.e("path",ProfilePath+"");
                    Bitmap bmp = BitmapFactory.decodeFile(ProfilePath);
                    profileView.setImageBitmap(bmp);*/
                    Glide.with(this).load(ProfilePath).centerCrop().override(300).into(profileView);
                    buttonBackgroundLayout.setVisibility(View.GONE);
                }
                break;
        }
     }
}