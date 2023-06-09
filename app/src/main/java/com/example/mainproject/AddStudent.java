package com.example.mainproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.UUID;

public class AddStudent extends AppCompatActivity {
    Button btnAddStudent, btnCancelAddingStudent;
    EditText name, surname, secondName;
    DatePicker birthdate;
    Context mContext;
    final String TAG = "Firebase";
    FirebaseConnector dbConnect;
    Spinner group;
    private String MatchStudID;
    ArrayList<MatchesGroup> groups = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        mContext = this;
        dbConnect = new FirebaseConnector();
        btnAddStudent = findViewById(R.id.btnStudAddSave);
        btnCancelAddingStudent = findViewById(R.id.btnStudAddCancel);
        name = findViewById(R.id.studName);
        surname = findViewById(R.id.studSurname);
        secondName = findViewById(R.id.studSecondName);
        birthdate = findViewById(R.id.studBirthdate);
        group = findViewById(R.id.studGroup);
        if(getIntent().hasExtra("Matches")){
            MatchesStud matches=(MatchesStud) getIntent().getSerializableExtra("Matches");
            name.setText(matches.getName());
            surname.setText(matches.getSurname());
            secondName.setText(matches.getSecond_name());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate date = LocalDate.parse(matches.getBirthdate(), formatter);
            birthdate.updateDate(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
            MatchStudID = matches.getId();
        }
        else
        {
            MatchStudID = UUID.randomUUID().toString();
            while (dbConnect.getStud(MatchStudID) != null)
                MatchStudID = UUID.randomUUID().toString();
            Calendar today = Calendar.getInstance();
            birthdate.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                }
            });
        }
        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupString = group.getSelectedItem().toString();
                Scanner scan = new Scanner(groupString);
                String faculty = scan.next();
                String f = scan.next();
                String g = scan.next();
                int number = Integer.parseInt(scan.next());
                MatchesGroup intentGroup = new MatchesGroup();
                for (MatchesGroup group: dbConnect.groups){
                    if (group.getNumber() == number && group.getFaculty().equals(faculty)){
                        intentGroup = group;
                    }
                }
                String day = String.valueOf(birthdate.getDayOfMonth()).length() == 1 ? "0" +
                        birthdate.getDayOfMonth() : String.valueOf(birthdate.getDayOfMonth());
                String month = String.valueOf((birthdate.getMonth() + 1)).length() == 1 ? "0" +
                        (birthdate.getMonth() + 1) : String.valueOf((birthdate.getMonth() + 1));
                MatchesStud matches = new MatchesStud(MatchStudID, name.getText().toString(), surname.getText().toString(), secondName.getText().toString(),
                        day + "." + month + "." + birthdate.getYear(),
                        intentGroup.getId());
                Intent intent = getIntent();
                intent.putExtra("Matches", matches);
                setResult(1, intent);
                finish();
            }
        });
        btnCancelAddingStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        dbConnect.groupsEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbConnect.groups.clear();
                for(DataSnapshot gr: snapshot.getChildren()){
                    MatchesGroup group = gr.getValue(MatchesGroup.class);
                    dbConnect.groups.add(group);
                    dbConnect.groupsIds.add(group.getId());}
                System.out.println(dbConnect.groups.toString());
                groups = dbConnect.getAllGroups();
                ArrayList<String> spinnerData = new ArrayList<>();
                for (MatchesGroup g:
                        groups) {
                    spinnerData.add(g.getFaculty() + " факультет, группа " + g.getNumber());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, spinnerData);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                group.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadGroup:onCancelled", error.toException());
            }
        });
        dbConnect.studentsEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbConnect.students.clear();
                for(DataSnapshot stud: snapshot.getChildren()){
                    MatchesStud student = stud.getValue(MatchesStud.class);
                    dbConnect.students.add(student);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadStud:onCancelled", error.toException());
            }
        });
    }
}