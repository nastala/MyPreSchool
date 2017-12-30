package com.example.mypreschool.Classes;

import java.io.Serializable;

/**
 * Created by Nastala on 12/11/2017.
 */

public class Student implements Serializable{
    private String name, sgurl, parentID, studentID, classID, schoolID, stat;
    private boolean isStated;

    public Student() {
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public boolean isStated() {
        return isStated;
    }

    public void setStated(boolean stated) {
        isStated = stated;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
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
