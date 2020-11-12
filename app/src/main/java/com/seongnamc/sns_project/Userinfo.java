package com.seongnamc.sns_project;

import android.widget.EditText;

public class Userinfo {

    private String name;
    private String phonenumber;
    private String address;
    private String birthday;
    private String photoUrl;

    public Userinfo(String name, String phonenumber, String address, String birthday, String photoUrl) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.address = address;
        this.birthday = birthday;
        this.photoUrl = photoUrl;
    }

    public Userinfo(String name, String phonenumber, String address, String birthday) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.address = address;
        this.birthday = birthday;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return this.phonenumber;
    }
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return this.birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }




}
