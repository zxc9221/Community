package com.seongnamc.sns_project.adapter;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seongnamc.sns_project.FirebaseHelper;
import com.seongnamc.sns_project.Postinfo;
import com.seongnamc.sns_project.R;
import com.seongnamc.sns_project.activity.PostActivity;
import com.seongnamc.sns_project.activity.WritePostActivity;
import com.seongnamc.sns_project.listener.OnPostListener;
import com.seongnamc.sns_project.view.ContentsItemView;
import com.seongnamc.sns_project.view.ReadContentsView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.seongnamc.sns_project.Utility.INTENT_PATH;
import static com.seongnamc.sns_project.Utility.isStorageUri;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private String TAG = "PostAdapter";
    private FirebaseHelper firebaseHelper;
    private ArrayList<Postinfo> mDataset;
    private ArrayList<ArrayList< SimpleExoPlayer>> playerArrayList2 = new ArrayList<>();
    private Activity activity;

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        PostViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostAdapter(Activity activity, ArrayList<Postinfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;

        firebaseHelper = new FirebaseHelper(activity);

    }

    public void setOnPostLister(OnPostListener onPostListener){
        firebaseHelper.setOnPostLisener(onPostListener);
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
        final PostViewHolder PostViewHolder = new PostViewHolder(cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra(INTENT_PATH,mDataset.get(PostViewHolder.getAdapterPosition()));
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);

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

        Postinfo postinfo = mDataset.get(position);
        titleTextView.setText(postinfo.getTitle());

        ReadContentsView readContentsView = cardView.findViewById(R.id.readContentsView);
        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);

        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(postinfo)) {
            contentsLayout.setTag(postinfo);
            contentsLayout.removeAllViews();

            readContentsView.setMoreIndex(0);
            readContentsView.setPostInfo(postinfo);

            ArrayList< SimpleExoPlayer > Simple = readContentsView.getPlayerArrayList();

            if(Simple!= null) {
                playerArrayList2.add(Simple);
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
                        myStartActivity(WritePostActivity.class, mDataset.get(position));
                        return true;
                    case R.id.deletePost:

                        firebaseHelper.storageDelete(mDataset.get(position));

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

    public void playerStop() {
        for (int i = 0; i < playerArrayList2.size(); i++) {
            ArrayList< SimpleExoPlayer > Simple = playerArrayList2.get(i);
            for(int j = 0; j<Simple.size(); j++){
                SimpleExoPlayer simple = Simple.get(j);
                if(simple.getPlayWhenReady()) {
                    simple.setPlayWhenReady(false);
                }
            }
        }
    }

    private void myStartActivity(Class c, Postinfo postinfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra(INTENT_PATH ,postinfo);

        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }


}
