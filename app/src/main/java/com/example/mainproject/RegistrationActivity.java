package com.example.mainproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity {
    EditText etEmail, etPassword, etName, etSurname, etSecondName;
    Button btnRegistration, btnBack;
    private FirebaseAuth auth1, auth2;
    Context mContext;
    final String TAG = "Firebase";
    FirebaseConnector dbConnect;
    Spinner univer;
    private String MatchUserID;
    ArrayList<MatchesUniver> universities = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mContext = this;
        dbConnect = new FirebaseConnector();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etSecondName = findViewById(R.id.etSecondName);
        univer = findViewById(R.id.univerReg);
        btnRegistration = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBackReg);
        auth1 = FirebaseAuth.getInstance();
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://university-db-d344b-default-rtdb.europe-west1.firebasedatabase.app")
                .setApiKey("AIzaSyBNbVBQuX3-9TGkhzPIwTMDB6ozfwb473c")
                .setApplicationId("university-db-d344b").build();
        try { FirebaseApp app = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "AnyAppName");
            auth2 = FirebaseAuth.getInstance(app);
        } catch (IllegalStateException e){
            auth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("project-611478158569"));
        }
        if(getIntent().hasExtra("Matches")){
            MatchesUser matches=(MatchesUser) getIntent().getSerializableExtra("Matches");
            etName.setText(matches.getName());
            etSurname.setText(matches.getSurname());
            etSecondName.setText(matches.getSecond_name());
            etEmail.setText(matches.getEmail());
            MatchUserID = matches.getId();
        }
        else
        {
            MatchUserID = UUID.randomUUID().toString();
            while (dbConnect.getUser(MatchUserID) != null)
                MatchUserID = UUID.randomUUID().toString();
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String surname = etSurname.getText().toString().trim();
                String secondName = etSecondName.getText().toString().trim();
                String univerString = univer.getSelectedItem().toString();
                Scanner scan = new Scanner(univerString);
                String title = scan.next();
                String c = scan.next();
                String city = scan.next();
                MatchesUniver intentUniver = new MatchesUniver();
                for (MatchesUniver univer: dbConnect.universities){
                    if (univer.getTitle().equals(title) && univer.getCity().equals(city)){
                        intentUniver = univer;
                    }
                }
                auth2.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(RegistrationActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_LONG).show();
                            Log.e("RegTag", task.getException().toString());
                        } else {
                            auth2.signOut();
                        }
                    }
                });
                MatchesUser matches = new MatchesUser(MatchUserID, name, surname, secondName, email, intentUniver.getId());
                Intent intent = getIntent();
                intent.putExtra("Matches", matches);
                setResult(1, intent);
                finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        dbConnect.universitiesEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbConnect.universities.clear();
                for(DataSnapshot u: snapshot.getChildren()){
                    MatchesUniver univer = u.getValue(MatchesUniver.class);
                    dbConnect.universities.add(univer);}
                universities = dbConnect.universities;
                ArrayList<String> spinnerData = new ArrayList<>();
                for (MatchesUniver u:
                        universities) {
                    spinnerData.add(u.getTitle() + " г. " + u.getCity());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, spinnerData);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                univer.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadUniver:onCancelled", error.toException());
            }
        });
        dbConnect.usersEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbConnect.users.clear();
                for(DataSnapshot u: snapshot.getChildren()){
                    MatchesUser user = u.getValue(MatchesUser.class);
                    dbConnect.users.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadUser:onCancelled", error.toException());
            }
        });
    }
}