package com.seongnamc.sns_project.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.seongnamc.sns_project.FirebaseHelper;
import com.seongnamc.sns_project.Postinfo;
import com.seongnamc.sns_project.R;
import com.seongnamc.sns_project.adapter.PostAdapter;
import com.seongnamc.sns_project.listener.OnPostListener;
import com.seongnamc.sns_project.view.ReadContentsView;


import static com.seongnamc.sns_project.Utility.INTENT_PATH;


public class PostActivity extends BasicActivity {
    private Postinfo postinfo;
    private FirebaseHelper firebaseHelper;
    private ReadContentsView readContentsView;
    private LinearLayout contentsLayout;
    private PostAdapter postAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        postinfo = (Postinfo) getIntent().getSerializableExtra(INTENT_PATH);

        readContentsView = findViewById(R.id.readContentsView);

        firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.setOnPostLisener(onPostListener);

        contentsLayout = findViewById(R.id.contentsLayout);

        uiUpdate();


    }
    @Override
    protected void onPause() {
        super.onPause();
        //postAdapter.playerStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    postinfo = (Postinfo) data.getSerializableExtra(INTENT_PATH);
                    contentsLayout.removeAllViews();
                    uiUpdate();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.modifyPost:
                // User chose the "Settings" item, show the app settings UI...
                myStartActivity(WritePostActivity.class, postinfo);
                return true;

            case R.id.deletePost:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                firebaseHelper.storageDelete(postinfo);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(Postinfo postinfo) {
            Log.d("onDelete","삭제성공");
        }

        @Override
        public void onModify() {
            Log.d("onModify","수정성공");
        }
    };

    private void uiUpdate(){
        setToolbarTitle(postinfo.getTitle());
        readContentsView.setPostInfo(postinfo);
    }


    private void myStartActivity(Class c, Postinfo postinfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra(INTENT_PATH ,postinfo);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 0);
    }
}


