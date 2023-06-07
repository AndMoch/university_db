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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class StudentsMarksBySubj extends AppCompatActivity {
    private static final int ADD_MARK_ACTIVITY = 21;
    final String TAG = "Firebase";
    FirebaseConnector mFirebaseConnector;
    Context mContext;
    ListView mListView;
    markSubjListAdapter markAdapter;
    private String thisStudId;
    private String thisGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_marks_by_subj);
        mContext = this;
        mListView = findViewById(R.id.marksSubjList);
        thisStudId = getIntent().getStringExtra("thisStudId");
        thisGroupId = getIntent().getStringExtra("thisGroupId");
        mFirebaseConnector = new FirebaseConnector();
        markAdapter = new markSubjListAdapter(mContext, mFirebaseConnector.subjs);
        mListView.setAdapter(markAdapter);
        registerForContextMenu(mListView);
        Button btnStudSubjsBack = findViewById(R.id.btnStudSubjsBack);
        btnStudSubjsBack.setOnClickListener(new View.OnClickListener() {
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
                    if(mark.getStudId().equals(thisStudId)){
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
                    if(subj.getGroupId().equals(thisGroupId))
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
        getMenuInflater().inflate(R.menu.menu_stud_subj, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addMarkInSubjs:
                if (mFirebaseConnector.subjs.size() != 0){
                Intent i = new Intent(mContext, AddMarkToStudSubj.class);
                i.putExtra("thisStudId", thisStudId);
                i.putExtra("thisGroupId", thisGroupId);
                startActivityForResult (i, ADD_MARK_ACTIVITY);
                updateMarksList();}
                else {Toast.makeText(mContext, "В группе нет предметов", Toast.LENGTH_LONG).show();}
                return true;
            case R.id.deleteAllMarksInSubjs:
                mFirebaseConnector.deleteAllMarksOfStud(thisStudId);
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
        inflater.inflate(R.menu.context_menu_stud_subj, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.marksForSubj:
                Intent i = new Intent(mContext, StudentMarks.class);
                MatchesSubj s = (MatchesSubj) mListView.getItemAtPosition((int)info.id);
                i.putExtra("thisSubjId", s.getId());
                i.putExtra("thisStudId", thisStudId);
                i.putExtra("thisGroupId", thisGroupId);
                startActivity(i);
                return true;
            case R.id.deleteAllMarksForSubj:
                MatchesSubj d = (MatchesSubj) mListView.getItemAtPosition((int)info.id);
                mFirebaseConnector.deleteAllMarksOfStudForSubj(thisStudId, d.getId());
                updateMarksList();
                return true;
            default:
                return super.onContextItemSelected(item);
    }}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            MatchesMark mm = (MatchesMark) data.getExtras().getSerializable("Matches");
            mFirebaseConnector.writeNewMark(mm.getId(), mm.getStudId(), mm.getSubjId(), mm.getMark(), mm.getDate());
            updateMarksList();
        }
    }
    private void updateMarksList() {
        ArrayList<MatchesSubj> subjs = new ArrayList<>();
        for (MatchesSubj subj: mFirebaseConnector.subjs){
            if(subj.getGroupId().equals(thisGroupId)){
                subjs.add(subj);
            }
        }
        markAdapter.setArrayMyData(subjs);
        markAdapter.notifyDataSetChanged();
    }

    class markSubjListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<MatchesSubj> arrayMyMatches;

        public markSubjListAdapter (Context ctx, ArrayList<MatchesSubj> arr) {
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
                convertView = mLayoutInflater.inflate(R.layout.item_subj_aver, null);
            TextView vSubjAndAver = convertView.findViewById(R.id.subjAverInfo);
            MatchesSubj ms = arrayMyMatches.get(position);
            ArrayList<Integer> marksOfStudForSubj = new ArrayList<>();
            for (MatchesMark mark:
                    mFirebaseConnector.marks) {
                if (mark.getStudId().equals(thisStudId) && mark.getSubjId().equals(ms.getId())){
                    marksOfStudForSubj.add(mark.getMark());
                }
            }
            if (marksOfStudForSubj.size() != 0){
            int[] forAver = marksOfStudForSubj.stream().mapToInt(i -> i).toArray();
            double averageMarkForSubj = (double) Arrays.stream(forAver).sum() / forAver.length;
            averageMarkForSubj = Math.round(averageMarkForSubj * 100);
            averageMarkForSubj /= 100;
            vSubjAndAver.setText(ms.getTitle() + "; средняя оценка - " + averageMarkForSubj);}
            else {
                vSubjAndAver.setText(ms.getTitle() + "; пока нет оценок");}
            return convertView;
        }
    }
}