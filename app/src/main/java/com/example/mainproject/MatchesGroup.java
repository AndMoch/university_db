package com.example.mainproject;

import java.io.Serializable;
import java.util.UUID;

public class MatchesGroup implements Serializable {
    private String id;
    private String faculty;
    private int number;
    public MatchesGroup(){
    }

    public MatchesGroup (String id, int number, String faculty) {
        this.id = id;
        this.number = number;
        this.faculty=faculty;
    }

    public String getId() {
        return id;
    }

    public String getFaculty() {
        return faculty;
    }

    public int getNumber() {
        return number;
    }
}