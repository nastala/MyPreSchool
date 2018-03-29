package com.example.mypreschool.Classes;

public class TeacherContact {
    private String name, value;
    private int resource;

    public TeacherContact(String name, String value, int resource) {
        this.name = name;
        this.value = value;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getResource() {
        return resource;
    }
}
