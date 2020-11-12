package com.seongnamc.sns_project.listener;

import com.seongnamc.sns_project.Postinfo;

import java.util.Date;

public interface OnPostListener {
    void onDelete(Postinfo postinfo);
    void onModify();
}
