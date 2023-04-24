package com.example.mainproject;

import java.io.Serializable;

public class MatchesGroup implements Serializable {
    private long id;
    private String faculty;
    private int number;

    public MatchesGroup (long id, int number, String faculty) {
        this.id = id;
        this.number = number;
        this.faculty=faculty;
    }

    public long getId() {
        return id;
    }

    public String getFaculty() {
        return faculty;
    }

    public int getNumber() {
        return number;
    }
}