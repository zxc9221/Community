package com.seongnamc.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.seongnamc.sns_project.Utility;

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
    private Utility utility = new Utility(this);
    private EditText contentsEditText;
    private EditText titleEditText;
    private FirebaseStorage storage;
    private StorageReference storageRef;


    int successCount = 0;
    int pathCount = 0;

    private RelativeLayout loaderLayout;

    private Postinfo postinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        parent =  findViewById(R.id.contentslayout);
        contentsEditText =  findViewById(R.id.contentEditText);
        titleEditText =  findViewById(R.id.titleEditText);
        buttonsBackgroundLayout = findViewById(R.id.ButtonsBackgroundLayout);
        buttonsBackgroundLayout.setOnClickListener(onClickListner);
        loaderLayout = findViewById(R.id.loaderLayout);

        findViewById(R.id.addpostButton).setOnClickListener(onClickListner);
        findViewById(R.id.picturebutton).setOnClickListener(onClickListner);
        findViewById(R.id.vidiobutton).setOnClickListener(onClickListner);


        findViewById(R.id.imageModify).setOnClickListener(onClickListner);
        findViewById(R.id.vidioModify).setOnClickListener(onClickListner);
        findViewById(R.id.deletePost).setOnClickListener(onClickListner);


        findViewById(R.id.contentEditText).setOnFocusChangeListener(onFocusChangeListener);
        findViewById(R.id.titleEditText).setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(hasFocus){
                    selectedEditText = null;
                }
            }
        });
        postinfo = (Postinfo) getIntent().getSerializableExtra("postinfo");

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        modifyPostInit();



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
                    View selectedView = (View) selectedImageView.getParent();

                    String[] list1 = pathList.get(parent.indexOfChild(selectedView) - 1).split("\\?");
                    String[] list2 =  list1[0].split("%2F");
                    String name = list2[list2.length - 1];

                    StorageReference desertRef = storageRef.child("posts/"+postinfo.getID()+"/"+ name);

                    // Delete the file
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                            utility.showToast("이미지를 삭제하였습니다.");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            utility.showToast("이미지를 삭제하지 못하였습니다.");
                        }
                    });
                    pathList.remove(parent.indexOfChild(selectedView) - 1);

                    parent.removeView(selectedView);
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
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
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
                    pathList.set(parent.indexOfChild((View) selectedImageView.getParent()) - 1,Path);
                    Glide.with(this).load(Path).centerCrop().override(500).into(selectedImageView);
                }
                break;
        }
    }

    private void storageUploader(){
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();
        if (title.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            // Create a storage reference from our app
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();


            final DocumentReference documentReference = postinfo == null ? firestore.collection("posts").document() : firestore.collection("posts").document(postinfo.getID());
            final Date date = postinfo == null ? new Date() : postinfo.getCreatedAt();
            ArrayList<String> contentsList = new ArrayList<>();

            pathCount = 0;
            successCount = 0;
            for (int i = 0; i < parent.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt((i));
                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View view = linearLayout.getChildAt(j);
                    if (view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                        }

                    } else if(!(Patterns.WEB_URL.matcher(pathList.get(pathCount)).matches()) ){
                        successCount++;
                        contentsList.add(pathList.get(pathCount));
                        String extension = pathList.get(pathCount).substring(pathList.get(pathCount).lastIndexOf('.') + 1);
                        String[] pathArray = pathList.get(pathCount).split("\\.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);

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
                                            successCount--;
                                            if (0 == successCount) {
                                                Postinfo info = new Postinfo(title, contentsList, user.getUid(), date);
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
            if (successCount == 0) {

                Postinfo info = new Postinfo(title, contentsList, user.getUid(), date);
                storeUploader(documentReference, info);
            }
        } else {
            utility.showToast("제목을 입력해주세요.");
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

    private void  modifyPostInit(){
        if(postinfo != null){
            titleEditText.setText(postinfo.getTitle());

            ArrayList<String> contentsList = postinfo.getContents();
            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/")) {
                    pathList.add(contents);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    parent.addView(linearLayout);

                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                            Log.e("onClick",selectedImageView.toString());
                        }
                    });
                    Glide.with(this).load(contents).centerCrop().override(500).into(imageView);
                    linearLayout.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    if(i < contentsList.size() - 1){
                        String nextContents = contentsList.get(i+1);
                        if(!(Patterns.WEB_URL.matcher(nextContents).matches() || nextContents.contains("https://firebasestorage.googleapis.com/"))){
                            editText.setText(nextContents);
                        }
                    }
                    editText.setOnFocusChangeListener(onFocusChangeListener);
                    linearLayout.addView(editText);

                }else if(i == 0){
                    contentsEditText.setText(contents);
                }

            }
        }
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
