package com.example.mainproject;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConnector{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    DatabaseReference studentsEndpoint = reference.child("students");
    DatabaseReference groupsEndpoint = reference.child("groups");

}
