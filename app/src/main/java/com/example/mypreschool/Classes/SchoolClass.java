package com.example.mypreschool.Classes;

/**
 * Created by Nastala on 12/19/2017.
 */

public class SchoolClass {
    private String ClassName;
    private String ClassID;
    private boolean isTeacherAssigned;

    public SchoolClass() {
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getClassID() {
        return ClassID;
    }

    public void setClassID(String classID) {
        ClassID = classID;
    }

    public boolean isTeacherAssigned() {
        return isTeacherAssigned;
    }

    public void setTeacherAssigned(boolean teacherAssigned) {
        isTeacherAssigned = teacherAssigned;
    }
}
