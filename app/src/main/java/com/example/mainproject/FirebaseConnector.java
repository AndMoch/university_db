package com.example.mainproject;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseConnector{
    FirebaseDatabase db;
    DatabaseReference ref;
    DatabaseReference studentsEndpoint;
    DatabaseReference groupsEndpoint;
    FirebaseConnector(){
        db = FirebaseDatabase.getInstance("https://university-db-d344b-default-rtdb.europe-west1.firebasedatabase.app");
        ref = db.getReference("university");
        studentsEndpoint = ref.child("students");
        groupsEndpoint = ref.child("groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                Log.d("ValueEventListenerSuccess", "Value is: " + value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ValueEventListenerError", "Failed to read value.", error.toException());
            }
        });
    }
    public void writeNewStud(long id, String name, String surname, String second_name, String birthdate,
                             int group_id){
        String studId = Long.toString(id);
        ref.child("students").child(studId).setValue(new MatchesStud(id, name, surname, second_name, birthdate, group_id));
    }
    public void writeNewGroup(long id, int number, String faculty){
        String groupId = Long.toString(id);
        ref.child("groups").child(groupId).setValue(new MatchesGroup(id, number, faculty));
    }

}
