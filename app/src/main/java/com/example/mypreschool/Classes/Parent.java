package com.example.mypreschool.Classes;

/**
 * Created by Nastala on 12/11/2017.
 */

public class Parent {
    private String isim, uid;

    public Parent() {
    }

    public Parent(String isim, String uid) {
        this.isim = isim;
        this.uid = uid;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
