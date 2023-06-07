package com.example.mainproject;

import java.io.Serializable;

public class MatchesSubj implements Serializable {
    private String id;
    private String title;
    private String groupId;
    MatchesSubj(){}
    MatchesSubj(String id, String title, String groupId){
        this.id = id;
        this.title = title;
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGroupId() {
        return groupId;
    }
}
