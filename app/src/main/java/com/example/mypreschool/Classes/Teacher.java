package com.example.mypreschool.Classes;

/**
 * Created by Nastala on 12/21/2017.
 */

public class Teacher {
    private String teacherName;
    private String teacherClassID;
    private String teacherID;
    private String TeacherSchoolID;
    private String teacherPhoneNumber;
    private String teacherEmail;

    public Teacher() {
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public String getTeacherSchoolID() {
        return TeacherSchoolID;
    }

    public void setTeacherSchoolID(String teacherSchoolID) {
        TeacherSchoolID = teacherSchoolID;
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

    public String getTeacherPhoneNumber() {
        return teacherPhoneNumber;
    }

    public void setTeacherPhoneNumber(String teacherPhoneNumber) {
        this.teacherPhoneNumber = teacherPhoneNumber;
    }
}
