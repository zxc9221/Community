package com.seongnamc.sns_project;

public class Writeinfo {
    private String title;
    private String contents;

    public  Writeinfo(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String name) {
        this.title = title;
    }

    public String getContents() {
        return this.contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }
}
