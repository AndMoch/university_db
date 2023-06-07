package com.example.mainproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Firebase";
    Context mContext;
    Button btnLog, btnUniReq, btnAccReq;
    FirebaseAuth auth;
    FirebaseConnector mFirebaseConnector;
    private String universityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        auth = FirebaseAuth.getInstance();
        btnLog = findViewById(R.id.btnLogin);
        btnUniReq = findViewById(R.id.btnSendRequestUniver);
        btnAccReq = findViewById(R.id.btnSendRequestAccount);
        mFirebaseConnector = new FirebaseConnector();
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        });
        btnUniReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SendRequestUniver.class));
            }
        });
        btnAccReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SendRequestAccount.class));
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            if (auth.getCurrentUser().getEmail().equals("andmochgeek@ya.ru")){
                startActivity(new Intent(mContext, CreatorActivity.class));}
            else {
                Intent i = new Intent(mContext, MainPageActivity.class);
                startActivity(i);}
            finish();
        }
    }
}