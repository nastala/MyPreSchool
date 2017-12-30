package com.example.mypreschool.Classes;

import java.util.ArrayList;

/**
 * Created by Nastala on 12/27/2017.
 */

public class ShareActivity {
    private String activityTitle, activityDetails, activityID, sgurl, tsgurl, id;
    private int likeNumber;
    private boolean isCurrentParentLiked;
    private ArrayList<String> likedParents;

    public ShareActivity(){

    }

    public ArrayList<String> getLikedParents() {
        return likedParents;
    }

    public void setLikedParents(ArrayList<String> likedParents) {
        this.likedParents = likedParents;
    }

    public boolean getCurrentParentLiked() {
        return isCurrentParentLiked;
    }

    public void setCurrentParentLiked(boolean currentParentLiked) {
        isCurrentParentLiked = currentParentLiked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTsgurl() {
        return tsgurl;
    }

    public void setTsgurl(String tsgurl) {
        this.tsgurl = tsgurl;
    }

    public int getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getActivityDetails() {
        return activityDetails;
    }

    public void setActivityDetails(String activityDetails) {
        this.activityDetails = activityDetails;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getSgurl() {
        return sgurl;
    }

    public void setSgurl(String sgurl) {
        this.sgurl = sgurl;
    }
}
