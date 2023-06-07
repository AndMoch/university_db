package com.example.mainproject;

import java.io.Serializable;
import java.util.UUID;

public class MatchesGroup implements Serializable {
    private String id;
    private String faculty;
    private int number;
    String universityId;

    public MatchesGroup(){
    }

    public MatchesGroup (String id, int number, String faculty, String universityId) {
        this.id = id;
        this.number = number;
        this.faculty = faculty;
        this.universityId = universityId;
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

    public String getUniversityId() {
        return universityId;
    }
}