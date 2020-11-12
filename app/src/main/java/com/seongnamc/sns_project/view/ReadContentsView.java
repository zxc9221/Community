package com.seongnamc.sns_project.view;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.video.VideoListener;
import com.seongnamc.sns_project.Postinfo;
import com.seongnamc.sns_project.R;

import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.seongnamc.sns_project.Utility.isStorageUri;

public class ReadContentsView extends LinearLayout {
    private Context context;
    private LayoutInflater layoutinflater;
    private int moreIndex = -1;
    private ArrayList<SimpleExoPlayer> playerArrayList = new ArrayList<>();

    public ReadContentsView(Context context) {
        super(context);
        this.context = context;
        InitView();
    }

    public ReadContentsView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        InitView();
    }

    private void InitView() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        layoutinflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutinflater.inflate(R.layout.view_post, this, true);

    }

    public void setMoreIndex(int moreIndex){
        this.moreIndex = moreIndex;
    }

    public void setPostInfo(Postinfo postinfo) {
        TextView createAtTextView = findViewById(R.id.createAtTextView);
        createAtTextView.setText(new SimpleDateFormat("yyyy.MM.dd-HH:mm", Locale.getDefault()).format(postinfo.getCreatedAt()));

        LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = postinfo.getContents();
        ArrayList<String> formatsList = postinfo.getFormats();

        for (int i = 0; i < contentsList.size(); i++) {
            if (i == moreIndex) {
                TextView textView = new TextView(context);
                textView.setLayoutParams(layoutParams);
                textView.setText("더보기. ");
                contentsLayout.addView(textView);
                break;
            }
            String contents = contentsList.get(i);
            String formats = formatsList.get(i);

            if(formats.equals("image")){
                ImageView imageView = (ImageView)layoutinflater.inflate(R.layout.view_contents_image, this, false);
                contentsLayout.addView(imageView);
                Glide.with(this).load(contents).centerCrop().override(1000).thumbnail(0.1f).into(imageView);
            }else if(formats.equals("vidio")){
                final PlayerView playerView = (PlayerView) layoutinflater.inflate(R.layout.view_contents_player, this, false);

                SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
                // Build the media item.
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(contents));

                // Set the media item to be played.
                player.setMediaItem(mediaItem);
                // Prepare the player.
                player.prepare();

                player.addVideoListener(new VideoListener() {
                    @Override
                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        playerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
                    }
                });

                        // Start the playback.
                player.play();

                playerArrayList.add(player);

                // Bind the player to the view.
                playerView.setPlayer(player);
                contentsLayout.addView(playerView);

            }else{
                TextView textView = (TextView)layoutinflater.inflate(R.layout.view_contents_text, this, false);
                textView.setText(contents);
                contentsLayout.addView(textView);
            }

        }

    }

    public ArrayList<SimpleExoPlayer> getPlayerArrayList(){
        return playerArrayList;
    }
}
