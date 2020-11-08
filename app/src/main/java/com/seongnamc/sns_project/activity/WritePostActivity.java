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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.seongnamc.sns_project.R;
import com.seongnamc.sns_project.Postinfo;

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
    private RelativeLayout buttonsBackgroundLayout;
    private ImageView selectedImageView;
    private EditText selectedEditText;
    int successCount = 0;

    private RelativeLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        parent =  findViewById(R.id.contentslayout);
        buttonsBackgroundLayout = findViewById(R.id.ButtonsBackgroundLayout);

        findViewById(R.id.contentEditText).setOnFocusChangeListener(onFocusChangeListener);
        findViewById(R.id.titleeditText).setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(hasFocus){
                    selectedEditText = null;
                }
            }
        });

        findViewById(R.id.addpostButton).setOnClickListener(onClickListner);
        findViewById(R.id.picturebutton).setOnClickListener(onClickListner);
        findViewById(R.id.vidiobutton).setOnClickListener(onClickListner);
        buttonsBackgroundLayout.setOnClickListener(onClickListner);

        findViewById(R.id.imageModify).setOnClickListener(onClickListner);
        findViewById(R.id.vidioModify).setOnClickListener(onClickListner);
        findViewById(R.id.deletePost).setOnClickListener(onClickListner);

        loaderLayout = findViewById(R.id.loaderLayout);


    }

    View.OnClickListener onClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.addpostButton :
                    storageUploader();
                    break;
                case R.id.picturebutton :
                    PostActivity(GalleryActivity.class, "image");
                    break;
                case R.id.vidiobutton :
                    PostActivity(GalleryActivity.class, "vidio");
                    break;

                case R.id.ButtonsBackgroundLayout :
                    if(buttonsBackgroundLayout.getVisibility() == View.VISIBLE){
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;

                case R.id.vidioModify :
                    PostActivity(GalleryActivity.class, "vidio", 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.imageModify :
                    PostActivity(GalleryActivity.class, "image", 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.deletePost:
                    parent.removeView((View)selectedImageView.getParent());
                    buttonsBackgroundLayout.setVisibility(View.GONE);
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

                    LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    if(selectedEditText == null){
                        parent.addView(linearLayout);
                    }
                    else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i) == selectedEditText.getParent()) {
                                parent.addView(linearLayout, i+1);
                                break;
                            }
                        }
                    }

                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                            Log.e("onClick",selectedImageView.toString());
                        }
                    });
                    Glide.with(this).load(Path).centerCrop().override(500).into(imageView);
                    linearLayout.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("내용");
                    editText.setOnFocusChangeListener(onFocusChangeListener);
                    linearLayout.addView(editText);
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK) {
                    String Path = data.getStringExtra("ProfilePath");
                    Glide.with(this).load(Path).centerCrop().override(500).into(selectedImageView);
                }
                break;
        }
    }

    private void storageUploader() {
        final String title = ((EditText) findViewById(R.id.titleeditText)).getText().toString();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final DocumentReference documentReference = firestore.collection("posts").document();

        if (title.length() > 0) {
            // Create a storage reference from our app
            ArrayList<String> contentsList = new ArrayList<>();
            int pathCount = 0;

            loaderLayout.setVisibility(View.VISIBLE);

            for (int i = 0; i < parent.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt((i));
                for(int j = 0; j < linearLayout.getChildCount(); j++){
                    View view = linearLayout.getChildAt(j);
                    if (view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                        }

                    } else {
                        contentsList.add(pathList.get(pathCount));

                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + ".jpg");

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
                                            Log.d("dddddd","in");
                                            if(pathList.size() == successCount){
                                                Postinfo info = new Postinfo(title, contentsList, user.getUid(), new Date());
                                                storeUploader(documentReference, info);
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

            }
            if(pathList.size() == 0){
                Postinfo info = new Postinfo(title, contentsList, user.getUid(), new Date());
                storeUploader(documentReference, info);
            }
        } else {
            StartToast("제목을 입력해주세요.");
        }
    }



    private void storeUploader(DocumentReference  documentReference, Postinfo info){
        documentReference.set(info)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    loaderLayout.setVisibility(View.GONE);
                    myStartActivity(MainActivity.class);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                    loaderLayout.setVisibility(View.GONE);
                }
            });
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus){
            if(hasFocus){
                selectedEditText = (EditText)v;
            }
        }
    };

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
    private void PostActivity(Class c, String media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("media",media);
        startActivityForResult(intent, requestCode);
    }
}
