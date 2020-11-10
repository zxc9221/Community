package com.seongnamc.sns_project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Postinfo implements Serializable {
    private String title;
    private ArrayList<String> contents;
    private String publisher;
    private Date createdAt;
    private String id;

    public Postinfo(String title, ArrayList<String> contents, String publisher, Date createdAt, String id) {
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
    }
    public Postinfo(String title, ArrayList<String> contents, String publisher, Date createdAt) {
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String name) {
        this.title = title;
    }

    public ArrayList<String> getContents() {
        return this.contents;
    }
    public void setContents(ArrayList<String> contents) {
        this.contents = contents;
    }

    public String getPublisher() {
        return this.publisher;
    }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getID() {
        return this.id;
    }
    public void setID(String id) { this.id = id; }
}
