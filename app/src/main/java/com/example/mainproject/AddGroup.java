package com.example.mainproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddGroup extends AppCompatActivity {
    Button btnAddGroup, btnCancelAddingGroup;
    EditText groupFaculty, groupNumber;
    private Context context;
    private long MatchGroupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        btnAddGroup = findViewById(R.id.btnGroupAddSave);
        btnCancelAddingGroup = findViewById(R.id.btnGroupAddCancel);
        groupFaculty = findViewById(R.id.groupFaculty);
        groupNumber = findViewById(R.id.groupNumber);
        if(getIntent().hasExtra("Matches")){
            MatchesGroup matches=(MatchesGroup)getIntent().getSerializableExtra("Matches");
            groupNumber.setText(matches.getNumber());
            groupFaculty.setText(matches.getFaculty());
            MatchGroupID=matches.getId();
        }
        else
        {
            MatchGroupID=-1;
        }
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchesGroup matches = new MatchesGroup(MatchGroupID, Integer.parseInt(groupNumber.getText().toString()), groupFaculty.getText().toString());
                Intent intent=getIntent();
                intent.putExtra("Matches",matches);
                setResult(1, intent);
                finish();
            }
        });
        btnCancelAddingGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}