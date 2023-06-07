package com.example.mainproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddMark extends AppCompatActivity {
    Button btnMarkToSubjAddSave, btnMarkToSubjAddCancel;
    DatePicker markDateInSubj;
    Spinner markItSelfInSubj;
    Context mContext;
    final String TAG = "Firebase";
    FirebaseConnector dbConnect;
    private String MatchMarkID;
    private String thisSubjId;
    private String thisStudId;
    private String thisGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mark_to_stud_subj);
        mContext = this;
        dbConnect = new FirebaseConnector();
        btnMarkToSubjAddSave = findViewById(R.id.btnMarkToSubjAddSave);
        btnMarkToSubjAddCancel = findViewById(R.id.btnMarkToSubjAddCancel);
        markDateInSubj = findViewById(R.id.markDateInSubj);
        markItSelfInSubj = findViewById(R.id.markItSelfInSubj);
        thisGroupId = getIntent().getStringExtra("thisGroupId");
        thisStudId = getIntent().getStringExtra("thisStudId");
        thisSubjId = getIntent().getStringExtra("thisSubjId");
        dbConnect.subjsEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbConnect.subjs.clear();
                for(DataSnapshot s: snapshot.getChildren()){
                    MatchesSubj subj = s.getValue(MatchesSubj.class);
                    if(subj.getGroupId().equals(thisGroupId))
                        dbConnect.subjs.add(subj);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dbConnect.marksEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbConnect.marks.clear();
                for(DataSnapshot m: snapshot.getChildren()){
                    MatchesMark mark = m.getValue(MatchesMark.class);
                    if(mark.getStudId().equals(thisStudId)){
                        dbConnect.marks.add(mark);}}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ArrayList<Integer> spinnerData = new ArrayList<>(Arrays.asList(2, 3, 4, 5));
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, spinnerData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        markItSelfInSubj.setAdapter(adapter);
        if(getIntent().hasExtra("Matches")){
            MatchesMark matches = (MatchesMark) getIntent().getSerializableExtra("Matches");
            System.out.println(matches.getId());
            markItSelfInSubj.setSelection(spinnerData.indexOf(matches.getMark()));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate date = LocalDate.parse(matches.getDate(), formatter);
            markDateInSubj.updateDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        else
        {
            MatchMarkID = UUID.randomUUID().toString();
            while (dbConnect.getMark(MatchMarkID) != null)
                MatchMarkID = UUID.randomUUID().toString();
            Calendar today = Calendar.getInstance();
            markDateInSubj.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                }
            });
        }
        btnMarkToSubjAddCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnMarkToSubjAddSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day = String.valueOf(markDateInSubj.getDayOfMonth()).length() == 1 ? "0" + markDateInSubj.getDayOfMonth() : String.valueOf(markDateInSubj.getDayOfMonth());
                String month = String.valueOf((markDateInSubj.getMonth() + 1)).length() == 1 ? "0" + (markDateInSubj.getMonth() + 1) : String.valueOf((markDateInSubj.getMonth() + 1));
                MatchesMark matches = new MatchesMark(MatchMarkID, thisSubjId, thisStudId,
                        (int)markItSelfInSubj.getSelectedItem(), day + "." + month + "." + markDateInSubj.getYear());
                Intent intent = getIntent();
                intent.putExtra("Matches",matches);
                setResult(1, intent);
                finish();
            }
        });
    }
}