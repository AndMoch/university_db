package com.example.mainproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.UUID;

public class AddStudentToGroup extends AppCompatActivity {
    Button btnAddGroupStudent, btnCancelAddingGroupStudent;
    EditText name, surname, secondName;
    DatePicker birthdate;
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
        Calendar today = Calendar.getInstance();
        birthdate.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        });
        btnAddGroupStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day = String.valueOf(birthdate.getDayOfMonth()).length() == 1 ? "0" +
                        birthdate.getDayOfMonth() : String.valueOf(birthdate.getDayOfMonth());
                String month = String.valueOf((birthdate.getMonth() + 1)).length() == 1 ? "0" +
                        (birthdate.getMonth() + 1) : String.valueOf((birthdate.getMonth() + 1));

                MatchesStud matches = new MatchesStud(MatchStudID, name.getText().toString(),
                        surname.getText().toString(), secondName.getText().toString(),
                        day + "." + month + "." + birthdate.getYear(), groupId);
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