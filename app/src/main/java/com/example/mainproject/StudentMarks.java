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
import java.util.Arrays;

public class StudentMarks extends AppCompatActivity {
    private static final int ADD_MARK_ACTIVITY = 41;
    private static final int REDACT_MARK_ACTIVITY = 42;
    final String TAG = "Firebase";
    FirebaseConnector mFirebaseConnector;
    Context mContext;
    ListView mListView;
    markListAdapter markAdapter;
    private String thisStudId;
    private String thisSubjId;
    private String thisGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_marks);
        mContext = this;
        mListView = findViewById(R.id.marksList);
        thisStudId = getIntent().getStringExtra("thisStudId");
        thisSubjId = getIntent().getStringExtra("thisSubjId");
        thisGroupId = getIntent().getStringExtra("thisGroupId");
        mFirebaseConnector = new FirebaseConnector();
        markAdapter = new markListAdapter(mContext, new ArrayList<>());
        mListView.setAdapter(markAdapter);
        registerForContextMenu(mListView);
        Button btnStudMarksBack = findViewById(R.id.btnStudMarksBack);
        btnStudMarksBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mFirebaseConnector.marksEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFirebaseConnector.marks.clear();
                for(DataSnapshot m: snapshot.getChildren()){
                    MatchesMark mark = m.getValue(MatchesMark.class);
                    if(mark.getStudId().equals(thisStudId) && mark.getSubjId().equals(thisSubjId)){
                        mFirebaseConnector.marks.add(mark);}}
                updateMarksList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadStud:onCancelled", error.toException());
            }
        });
        mFirebaseConnector.subjsEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFirebaseConnector.subjs.clear();
                for(DataSnapshot s: snapshot.getChildren()){
                    MatchesSubj subj = s.getValue(MatchesSubj.class);
                    mFirebaseConnector.subjs.add(subj);}
                updateMarksList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadStud:onCancelled", error.toException());
            }
        });}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addMark:
                Intent i = new Intent(mContext, AddMark.class);
                i.putExtra("thisStudId", thisStudId);
                i.putExtra("thisGroupId", thisGroupId);
                i.putExtra("thisSubjId", thisSubjId);
                startActivityForResult (i, ADD_MARK_ACTIVITY);
                updateMarksList();
                return true;
            case R.id.deleteAllMarks:
                mFirebaseConnector.deleteAllMarksOfStudForSubj(thisStudId, thisSubjId);
                updateMarksList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_marks, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.editMark:
                Intent i = new Intent(mContext, AddMark.class);
                MatchesMark md = (MatchesMark) mListView.getItemAtPosition((int)info.id);
                i.putExtra("Matches", md);
                i.putExtra("thisStudId", thisStudId);
                i.putExtra("thisGroupId", thisGroupId);
                i.putExtra("thisSubjId", thisSubjId);
                startActivityForResult(i, REDACT_MARK_ACTIVITY);
                updateMarksList();
                return true;
            case R.id.deleteMark:
                MatchesMark delete = (MatchesMark) mListView.getItemAtPosition((int) info.id);
                mFirebaseConnector.deleteMark(delete.getId());
                updateMarksList();
                return true;
            default:
                return super.onContextItemSelected(item);}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if(requestCode == ADD_MARK_ACTIVITY){
                MatchesMark mm = (MatchesMark) data.getExtras().getSerializable("Matches");
                mFirebaseConnector.writeNewMark(mm.getId(), mm.getSubjId(), mm.getStudId(), mm.getMark(), mm.getDate());
                updateMarksList();}
            else if(requestCode == REDACT_MARK_ACTIVITY){
                MatchesMark mm = (MatchesMark) data.getExtras().getSerializable("Matches");
                mFirebaseConnector.updateMark(mm.getId(), mm.getSubjId(), mm.getStudId(), mm.getMark(), mm.getDate());
                updateMarksList();
            }
        }
    }
    private void updateMarksList() {
        markAdapter.setArrayMyData(mFirebaseConnector.marks);
        markAdapter.notifyDataSetChanged();
    }
    class markListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<MatchesMark> arrayMyMatches;

        public markListAdapter (Context ctx, ArrayList<MatchesMark> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public ArrayList<MatchesMark> getArrayMyData() {
            return arrayMyMatches;
        }

        public void setArrayMyData(ArrayList<MatchesMark> arrayMyData) {
            this.arrayMyMatches = arrayMyData;
        }
        public int getCount () {
            return arrayMyMatches.size();
        }

        public Object getItem (int position) {
            return arrayMyMatches.get(position);
        }

        public long getItemId (int position) {
            MatchesMark md = arrayMyMatches.get(position);
            if (md != null) {
                return position;
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.item_mark, null);
            TextView vMarkValue = convertView.findViewById(R.id.tvMarkValue);
            TextView vMarkDate = convertView.findViewById(R.id.tvMarkDate);
            MatchesMark ms = arrayMyMatches.get(position);
            vMarkDate.setText(ms.getDate());
            vMarkValue.setText(ms.getMark() + " ");
            return convertView;
        }
    }
}