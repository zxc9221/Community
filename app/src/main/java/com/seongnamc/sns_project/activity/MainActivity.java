
package com.seongnamc.sns_project.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seongnamc.sns_project.R;
import com.seongnamc.sns_project.fragment.HomeFragment;
import com.seongnamc.sns_project.fragment.PostFragment;
import com.seongnamc.sns_project.fragment.UserInfoFragment;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbarTitle(getResources().getString(R.string.app_name));
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        init_profile(firebaseUser);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                    case R.id.post:
                        PostFragment userListFragment = new PostFragment();
                        Log.d("aass","home");
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, userListFragment)
                                .commit();
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                init_profile(firebaseUser);
                break;
        }
    }




    @Override
    protected void onResume(){
        super.onResume();
        //postUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    private void init_profile(FirebaseUser firebaseUser) {
        if (firebaseUser == null) {
            myStartActivity(LoginActivity.class);
        } else {

            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
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
                                myStartActivity(UserActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

    }



    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }



}