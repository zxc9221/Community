
package com.seongnamc.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.seongnamc.sns_project.Postinfo;
import com.seongnamc.sns_project.R;
import com.seongnamc.sns_project.Utility;
import com.seongnamc.sns_project.adapter.PostAdapter;
import com.seongnamc.sns_project.listener.OnPostListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private PostAdapter postAdapter;
    private ArrayList<Postinfo> postList;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Utility utility = new Utility(this);
    private int successCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (firebaseUser == null) {
            myStartActivity(LoginActivity.class);
        } else {
            get_profile();
        }


        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        postList = new ArrayList<>();
        postAdapter = new PostAdapter(MainActivity.this , postList);
        postAdapter.setOnPostListener(onPostListener);


        findViewById(R.id.logoutButton).setOnClickListener(onClickListner);
        findViewById(R.id.writepostActionButton).setOnClickListener(onClickListner);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        recyclerView.setAdapter(postAdapter);


        /*bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        HomeFragment homeFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, homeFragment)
                                .commit();
                        return true;
                    case R.id.myInfo:
                        UserInfoFragment userInfoFragment = new UserInfoFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, userInfoFragment)
                                .commit();
                        return true;
                    case R.id.userList:
                        UserListFragment userListFragment = new UserListFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, userListFragment)
                                .commit();
                        return true;
                }
                return false;
            }
        });*/

    }

    View.OnClickListener onClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.logoutButton:
                    logout();
                    break;

                case R.id.writepostActionButton:
                    myStartActivity(WritePostActivity.class);
                    break;
            }
        }

    };


    @Override
    protected void onResume(){
        super.onResume();
        postUpdate();


    }

    private void postUpdate(){
        if (firebaseUser != null) {
            CollectionReference collectionReference = firebaseFirestore.collection("posts");


            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    postList.add(new Postinfo(
                                            document.getData().get("title").toString()
                                            , (ArrayList<String>) (document.getData().get("contents"))
                                            , document.getData().get("publisher").toString()
                                            , new Date(document.getDate("createdAt").getTime())
                                            , document.getId()));
                                }
                                postAdapter.notifyDataSetChanged();

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        myStartActivity(LoginActivity.class);
    }

    private void get_profile() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());


                        } else {
                            Log.d(TAG, "No such document");
                            myStartActivity(MemberActivity.class);
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(int position) {
            final String id = postList.get(position).getID();
            Log.e("Delete","삭제/"+id);

            ArrayList<String> contentsList = postList.get(position).getContents();
            successCnt = 0;
            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/")) {
                    successCnt++;
                    String[] list1 = contents.split("\\?");
                    String[] list2 =  list1[0].split("%2F");
                    String name = list2[list2.length - 1];

                    // Create a reference to the file to delete
                    StorageReference desertRef = storageRef.child("posts/"+id+"/"+name);

                    // Delete the file
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                            //utility.showToast("이미지를 삭제하였습니다.");
                            successCnt--;

                            delate_store(id);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            utility.showToast("이미지를 삭제하지 못하였습니다.");
                        }
                    });
                }
            }

            delate_store(id);

        }


        @Override
        public void onModify(int position) {
            String id = postList.get(position).getID();
            Log.e("Modify","수정/"+id);
            myStartActivity(WritePostActivity.class, postList.get(position));

        }
    };

    private void delate_store(String id){
        if(successCnt == 0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            utility.showToast("게시글을 삭제하였습니다.");
                            postUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error deleting document", e);
                            utility.showToast("게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 0);
    }

    private void myStartActivity(Class c, Postinfo postinfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postinfo",postinfo);

        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 0);
    }







}