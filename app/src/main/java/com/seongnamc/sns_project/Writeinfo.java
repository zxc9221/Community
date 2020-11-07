package com.seongnamc.sns_project;

public class Writeinfo {
    private String title;
    private String contents;
    private String publisher;
    private String imageUrl;

    public  Writeinfo(String title, String contents, String publisher) {
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
    }
    public  Writeinfo(String title, String contents, String publisher, String imageUrl) {
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.imageUrl = imageUrl;
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

    public String getPublisher() {
        return this.publisher;
    }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
