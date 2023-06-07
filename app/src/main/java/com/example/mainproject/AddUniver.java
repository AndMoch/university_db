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

public class AddUniver extends AppCompatActivity {

    Button btnUniverSave, btnUniverCancel;
    EditText univerName, univerCity;
    Context mContext;
    final String TAG = "Firebase";
    FirebaseConnector dbConnect;
    private String MatchUniverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_univer);
        mContext = this;
        dbConnect = new FirebaseConnector();
        btnUniverSave = findViewById(R.id.btnUniverAddSave);
        btnUniverCancel = findViewById(R.id.btnUniverAddCancel);
        univerName = findViewById(R.id.univerName);
        univerCity = findViewById(R.id.univerCity);
        dbConnect.universitiesEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbConnect.universities.clear();
                for(DataSnapshot u: snapshot.getChildren()){
                    MatchesUniver univer = u.getValue(MatchesUniver.class);
                    dbConnect.universities.add(univer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadUniver:onCancelled", error.toException());
            }
        });
        if(getIntent().hasExtra("Matches")){
            MatchesUniver matches = (MatchesUniver) getIntent().getSerializableExtra("Matches");
            univerCity.setText(matches.getCity());
            univerName.setText(matches.getTitle());
            MatchUniverID = matches.getId();
        }
        else
        {
            MatchUniverID = UUID.randomUUID().toString();
            while (dbConnect.getUniver(MatchUniverID) != null)
                MatchUniverID = UUID.randomUUID().toString();
        }
        btnUniverCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnUniverSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchesUniver ms = null;
                for (MatchesUniver univer:
                        dbConnect.universities) {
                    if(univer.getTitle().equals(univerName.getText().toString()) && univer.getCity().equals(univerCity.getText().toString()) && !univer.getId().equals(MatchUniverID)){
                        ms = univer;
                        break;
                    }
                }
                if (ms == null){
                    MatchesUniver matches = new MatchesUniver(MatchUniverID, univerName.getText().toString(), univerCity.getText().toString());
                    Intent intent = getIntent();
                    intent.putExtra("Matches",matches);
                    setResult(1, intent);
                    finish();}
                else
                    Toast.makeText(mContext, "Такой университет уже есть", Toast.LENGTH_LONG).show();
            }
        });
    }
}