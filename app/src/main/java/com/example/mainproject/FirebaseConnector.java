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
    ArrayList<MatchesStud> students = new ArrayList<>();
    ArrayList<MatchesGroup> groups = new ArrayList<>();
    ArrayList<String> groupsIds = new ArrayList<>();
    FirebaseConnector(){
        db = FirebaseDatabase.getInstance("https://university-db-d344b-default-rtdb.europe-west1.firebasedatabase.app");
        ref = db.getReference("university");
        studentsEndpoint = ref.child("students");
        groupsEndpoint = ref.child("groups");
        studentsEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                students.clear();
                for(DataSnapshot stud: snapshot.getChildren()){
                    MatchesStud student = stud.getValue(MatchesStud.class);
                    students.add(student);}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadStud:onCancelled", error.toException());
            }
        });
    }
    public void writeNewStud(String id, String name, String surname, String second_name, String birthdate,
                             String group_id){
        studentsEndpoint.child(id).setValue(new MatchesStud(id, name, surname, second_name, birthdate, group_id));
    }
    public void writeNewGroup(String id, int number, String faculty){
        groupsEndpoint.child(id).setValue(new MatchesGroup(id, number, faculty));
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
    public void updateGroup(String id, int number, String faculty){
        HashMap<String, Object> update = new HashMap<>();
        update.put(id + "/number", number);
        update.put(id + "/faculty", faculty);
        ref.child("groups").updateChildren(update);
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
}