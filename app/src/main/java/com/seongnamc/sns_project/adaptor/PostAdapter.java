package com.seongnamc.sns_project.adaptor;


import android.app.Activity;
import android.media.MediaRouter;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seongnamc.sns_project.Postinfo;
import com.seongnamc.sns_project.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.logging.SimpleFormatter;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private String TAG = "PostAdapter";
    private ArrayList<Postinfo> mDataset;
    private Activity activity;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        PostViewHolder(Activity activity, CardView v, Postinfo postinfo) {
            super(v);
            cardView = v;

            LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ArrayList<String> contentsList = postinfo.getContents();

            if (contentsLayout.getChildCount() == 0) {
                for (int i = 0; i < contentsList.size(); i++) {
                    String contents = contentsList.get(i);
                    if (Patterns.WEB_URL.matcher(contents).matches()) {
                        //if()
                        ImageView imageView = new ImageView(activity);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setAdjustViewBounds(true);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                        contentsLayout.addView(imageView);
                    } else {
                        TextView textView = new TextView(activity);
                        textView.setLayoutParams(layoutParams);
                        contentsLayout.addView(textView);

                    }
                }
            }
            cardView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostAdapter(Activity activity, ArrayList<Postinfo> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final PostViewHolder PostViewHolder = new PostViewHolder(activity, cardView, mDataset.get(viewType));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cardView.findViewById(R.id.menuCadeView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, PostViewHolder.getAdapterPosition());
            }
        });

        return PostViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView titleTextView = cardView.findViewById(R.id.tiltleTextView);
        titleTextView.setText(mDataset.get(position).getTitle());

        TextView createAtTextView = cardView.findViewById(R.id.createAtTextView);
        createAtTextView.setText(new SimpleDateFormat("yyyy.MM.dd-hh:mm", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ArrayList<String> contentsList = mDataset.get(position).getContents();


        for (int i = 0; i < contentsList.size(); i++) {
            String contents = contentsList.get(i);
            if (Patterns.WEB_URL.matcher(contents).matches()) {
                Glide.with(activity).load(contents).centerCrop().override(1000).thumbnail(0.1f).into((ImageView)contentsLayout.getChildAt(i));
            } else {
                ((TextView)contentsLayout.getChildAt(i)).setText(contents);

            }
        }
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modifyPost:

                        return true;
                    case R.id.deletePost:
                        delete(position);

                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post_navigation, popup.getMenu());
        popup.show();
    }

    private void delete(int position){
        firebaseFirestore.collection("posts").document(mDataset.get(position).getID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        StartToast("게시글을 삭제하였습니다.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error deleting document", e);
                        StartToast("게시글을 삭제하지 못하였습니다.");
                    }
                });

    }
    private void StartToast(String text){  Toast.makeText(activity, text , Toast.LENGTH_SHORT).show();
    }


}
