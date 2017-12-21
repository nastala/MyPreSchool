package com.example.mypreschool.Classes;

/**
 * Created by Nastala on 12/21/2017.
 */

public class Teacher {
    private String teacherName;
    private String teacherClassID;
    private String teacherID;
    private int teacherPhoneNumber;

    public Teacher() {
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherClassID() {
        return teacherClassID;
    }

    public void setTeacherClassID(String teacherClassID) {
        this.teacherClassID = teacherClassID;
    }

    public int getTeacherPhoneNumber() {
        return teacherPhoneNumber;
    }

    public void setTeacherPhoneNumber(int teacherPhoneNumber) {
        this.teacherPhoneNumber = teacherPhoneNumber;
    }
}
