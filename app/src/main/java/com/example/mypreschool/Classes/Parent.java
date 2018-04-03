package com.example.mypreschool.Classes;

/**
 * Created by Nastala on 12/11/2017.
 */

public class Parent {
    private String isim, uid;
    private boolean check;

    public Parent() {
        check = false;
    }

    public Parent(String isim, String uid) {
        this.isim = isim;
        this.uid = uid;
        check = false;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
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
