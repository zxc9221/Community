package com.seongnamc.sns_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seongnamc.sns_project.R;
import com.seongnamc.sns_project.activity.LoginActivity;
import com.seongnamc.sns_project.activity.UserActivity;
import com.seongnamc.sns_project.activity.WritePostActivity;

public class UserInfoFragment extends Fragment {
    private static final String TAG = "UserFragment";
    private DocumentReference documentReference;


    public UserInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userinfo, container, false);
        final TextView nametext = view.findViewById(R.id.nameText);
        final TextView addresstext = view.findViewById(R.id.addressText);
        final TextView phonnumbertext = view.findViewById(R.id.phonenumberText);
        final TextView birthdaytext = view.findViewById(R.id.birthdayText);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        documentReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: ");
                            nametext.setText(document.getData().get("name").toString());
                            birthdaytext.setText(document.getData().get("birthday").toString());
                            phonnumbertext.setText(document.getData().get("phonenumber").toString());
                            addresstext.setText(document.getData().get("address").toString());

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        view.findViewById(R.id.logoutButton).setOnClickListener(onClickListner);
        // Inflate the layout for this fragment
        return view;

    }

    View.OnClickListener onClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.logoutButton:
                    logout();
                    break;

            }
        }

    };


    private void logout() {
        FirebaseAuth.getInstance().signOut();
        myStartActivity(LoginActivity.class);
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 0);
    }
}