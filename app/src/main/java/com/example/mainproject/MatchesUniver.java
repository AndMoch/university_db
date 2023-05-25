package com.example.mainproject;

import java.io.Serializable;

public class MatchesUniver implements Serializable {
    private String id;
    private String title;

    public MatchesUniver(){
    }

    public MatchesUniver (String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}