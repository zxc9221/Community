package com.seongnamc.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.seongnamc.sns_project.Memberinfo;
import com.seongnamc.sns_project.R;
import com.seongnamc.sns_project.Writeinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class WritePostActivity extends BasicActivity {
    private static final String TAG = "WtitePostActivity";
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    int successCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        findViewById(R.id.addpostButton).setOnClickListener(onClickListner);
        findViewById(R.id.picturebutton).setOnClickListener(onClickListner);
        findViewById(R.id.vidiobutton).setOnClickListener(onClickListner);
        parent =  findViewById(R.id.contentslayout);
    }

    View.OnClickListener onClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.addpostButton :
                    uploadContents();
                    myStartActivity(MainActivity.class);
                    break;
                case R.id.picturebutton :
                    PostActivity(GalleryActivity.class, "image");
                    break;
                case R.id.vidiobutton :
                    PostActivity(GalleryActivity.class, "vidio");
                    break;


            }
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    String Path = data.getStringExtra("ProfilePath");
                    pathList.add(Path);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    Glide.with(this).load(Path).centerCrop().override(500).into(imageView);
                    parent.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    parent.addView(editText);
                }
                break;
        }
    }

    private void uploadContents() {
        final String title = ((EditText) findViewById(R.id.titleeditText)).getText().toString();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        if (title.length() > 0) {
            // Create a storage reference from our app
            ArrayList<String> contentsList = new ArrayList<>();
            int pathCount = 0;


            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt((i));
                if (view instanceof EditText) {
                    String text = ((EditText) view).getText().toString();
                    if (text.length() > 0) {
                        contentsList.add(text);
                    }
                } else {
                    contentsList.add(pathList.get(pathCount));

                    final StorageReference mountainImagesRef = storageRef.child("user/" + user.getUid() + "/" + pathCount + ".jpg");

                    try {
                        InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index",""+(contentsList.size()-1)).build();
                        UploadTask uploadTask = mountainImagesRef.putStream(stream,metadata);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        contentsList.set(index, uri.toString());
                                        successCount++;
                                        if(pathList.size() == successCount){
                                            Writeinfo info = new Writeinfo(title, contentsList, user.getUid(), new Date());
                                            upLoaderDb(info);
                                        }
                                    }
                                });


                            }
                        });

                    } catch (FileNotFoundException e) {
                        Log.e("로그", "error" + e.toString());
                    }
                    pathCount++;

                }
            }
        } else {
            StartToast("정보를 입력해주세요.");
        }
    }



    private void upLoaderDb(Writeinfo info){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("contents").add(info)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, ""+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "",e);
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
    private void PostActivity(Class c, String media) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("media",media);
        startActivityForResult(intent, 0);
    }
}
