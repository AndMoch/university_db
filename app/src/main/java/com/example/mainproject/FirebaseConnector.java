package com.example.mainproject;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class FirebaseConnector{
    final String TAG = "Firebase";
    FirebaseDatabase db;
    DatabaseReference ref;
    DatabaseReference studentsEndpoint;
    DatabaseReference groupsEndpoint;
    DatabaseReference universitiesEndpoint;
    DatabaseReference usersEndpoint;
    DatabaseReference subjsEndpoint;
    DatabaseReference marksEndpoint;
    ArrayList<MatchesStud> students = new ArrayList<>();
    ArrayList<MatchesGroup> groups = new ArrayList<>();
    ArrayList<String> groupsIds = new ArrayList<>();
    ArrayList<MatchesUniver> universities = new ArrayList<>();
    ArrayList<MatchesUser> users = new ArrayList<>();
    ArrayList<MatchesMark> marks = new ArrayList<>();
    ArrayList<MatchesSubj> subjs = new ArrayList<>();
    FirebaseConnector(){
        db = FirebaseDatabase.getInstance("https://university-db-d344b-default-rtdb.europe-west1.firebasedatabase.app");
        ref = db.getReference("main");
        studentsEndpoint = ref.child("students");
        usersEndpoint = ref.child("users");
        universitiesEndpoint = ref.child("universities");
        groupsEndpoint = ref.child("groups");
        subjsEndpoint = ref.child("subjects");
        marksEndpoint = ref.child("marks");
    }
    public void writeNewStud(String id, String name, String surname, String second_name, String birthdate,
                             String group_id){
        studentsEndpoint.child(id).setValue(new MatchesStud(id, name, surname, second_name, birthdate, group_id));
    }
    public void writeNewGroup(String id, int number, String faculty, String universityId){
        groupsEndpoint.child(id).setValue(new MatchesGroup(id, number, faculty, universityId));
    }
    public void writeNewUniversity(String id, String title, String city){
        universitiesEndpoint.child(id).setValue(new MatchesUniver(id, title, city));
    }
    public void writeNewUser(String id, String name, String surname, String secondName, String email, String universityId){
        usersEndpoint.child(id).setValue(new MatchesUser(id, name, surname, secondName, email, universityId));
    }
    public void writeNewMark(String id, String studId, String subjId, int mark, String date){
        marksEndpoint.child(id).setValue(new MatchesMark(id, subjId, studId, mark, date));
    }
    public void writeNewSubj(String id, String title, String groupId){
        subjsEndpoint.child(id).setValue(new MatchesSubj(id, title, groupId));
    }
    public void updateStud(String id, String name, String surname, String second_name, String birthdate,
                           String group_id){
        HashMap<String, Object> update = new HashMap<>();
        update.put(id + "/name", name);
        update.put(id + "/surname", surname);
        update.put(id + "/second_name", second_name);
        update.put(id + "/birthdate", birthdate);
        update.put(id + "/group_id", group_id);
        ref.child("students").updateChildren(update);
    }
    public void updateSubj(String id, String title){
        HashMap<String, Object> update = new HashMap<>();
        update.put(id + "/title", title);
        ref.child("subjects").updateChildren(update);
    }
    public void updateMark(String id, String studId, String subjId, int mark, String date){
        HashMap<String, Object> update = new HashMap<>();
        update.put(id + "/subjId", subjId);
        update.put(id + "/studId", studId);
        update.put(id + "/mark", mark);
        update.put(id + "/date", date);
        ref.child("marks").updateChildren(update);
    }
    public void updateGroup(String id, int number, String faculty){
        HashMap<String, Object> update = new HashMap<>();
        update.put(id + "/number", number);
        update.put(id + "/faculty", faculty);
        ref.child("groups").updateChildren(update);
    }
    public void updateUniversity(String id, String title, String city){
        HashMap<String, Object> update = new HashMap<>();
        update.put(id + "/title", title);
        update.put(id + "/city", city);
        ref.child("universities").updateChildren(update);
    }
    public void updateUser(String id, String name, String surname, String second_name, String email,
                           String univerId){
        HashMap<String, Object> update = new HashMap<>();
        update.put(id + "/name", name);
        update.put(id + "/surname", surname);
        update.put(id + "/second_name", second_name);
        update.put(id + "/email", email);
        update.put(id + "/universityId", univerId);
        ref.child("users").updateChildren(update);
    }
    public MatchesGroup getGroup(String id){
        for (MatchesGroup group: groups) {
            if (group.getId().equals(id)){
                return group;
            }
        }
        return null;
    }
    public ArrayList<MatchesGroup> getAllGroups(){
        return groups;
    }
    public MatchesStud getStud(String id){
        for (MatchesStud stud: students) {
            if (stud.getId().equals(id)){
                return stud;
            }
        }
        return null;
    }
    public ArrayList<MatchesStud> getAllStuds(){
        return students;
    }
    public ArrayList<MatchesStud> getAllStudsByGroup(String groupId){
        ArrayList<MatchesStud> studs = new ArrayList<>();
        for (MatchesStud stud: students){
            if(stud.getGroup_id().equals(groupId)){
                studs.add(stud);
            }
        }
        return studs;
    }
    public MatchesSubj getSubj(String subjId) {
        for (MatchesSubj s: subjs) {
            if (s.getId().equals(subjId)){
                return s;
            }
        }
        return null;
    }
    public MatchesMark getMark(String markId) {
        for (MatchesMark m: marks) {
            if (m.getId().equals(markId)){
                return m;
            }
        }
        return null;
    }
    public MatchesUniver getUniver(String univerId) {
        for (MatchesUniver u: universities) {
            if (u.getId().equals(univerId)){
                return u;
            }
        }
        return null;
    }
    public void deleteGroup(String id){
        groupsEndpoint.child(id).removeValue();
    }
    public void deleteAllGroups(){
        groupsEndpoint.removeValue();
    }
    public void deleteStud(String id){
        studentsEndpoint.child(id).removeValue();
    }
    public void deleteAllStuds(){
        studentsEndpoint.removeValue();
    }
    public void deleteAllStudsFromGroup(String groupId){
        for (MatchesStud stud: students){
            if(stud.getGroup_id().equals(groupId)){
            studentsEndpoint.child(stud.getId()).removeValue();}}
    }
    public void deleteSubj(String id) {
        subjsEndpoint.child(id).removeValue();
    }
    public void deleteMark(String id) {
        marksEndpoint.child(id).removeValue();
    }
    public void deleteAllSubjs(String groupId) {
        for (MatchesSubj subj: subjs){
            if(subj.getGroupId().equals(groupId)){
                deleteSubj(subj.getId());}
    }}
    public void deleteAllMarksOfStud(String studId) {
        for (MatchesMark mark: marks){
            if(mark.getStudId().equals(studId)){
                deleteMark(mark.getId());}
        }}
    public void deleteAllMarksOfStudForSubj(String studId, String subjId) {
        for (MatchesMark mark: marks){
            if(mark.getStudId().equals(studId) && mark.getSubjId().equals(subjId)){
                deleteMark(mark.getId());}
        }}

    public void deleteUniver(String universityId) {
        universitiesEndpoint.child(universityId).removeValue();
    }
    public void deleteUser(String id){
        usersEndpoint.child(id).removeValue();
    }

    public MatchesUser getUser(String id) {
        for (MatchesUser u: users) {
            if (u.getId().equals(id)){
                return u;
            }
        }
        return null;
    }

    public MatchesSubj getSubjByIdAndTitle(String title, String groupId) {
        for (MatchesSubj s:
             subjs) {
            if(s.getTitle().equals(title) && s.getGroupId().equals(groupId)){
                return s;
            }
        }
        return null;
    }

    public ArrayList<MatchesMark> getMarksOfStudForSubj(String studId, String subjId) {
        ArrayList<MatchesMark> ms = new ArrayList<>();
        for (MatchesMark m:
                marks) {
            if(m.getStudId().equals(studId) && m.getSubjId().equals(subjId)){
                ms.add(m);
            }
        }
        return ms;
    }

    public MatchesUser getUserByEmail(String email) {
        for (MatchesUser user: users) {
            if(user.getEmail().equals(email)){
                return user;}
        }
        return null;
    }
}