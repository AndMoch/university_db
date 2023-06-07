package com.example.mainproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class AddSubj extends AppCompatActivity {
    Button btnSubjAddSave, btnSubjCancel;
    EditText subjTitle;
    Context mContext;
    final String TAG = "Firebase";
    FirebaseConnector dbConnect;
    private String MatchSubjID;
    private String thisGroupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subj);
        mContext = this;
        dbConnect = new FirebaseConnector();
        btnSubjAddSave = findViewById(R.id.btnSubjAddSave);
        btnSubjCancel = findViewById(R.id.btnSubjAddCancel);
        subjTitle = findViewById(R.id.subjTitle);
        thisGroupID = getIntent().getStringExtra("thisGroupId");
        System.out.println(thisGroupID);
        dbConnect.subjsEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbConnect.subjs.clear();
                for(DataSnapshot s: snapshot.getChildren()){
                    MatchesSubj subj = s.getValue(MatchesSubj.class);
                    if(subj.getGroupId().equals(thisGroupID))
                        dbConnect.subjs.add(subj);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadGroup:onCancelled", error.toException());
            }
        });
        if(getIntent().hasExtra("Matches")){
            MatchesSubj matches=(MatchesSubj) getIntent().getSerializableExtra("Matches");
            System.out.println(matches.getId());
            subjTitle.setText(matches.getTitle());
            MatchSubjID = matches.getId();
        }
        else
        {
            MatchSubjID = UUID.randomUUID().toString();
            while (dbConnect.getSubj(MatchSubjID) != null)
                MatchSubjID = UUID.randomUUID().toString();
        }
        btnSubjCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSubjAddSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchesSubj ms = null;
                for (MatchesSubj subj:
                        dbConnect.subjs) {
                    if(subj.getTitle().equals(subjTitle.getText().toString()) && subj.getGroupId().equals(thisGroupID)){
                        ms = subj;
                        break;
                    }
                }
                if (ms == null){
                    MatchesSubj matches = new MatchesSubj(MatchSubjID, subjTitle.getText().toString(), thisGroupID);
                    Intent intent = getIntent();
                    intent.putExtra("Matches",matches);
                    setResult(1, intent);
                    finish();}
                else
                    Toast.makeText(mContext, "В этой группе уже есть такой предмет", Toast.LENGTH_LONG).show();
            }
        });
    }
}