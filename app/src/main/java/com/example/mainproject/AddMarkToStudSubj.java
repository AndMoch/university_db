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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

public class AddMarkToStudSubj extends AppCompatActivity {
    Button btnMarkAddSave, btnMarkAddCancel;
    DatePicker markDate;
    Spinner markItSelf, markSubj;
    Context mContext;
    final String TAG = "Firebase";
    FirebaseConnector dbConnect;
    private String MatchMarkID;
    private String thisStudId;
    private String thisGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mark);
        mContext = this;
        dbConnect = new FirebaseConnector();
        btnMarkAddSave = findViewById(R.id.btnMarkAddSave);
        btnMarkAddCancel = findViewById(R.id.btnMarkAddCancel);
        markDate = findViewById(R.id.markDate);
        markItSelf = findViewById(R.id.markItSelf);
        markSubj = findViewById(R.id.markSubj);
        thisGroupId = getIntent().getStringExtra("thisGroupId");
        thisStudId = getIntent().getStringExtra("thisStudId");
        dbConnect.subjsEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbConnect.subjs.clear();
                for(DataSnapshot s: snapshot.getChildren()){
                    MatchesSubj subj = s.getValue(MatchesSubj.class);
                    if(subj.getGroupId().equals(thisGroupId))
                        dbConnect.subjs.add(subj);
                }
                ArrayList<String> spinnerDataSubj = new ArrayList<>();
                for (MatchesSubj subj: dbConnect.subjs){
                    spinnerDataSubj.add(subj.getTitle());
                }
                ArrayAdapter<String> adapterSubj = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, spinnerDataSubj);
                adapterSubj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                markSubj.setAdapter(adapterSubj);
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
        markItSelf.setAdapter(adapter);
        MatchMarkID = UUID.randomUUID().toString();
        while (dbConnect.getMark(MatchMarkID) != null)
            MatchMarkID = UUID.randomUUID().toString();
        Calendar today = Calendar.getInstance();
        markDate.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        });
        btnMarkAddCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnMarkAddSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subjTitle = markSubj.getSelectedItem().toString();
                String subjId = "";
                for (MatchesSubj subj: dbConnect.subjs){
                    if (subj.getTitle().equals(subjTitle)){
                        subjId = subj.getId();
                        break;
                    }
                }
                String day = String.valueOf(markDate.getDayOfMonth()).length() == 1 ? "0" + markDate.getDayOfMonth() : String.valueOf(markDate.getDayOfMonth());
                String month = String.valueOf((markDate.getMonth() + 1)).length() == 1 ? "0" + (markDate.getMonth() + 1) : String.valueOf((markDate.getMonth() + 1));
                MatchesMark matches = new MatchesMark(MatchMarkID, subjId, thisStudId,
                        (int)markItSelf.getSelectedItem(), day + "." + month + "." + markDate.getYear());
                Intent intent = getIntent();
                intent.putExtra("Matches",matches);
                setResult(1, intent);
                finish();
            }
        });
    }
}