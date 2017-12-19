package com.example.mypreschool.Classes;

import java.util.ArrayList;

/**
 * Created by Nastala on 12/19/2017.
 */

public class School {
    private String SchoolName;
    private String SchoolID;
    private ArrayList<SchoolClass> classes;

    public School() {
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getSchoolID() {
        return SchoolID;
    }

    public void setSchoolID(String schoolID) {
        SchoolID = schoolID;
    }

    public ArrayList<SchoolClass> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<SchoolClass> classes) {
        this.classes = classes;
    }
}
