package com.example.mainproject;

import java.io.Serializable;

public class MatchesStud implements Serializable {
    private long id;
    private String name;
    private String surname;
    private String second_name;
    private String birthdate;
    private int group_id;
    public MatchesStud(){

    }

    public MatchesStud (long id, String name, String surname, String second_name, String birthdate, int group_id) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.second_name = second_name;
        this.birthdate = birthdate;
        this.group_id = group_id;
    }

    public long getId() {
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

    public String getBirthdate() {
        return birthdate;
    }

    public int getGroup_id() {
        return group_id;
    }
}