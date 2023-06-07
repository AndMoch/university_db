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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

public class RatingActivity extends AppCompatActivity {
    final String TAG = "Firebase";
    private String thisGroupId;
    FirebaseConnector mFirebaseConnector;
    Context mContext;
    ListView mListView;
    Spinner subj;
    ratingListAdapter ratingAdapter;
    ArrayList<String> subjIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        mContext = this;
        mListView = findViewById(R.id.ratingList);
        mFirebaseConnector = new FirebaseConnector();
        ratingAdapter = new ratingListAdapter(mContext, new ArrayList<>());
        subj = findViewById(R.id.ratingSubj);
        thisGroupId = getIntent().getStringExtra("thisGroupId");
        subj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String subjTitle = parent.getItemAtPosition(position).toString();
                updateRatingList(subjTitle, thisGroupId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mListView.setAdapter(ratingAdapter);
        registerForContextMenu(mListView);
        Button btnRatingBack = findViewById(R.id.btnRatingBack);
        btnRatingBack.setOnClickListener(new View.OnClickListener() {
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
                ArrayList<String> spinnerData = new ArrayList<>();
                for(DataSnapshot subj: snapshot.getChildren()){
                    MatchesSubj subject = subj.getValue(MatchesSubj.class);
                    if(subject.getGroupId().equals(thisGroupId)){
                        mFirebaseConnector.subjs.add(subject);
                        subjIds.add(subject.getId());
                        spinnerData.add(subject.getTitle());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, spinnerData);
                subj.setAdapter(adapter);
                updateRatingList(subj.getSelectedItem().toString(), thisGroupId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadStud:onCancelled", error.toException());
            }
        });
        mFirebaseConnector.marksEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFirebaseConnector.marks.clear();
                for(DataSnapshot mk: snapshot.getChildren()){
                    MatchesMark mark = mk.getValue(MatchesMark.class);
                    if(subjIds.contains(mark.getSubjId())){
                        mFirebaseConnector.marks.add(mark);}}
                updateRatingList(subj.getSelectedItem().toString(), thisGroupId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadGroup:onCancelled", error.toException());
            }
        });
        mFirebaseConnector.studentsEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFirebaseConnector.students.clear();
                for(DataSnapshot stud: snapshot.getChildren()){
                    MatchesStud student = stud.getValue(MatchesStud.class);
                    if (student.getGroup_id().equals(thisGroupId)){
                        mFirebaseConnector.students.add(student);}}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadStud:onCancelled", error.toException());
            }
        });}

    private void updateRatingList(String subjTitle, String groupId) {
        ArrayList<Map.Entry<MatchesStud, Double>> ratingData = new ArrayList<>();
        MatchesSubj subj = mFirebaseConnector.getSubjByIdAndTitle(subjTitle, groupId);
        for (MatchesStud stud:
             mFirebaseConnector.students) {
            ArrayList<Integer> marksOfStudForSubj = new ArrayList<>();
            ArrayList<MatchesMark> marks = mFirebaseConnector.getMarksOfStudForSubj(stud.getId(), subj.getId());
            for (MatchesMark mark: marks) {
                marksOfStudForSubj.add(mark.getMark());
            }
            if (marksOfStudForSubj.size() != 0){
                int[] forAver = marksOfStudForSubj.stream().mapToInt(i -> i).toArray();
                double averageMarkForSubj = (double) Arrays.stream(forAver).sum() / forAver.length;
                averageMarkForSubj = Math.round(averageMarkForSubj * 100);
                averageMarkForSubj /= 100;
                AbstractMap.SimpleEntry<MatchesStud, Double> data = new AbstractMap.SimpleEntry<>(stud, averageMarkForSubj);
                ratingData.add(data);
            }
            else {
                AbstractMap.SimpleEntry<MatchesStud, Double> data = new AbstractMap.SimpleEntry<>(stud, (double) 0);
                ratingData.add(data);
            }

        }
        ratingData.sort(new Comparator<Map.Entry<MatchesStud, Double>>() {
            @Override
            public int compare(Map.Entry<MatchesStud, Double> o1, Map.Entry<MatchesStud, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        ratingAdapter.setArrayMyData(ratingData);
        ratingAdapter.notifyDataSetChanged();
    }

    class ratingListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<Map.Entry<MatchesStud, Double>> arrayMyMatches;

        public ratingListAdapter (Context ctx, ArrayList<Map.Entry<MatchesStud, Double>> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public ArrayList<Map.Entry<MatchesStud, Double>> getArrayMyData() {
            return arrayMyMatches;
        }

        public void setArrayMyData(ArrayList<Map.Entry<MatchesStud, Double>> arrayMyData) {
            this.arrayMyMatches = arrayMyData;
        }

        public int getCount () {
            return arrayMyMatches.size();
        }

        public Object getItem (int position) {
            return arrayMyMatches.get(position);
        }

        public long getItemId (int position) {
            Map.Entry<MatchesStud, Double> md = arrayMyMatches.get(position);
            if (md != null) {
                return position;
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.item_rating, null);
            TextView vName = convertView.findViewById(R.id.ratingStud);
            TextView vAverMark = convertView.findViewById(R.id.ratingMark);
            Map.Entry<MatchesStud, Double> md = arrayMyMatches.get(position);
            vName.setText(md.getKey().getName() + " " + md.getKey().getSurname() + " " + md.getKey().getSecond_name() + ";");
            if(md.getValue() != 0) {
                vAverMark.setText(" Средний балл: " + md.getValue());
            }
            else {
                vAverMark.setText(" Нет оценок по предмету");
            }
            return convertView;
        }
    }
}