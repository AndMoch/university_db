package com.example.mainproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class AddStudentToGroup extends AppCompatActivity {
    Button btnAddGroupStudent, btnCancelAddingGroupStudent;
    EditText name, surname, secondName, birthdate;
    Context mContext;
    final String TAG = "Firebase";
    FirebaseConnector dbConnect;
    private String MatchStudID;
    private String groupId;
    ArrayList<MatchesGroup> groups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_to_group);
        mContext = this;
        dbConnect = new FirebaseConnector();
        btnAddGroupStudent = findViewById(R.id.btnStudGroupAddSave);
        btnCancelAddingGroupStudent = findViewById(R.id.btnStudAddGroupCancel);
        name = findViewById(R.id.studGroupName);
        surname = findViewById(R.id.studGroupSurname);
        secondName = findViewById(R.id.studGroupSecondName);
        birthdate = findViewById(R.id.studGroupBirthdate);
        groupId = getIntent().getStringExtra("groupId");
        MatchStudID = UUID.randomUUID().toString();
        while (dbConnect.getStud(MatchStudID) != null){
            MatchStudID = UUID.randomUUID().toString();}
        btnAddGroupStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchesStud matches = new MatchesStud(MatchStudID, name.getText().toString(), surname.getText().toString(), secondName.getText().toString(), birthdate.getText().toString(), groupId);
                Intent intent = getIntent();
                intent.putExtra("Matches", matches);
                setResult(1, intent);
                finish();
            }
        });
        btnCancelAddingGroupStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}