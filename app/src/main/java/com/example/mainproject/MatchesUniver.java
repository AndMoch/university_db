package com.example.mainproject;

import java.io.Serializable;

public class MatchesUniver implements Serializable {
    private String id;
    private String title;
    private String city;

    public MatchesUniver(){
    }

    public MatchesUniver(String id, String title, String city) {
        this.id = id;
        this.title = title;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }
}