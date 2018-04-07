package com.example.mypreschool.Classes;

import java.util.Date;

public class PermissionRequestClass {
    private String id, title, details, givenRequestId;
    private double price;
    private Date date;
    private boolean check, isGivenRequest, allowed;

    public PermissionRequestClass(String id, String title, String details, double price, Date date) {
        this.title = title;
        this.details = details;
        this.price = price;
        this.date = date;
        this.id = id;
        check = false;
        isGivenRequest = false;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public String getGivenRequestId() {
        return givenRequestId;
    }

    public void setGivenRequestId(String givenRequestId) {
        this.givenRequestId = givenRequestId;
    }

    public boolean isGivenRequest() {
        return isGivenRequest;
    }

    public void setGivenRequest(boolean givenRequest) {
        isGivenRequest = givenRequest;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public double getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }
}
