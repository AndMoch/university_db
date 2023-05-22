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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllStudentsList extends AppCompatActivity {
    private static final int ADD_STUD_IN_ALL_ACTIVITY = 21;
    private static final int REDACT_STUD_IN_ALL_ACTIVITY = 23;
    final String TAG = "Firebase";
    FirebaseConnector mFirebaseConnector;
    Context mContext;
    ListView mListView;
    allStudsListAdapter studAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_students_list);
        mContext = this;
        mListView = findViewById(R.id.allStudsList);
        mFirebaseConnector = new FirebaseConnector();
        studAdapter = new AllStudentsList.allStudsListAdapter(mContext, mFirebaseConnector.getAllStuds());
        mListView.setAdapter(studAdapter);
        registerForContextMenu(mListView);
        Button btnAllGroups = findViewById(R.id.btnAllGroupsView);
        btnAllGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mFirebaseConnector.groupsEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFirebaseConnector.groups.clear();
                for(DataSnapshot gr: snapshot.getChildren()){
                    MatchesGroup group = gr.getValue(MatchesGroup.class);
                    mFirebaseConnector.groups.add(group);}
                updateGroupList();
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
                    mFirebaseConnector.students.add(student);}
                updateStudList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadStud:onCancelled", error.toException());
            }
        });}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all_studs_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addStudInALl:
                if (mFirebaseConnector.groups.size() != 0){
                    Intent i = new Intent(mContext, AddStudent.class);
                    startActivityForResult (i, ADD_STUD_IN_ALL_ACTIVITY);
                    updateStudList();}
                else {
                    Toast.makeText(this, "Сначала нужно добавить группу", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.deleteAllStuds:
                mFirebaseConnector.deleteAllStuds();
                updateStudList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_all_studs, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.editStudInAll:
                Intent i = new Intent(mContext, AddStudent.class);
                MatchesStud md = (MatchesStud) mListView.getItemAtPosition((int)info.id);
                i.putExtra("Matches", md);
                startActivityForResult(i, REDACT_STUD_IN_ALL_ACTIVITY);
                updateStudList();
                return true;
            case R.id.deleteStudInALl:
                MatchesStud delete = (MatchesStud) mListView.getItemAtPosition((int)info.id);
                mFirebaseConnector.deleteStud(delete.getId());
                updateStudList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            MatchesStud md = (MatchesStud) data.getExtras().getSerializable("Matches");
            if (requestCode == REDACT_STUD_IN_ALL_ACTIVITY)
                mFirebaseConnector.updateStud(md.getId(), md.getName(), md.getSurname(), md.getSecond_name(), md.getBirthdate(), md.getGroup_id());
            else
                mFirebaseConnector.writeNewStud(md.getId(), md.getName(), md.getSurname(), md.getSecond_name(), md.getBirthdate(), md.getGroup_id());
            updateStudList();
        }
    }

    private void updateStudList() {
        studAdapter.setArrayMyData(mFirebaseConnector.students);
        studAdapter.notifyDataSetChanged();
    }
    private void updateGroupList() {
        studAdapter.setArrayGroups(mFirebaseConnector.groups);
    }
    class allStudsListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<MatchesStud> arrayMyMatches;
        private ArrayList<MatchesGroup> arrayGroups;

        public allStudsListAdapter (Context ctx, ArrayList<MatchesStud> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public ArrayList<MatchesStud> getArrayMyData() {
            return arrayMyMatches;
        }

        public void setArrayMyData(ArrayList<MatchesStud> arrayMyData) {
            this.arrayMyMatches = arrayMyData;
        }
        public void setArrayGroups(ArrayList<MatchesGroup> arrayMyData) {
            this.arrayGroups = arrayMyData;
        }
        public int getCount () {
            return arrayMyMatches.size();
        }

        public Object getItem (int position) {
            return arrayMyMatches.get(position);
        }

        public long getItemId (int position) {
            MatchesStud md = arrayMyMatches.get(position);
            if (md != null) {
                return position;
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.item_stud, null);
            TextView vName = convertView.findViewById(R.id.name);
            TextView vSurname = convertView.findViewById(R.id.surname);
            TextView vSecondName = convertView.findViewById(R.id.secondName);
            TextView vBirthdate = convertView.findViewById(R.id.birthdate);
            TextView vGroup = convertView.findViewById(R.id.group);
            MatchesStud md = arrayMyMatches.get(position);
            MatchesGroup mg = new MatchesGroup();
            for (MatchesGroup group:
                 mFirebaseConnector.groups) {
                if (group.getId().equals(md.getGroup_id())){
                    mg = group;
                    break;}
            }
            vName.setText(md.getName() + " ");
            vSurname.setText(md.getSurname() + " ");
            vSecondName.setText(md.getSecond_name());
            vBirthdate.setText(md.getBirthdate() + " ");
            vGroup.setText(mg.getFaculty() + " факультет, группа №" + mg.getNumber());
            return convertView;
        }
    }
}
