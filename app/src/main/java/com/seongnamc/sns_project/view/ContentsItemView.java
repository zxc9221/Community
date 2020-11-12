package com.seongnamc.sns_project.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.seongnamc.sns_project.R;

import org.jetbrains.annotations.Nullable;

public class ContentsItemView extends LinearLayout {
    private ImageView imageView;
    private EditText editText;

    public ContentsItemView(Context context) {
        super(context);
        InitView();
    }

    public ContentsItemView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        InitView();
    }

    private void InitView(){
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
         setOrientation(LinearLayout.VERTICAL);

        LayoutInflater layoutinflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addView(layoutinflater.inflate(R.layout.view_contents_image, this, false));
        addView(layoutinflater.inflate(R.layout.view_contents_edit_text, this, false));

        imageView = findViewById(R.id.contentsImageview);
        editText = findViewById(R.id.contentEditText);
    }

    public void setImage(String path){
        Glide.with(this).load(path).centerCrop().override(1000).into(imageView);
    }

    public void setText(String text){
        editText.setText(text);
    }

    public void setOnClickListener(OnClickListener onClickListener){
        imageView.setOnClickListener(onClickListener);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener){
        editText.setOnFocusChangeListener(onFocusChangeListener);
    }
}
