package com.seongnamc.sns_project;

import android.app.ActionBar;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.seongnamc.sns_project.activity.MainActivity;
import com.seongnamc.sns_project.listener.OnPostListener;

import java.util.ArrayList;
import java.util.Date;

import static com.seongnamc.sns_project.Utility.isStorageUri;
import static com.seongnamc.sns_project.Utility.showToast;
import static com.seongnamc.sns_project.Utility.storageUrlToName;

public class FirebaseHelper {
    private Activity activity;
    private OnPostListener onPostListener;
    private int successCnt;

    public FirebaseHelper(Activity activity){
        this.activity = activity;
    }

    public void setOnPostLisener(OnPostListener onPostLisener){
        this.onPostListener = onPostLisener;
    }

    public void storageDelete(final Postinfo postinfo){
        final String id = postinfo.getID();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Log.e("Delete","삭제/"+id);


        ArrayList<String> contentsList = postinfo.getContents();
        successCnt = 0;
        for (int i = 0; i < contentsList.size(); i++) {
            String contents = contentsList.get(i);
            if (isStorageUri(contents)) {
                successCnt++;
                // Create a reference to the file to delete
                StorageReference desertRef = storageRef.child("posts/"+id+"/"+storageUrlToName(contents));

                // Delete the file
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        //utility.showToast("이미지를 삭제하였습니다.");
                        successCnt--;

                        delete_store(id, postinfo);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        showToast(activity,"이미지를 삭제하지 못하였습니다.");
                    }
                });
            }
        }

        delete_store(id, postinfo);

    }

    private void delete_store(final String id, Postinfo postinfo){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if(successCnt == 0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            showToast(activity,"게시글을 삭제하였습니다.");
                            onPostListener.onDelete(postinfo);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error deleting document", e);
                            showToast(activity,"게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }





}
