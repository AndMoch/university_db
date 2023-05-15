package com.example.mainproject;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.UUID;

public class FirebaseConnector{
    final String TAG = "Firebase";
    FirebaseDatabase db;
    DatabaseReference ref;
    DatabaseReference studentsEndpoint;
    DatabaseReference groupsEndpoint;
    FirebaseConnector(){
        db = FirebaseDatabase.getInstance("https://university-db-d344b-default-rtdb.europe-west1.firebasedatabase.app");
        ref = db.getReference("university");
        studentsEndpoint = ref.child("students");
        groupsEndpoint = ref.child("groups");
        ValueEventListener studentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MatchesStud student = snapshot.getValue(MatchesStud.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadStud:onCancelled", error.toException());
            }
        };
        ValueEventListener groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MatchesGroup group = snapshot.getValue(MatchesGroup.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadGroup:onCancelled", error.toException());
            }
        };
        studentsEndpoint.addValueEventListener(studentListener);
        groupsEndpoint.addValueEventListener(groupListener);
    }
    public void writeNewStud(String id, String name, String surname, String second_name, String birthdate,
                             String group_id){
        ref.child("students").child(id).setValue(new MatchesStud(id, name, surname, second_name, birthdate, group_id));
    }
    public void writeNewGroup(String id, int number, String faculty){
        ref.child("groups").child(id).setValue(new MatchesGroup(id, number, faculty));
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
}
