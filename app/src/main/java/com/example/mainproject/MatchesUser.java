package com.example.mainproject;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class MatchesUser implements Serializable {
    private String id;
    private String name;
    private String surname;
    private String second_name;
    private String email;

    private String universityId;

    public MatchesUser(){

    }

    public MatchesUser(String id, String name, String surname, String second_name, String email, String universityId) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.second_name = second_name;
        this.email = email;
        this.universityId = universityId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getSecond_name() {
        return second_name;
    }

    public String getEmail() {
        return email;
    }

    public String getUniversityId() {
        return universityId;
    }
}