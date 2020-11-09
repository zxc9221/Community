package com.seongnamc.sns_project.listener;

import java.util.Date;

public interface OnPostListener {
    void onDelete(String id);
    void onModify(String id, Date date);
}
