package com.example.mainproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MatchesStud implements Serializable {
    private String id;
    private String name;
    private String surname;
    private String second_name;
    private String birthdate;
    private String group_id;

    public MatchesStud(){
    }

    public MatchesStud (String id, String name, String surname, String second_name, String birthdate, String group_id) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.second_name = second_name;
        this.birthdate = birthdate;
        this.group_id = group_id;
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

    public String getBirthdate() {
        return birthdate;
    }

    public String getGroup_id() {
        return group_id;
    }

}