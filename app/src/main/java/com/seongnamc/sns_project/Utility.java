package com.seongnamc.sns_project;

import android.app.Activity;
import android.widget.Toast;

public class Utility {
    private Activity activity;

    public Utility(Activity activity){
        this.activity = activity;
    }
    public void showToast(String text){
        Toast.makeText(activity, text , Toast.LENGTH_SHORT).show();
    }
}
