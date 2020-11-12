package com.seongnamc.sns_project;

import android.app.Activity;
import android.util.Patterns;
import android.util.StateSet;
import android.widget.Toast;

import java.net.URLConnection;


public class Utility {

    public Utility(){

    }

    public static final String INTENT_PATH = "path";
    public static final String INTENT_MEDIA = "media";
    public static final int GALLERY_IMAGE = 0;
    public static final int GALLERY_VIDIO = 1;



    public static void showToast(Activity activity, String text){
        Toast.makeText(activity, text , Toast.LENGTH_SHORT).show();
    }
    public static boolean isStorageUri(String Url){
        return Patterns.WEB_URL.matcher(Url).matches() && Url.contains("https://firebasestorage.googleapis.com/");
    }
    public static String storageUrlToName(String Url){
        return Url.split("\\?")[0].split("%2F")[Url.split("\\?")[0].split("%2F").length - 1];
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
}
