package com.example.mainproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Scanner;

public class AddStudent extends AppCompatActivity {
    Button btnAddStudent, btnCancelAddingStudent;
    EditText name, surname, secondName, birthdate;
    Context mContext;
    DBMatches DBConnector;
    Spinner group;
    private UUID MatchStudID;
    ArrayList<MatchesGroup> groups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        mContext = this;
        DBConnector = new DBMatches(this);
        btnAddStudent = findViewById(R.id.btnStudAddSave);
        btnCancelAddingStudent = findViewById(R.id.btnStudAddCancel);
        name = findViewById(R.id.studName);
        surname = findViewById(R.id.studSurname);
        secondName = findViewById(R.id.studSecondName);
        birthdate = findViewById(R.id.studBirthdate);
        group = findViewById(R.id.studGroup);
        groups = DBConnector.selectAllGroups();
        ArrayList<String> spinnerData = new ArrayList<>();
        for (MatchesGroup g:
             groups) {
            spinnerData.add(g.getFaculty() + " факультет, группа " + g.getNumber());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        group.setAdapter(adapter);
        if(getIntent().hasExtra("Matches")){
            MatchesStud matches=(MatchesStud) getIntent().getSerializableExtra("Matches");
            name.setText(matches.getName());
            surname.setText(matches.getSurname());
            secondName.setText(matches.getSecond_name());
            birthdate.setText(matches.getBirthdate());
            MatchStudID = matches.getId();
        }
        else
        {
            MatchStudID=-1;
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
                MatchesGroup intentGroup = DBConnector.selectGroupByParams(number, faculty);
                MatchesStud matches = new MatchesStud(MatchStudID, name.getText().toString(), surname.getText().toString(), secondName.getText().toString(), birthdate.getText().toString(), (int)intentGroup.getId());
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
}