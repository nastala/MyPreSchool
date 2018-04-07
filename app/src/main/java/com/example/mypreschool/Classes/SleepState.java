package com.example.mypreschool.Classes;

public class SleepState {
    private String studentName, status;

    public SleepState(String studentName, String status) {
        this.studentName = studentName;
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStatus() {
        return status;
    }
}
