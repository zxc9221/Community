package com.seongnamc.sns_project.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.seongnamc.sns_project.Postinfo;
import com.seongnamc.sns_project.R;
import com.seongnamc.sns_project.activity.UserActivity;
import com.seongnamc.sns_project.activity.WritePostActivity;
import com.seongnamc.sns_project.adapter.PostAdapter;
import com.seongnamc.sns_project.listener.OnPostListener;

import java.util.ArrayList;
import java.util.Date;

public class PostFragment extends Fragment  {
    private static final String TAG = "PostFragment";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private PostAdapter postAdapter;
    private ArrayList<Postinfo> postList;
    private FirebaseStorage storage;
    private boolean updateing = false;
    private boolean toScroling = false;


    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        postAdapter.playerStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity(), postList);
        postAdapter.setOnPostLister(onPostListener);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        view.findViewById(R.id.writepostActionButton).setOnClickListener(onClickListner);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                if(newState == 1 && firstVisibleItemPosition == 0 ){
                    toScroling = true;
                }if(newState == 0 && toScroling == true){
                    toScroling = false;
                    postUpdate(true);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleCount = layoutManager.getChildCount();
                int totalthemeCounnt = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

                if(totalthemeCounnt - 2 <= lastVisibleItemPosition && updateing == false) {
                    postUpdate(false);
                }
                if(firstVisibleItemPosition > 0){
                    toScroling = false;
                }
            }
        });

        postUpdate(false);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(data != null){
                    postUpdate(true);
                }


                break;
        }
    }


    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(Postinfo postinfo) {
            Log.e("onDelete","식제");
            postList.remove(postinfo);
            postAdapter.notifyDataSetChanged();
        }

        @Override
        public void onModify() {
            Log.e("onModify","수정");
        }
    };

    private void postUpdate(final boolean clear){
        updateing = true;
        Date date = (postList.size() == 0 || clear == true)? new Date(): postList.get(postList.size() - 1).getCreatedAt();
        CollectionReference collectionReference = firebaseFirestore.collection("posts");

        collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt", date).limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear) {
                                postList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                postList.add(new Postinfo(
                                        document.getData().get("title").toString()
                                        , (ArrayList<String>) (document.getData().get("contents"))
                                        , (ArrayList<String>) (document.getData().get("formats"))
                                        , document.getData().get("publisher").toString()
                                        , new Date(document.getDate("createdAt").getTime())
                                        , document.getId()));
                            }
                            postAdapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updateing = false;
                    }
                });


    }


    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 0);
    }

    View.OnClickListener onClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.writepostActionButton:
                    myStartActivity(WritePostActivity.class);
                    break;
            }
        }

    };


}