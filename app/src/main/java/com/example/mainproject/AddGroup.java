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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

public class AddGroup extends AppCompatActivity {
    Button btnAddGroup, btnCancelAddingGroup;
    EditText groupFaculty, groupNumber;
    final String TAG = "Firebase";
    private Context context;
    private String MatchGroupID;
    private String thisUniversityId;


    FirebaseConnector dbConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        context = this;
        btnAddGroup = findViewById(R.id.btnGroupAddSave);
        btnCancelAddingGroup = findViewById(R.id.btnGroupAddCancel);
        groupFaculty = findViewById(R.id.groupFaculty);
        groupNumber = findViewById(R.id.groupNumber);
        thisUniversityId = getIntent().getStringExtra("universityId");
        dbConnect = new FirebaseConnector();
        if(getIntent().hasExtra("Matches")){
            MatchesGroup matches=(MatchesGroup)getIntent().getSerializableExtra("Matches");
            System.out.println(matches.getId());
            groupNumber.setText(String.valueOf(matches.getNumber()));
            groupFaculty.setText(matches.getFaculty());
            MatchGroupID = matches.getId();
            thisUniversityId = matches.getUniversityId();
        }
        else
        {
            MatchGroupID = UUID.randomUUID().toString();
            while (dbConnect.getGroup(MatchGroupID) != null)
                MatchGroupID = UUID.randomUUID().toString();
        }
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchesGroup mg = null;
                for (MatchesGroup group:
                     dbConnect.groups) {
                    if(group.getNumber() == Integer.parseInt(groupNumber.getText().toString()) && group.getFaculty().equals(groupFaculty.getText().toString())){
                        mg = group;
                        break;
                    }

                }
                if (mg == null){
                    MatchesGroup matches = new MatchesGroup(MatchGroupID, Integer.parseInt(groupNumber.getText().toString()), groupFaculty.getText().toString(), thisUniversityId);
                    Intent intent=getIntent();
                    intent.putExtra("Matches",matches);
                    setResult(1, intent);
                    finish();}
                else
                    Toast.makeText(context, "Такая группа уже существует", Toast.LENGTH_LONG).show();
            }
        });
        btnCancelAddingGroup.setOnClickListener(new View.OnClickListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadGroup:onCancelled", error.toException());
            }
        });
    }
}