package com.example.mainproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubjectsActivity extends AppCompatActivity {
    private static final int ADD_SUBJ_ACTIVITY = 31;
    private static final int REDACT_SUBJ_ACTIVITY = 32;
    final String TAG = "Firebase";
    FirebaseConnector mFirebaseConnector;
    Context mContext;
    ListView mListView;
    subjListAdapter subjAdapter;
    private String thisGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);
        mContext = this;
        mFirebaseConnector = new FirebaseConnector();
        mListView =  findViewById(R.id.subjList);
        subjAdapter = new subjListAdapter(mContext, mFirebaseConnector.subjs);
        thisGroupId = getIntent().getStringExtra("thisGroupId");
        System.out.println(thisGroupId);
        mListView.setAdapter(subjAdapter);
        registerForContextMenu(mListView);
        Button btnSubjBack = findViewById(R.id.btnSubjsBack);
        btnSubjBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mFirebaseConnector.subjsEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFirebaseConnector.subjs.clear();
                for(DataSnapshot sub: snapshot.getChildren()){
                    MatchesSubj subj = sub.getValue(MatchesSubj.class);
                    if(subj.getGroupId().equals(thisGroupId))
                        mFirebaseConnector.subjs.add(subj);}
                updateSubjsList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadSubj:onCancelled", error.toException());
            }
        });}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subj, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addSubj:
                Intent i = new Intent(mContext, AddSubj.class);
                i.putExtra("thisGroupId", thisGroupId);
                startActivityForResult (i, ADD_SUBJ_ACTIVITY);
                updateSubjsList();
                return true;
            case R.id.deleteAllSubjs:
                mFirebaseConnector.deleteAllSubjs(thisGroupId);
                updateSubjsList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_subj, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.editSubj:
                Intent i = new Intent(mContext, AddSubj.class);
                MatchesSubj ms = (MatchesSubj) mListView.getItemAtPosition((int)info.id);
                i.putExtra("Matches", ms);
                i.putExtra("thisGroupId", thisGroupId);
                startActivityForResult(i, REDACT_SUBJ_ACTIVITY);
                updateSubjsList();
                return true;
            case R.id.deleteSubj:
                MatchesSubj delete = (MatchesSubj) mListView.getItemAtPosition((int)info.id);
                mFirebaseConnector.deleteSubj(delete.getId());
                updateSubjsList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Log.i("AddSubj", requestCode + " " + resultCode);
            MatchesSubj ms = (MatchesSubj) data.getExtras().getSerializable("Matches");
            if (requestCode == REDACT_SUBJ_ACTIVITY)
                mFirebaseConnector.updateSubj(ms.getId(), ms.getTitle());
            else if (requestCode == ADD_SUBJ_ACTIVITY) {
                mFirebaseConnector.writeNewSubj(ms.getId(), ms.getTitle(), ms.getGroupId());
            }updateSubjsList();
        }
    }

    private void updateSubjsList () {
        ArrayList<MatchesSubj> subjs = new ArrayList<>();
        for (MatchesSubj s:
             mFirebaseConnector.subjs) {
            if (s.getGroupId().equals(thisGroupId)){
                subjs.add(s);
            }
        }
        subjAdapter.setArrayMyData(subjs);
        subjAdapter.notifyDataSetChanged();
    }

    class subjListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<MatchesSubj> arrayMyMatches;

        public subjListAdapter (Context ctx, ArrayList<MatchesSubj> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public ArrayList<MatchesSubj> getArrayMyData() {
            return arrayMyMatches;
        }

        public void setArrayMyData(ArrayList<MatchesSubj> arrayMyData) {
            this.arrayMyMatches = arrayMyData;
        }

        public int getCount () {
            return arrayMyMatches.size();
        }

        public Object getItem (int position) {
            return arrayMyMatches.get(position);
        }

        public long getItemId (int position) {
            MatchesSubj md = arrayMyMatches.get(position);
            if (md != null) {
                return position;
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.item_subj, null);
            TextView vTitle = convertView.findViewById(R.id.subjInfo);
            MatchesSubj ms = arrayMyMatches.get(position);
            vTitle.setText(ms.getTitle());
            return convertView;
        }
    }
}