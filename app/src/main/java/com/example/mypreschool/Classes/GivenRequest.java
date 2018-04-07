package com.example.mypreschool.Classes;

public class GivenRequest {
    private String studentID, permissionRequestID, studentName;
    private boolean permission;

    public GivenRequest(String studentID, String permissionRequestID, boolean permission) {
        this.studentID = studentID;
        this.permissionRequestID = permissionRequestID;
        this.permission = permission;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getPermissionRequestID() {
        return permissionRequestID;
    }

    public String getStudentName() {
        return studentName;
    }

    public boolean isPermission() {
        return permission;
    }
}
