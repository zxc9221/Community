
package com.seongnamc.sns_project.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seongnamc.sns_project.R;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(user == null){
            myStartActivity(LoginActivity.class);
        }
        else{
           get_profile();
        }
        findViewById(R.id.logoutButton).setOnClickListener(onClickListner);
        findViewById(R.id.writepostActionButton).setOnClickListener(onClickListner);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
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
            switch (v.getId()){
                case R.id.logoutButton :
                    logout();
                    break;

                case R.id.writepostActionButton:
                    myStartActivity(WritePostActivity.class);
                    break;
            }
        }

    };

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        myStartActivity(LoginActivity.class);
    }
    private void get_profile(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null) {
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

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 0);
    }


}