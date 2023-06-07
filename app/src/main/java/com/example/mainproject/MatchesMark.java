package com.example.mainproject;


import java.io.Serializable;

public class MatchesMark implements Serializable {
    private String id;
    private String subjId;
    private String studId;
    private String date;
    private int mark;
    public MatchesMark(){

    }
    public MatchesMark(String id, String subjId, String studId, int mark, String date){
        this.id = id;
        this.subjId = subjId;
        this.studId = studId;
        this.mark = mark;
        this.date = date;
    }
    public String getId(){return id;}
    public String getSubjId(){return subjId;}
    public String getStudId(){return studId;}
    public int getMark(){return mark;}
    public String getDate() {return date;}
}
