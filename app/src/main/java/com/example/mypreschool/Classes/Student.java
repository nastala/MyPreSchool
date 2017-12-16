package com.example.mypreschool.Classes;

/**
 * Created by Nastala on 12/11/2017.
 */

public class Student {
    private String name, sgurl, parentID, studentID;

    public Student() {
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSgurl() {
        return sgurl;
    }

    public void setSgurl(String sgurl) {
        this.sgurl = sgurl;
    }
}
