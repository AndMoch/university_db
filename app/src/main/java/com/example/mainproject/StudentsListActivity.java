package com.example.mainproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
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

import java.util.ArrayList;
import java.util.Arrays;

public class StudentsListActivity extends AppCompatActivity {
    private static final int ADD_STUD_TO_GROUP_ACTIVITY = 22;
    private static final int REDACT_STUD_IN_GROUP_ACTIVITY = 24;
    studListAdapter studAdapter;
    DBMatches mDBConnector;
    Context mContext;
    ListView mListView;
    long thisGroupId;

    FirebaseConnector mFirebaseConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);
        mContext = this;
        mDBConnector = new DBMatches(this);
        mFirebaseConnector = new FirebaseConnector();
        mListView =  findViewById(R.id.studList);
        studAdapter = new StudentsListActivity.studListAdapter(mContext, mDBConnector.selectStudsByGroup(thisGroupId));
        mListView.setAdapter(studAdapter);
        registerForContextMenu(mListView);
        thisGroupId = getIntent().getLongExtra("thisGroupId", 0);
        Button btnAllGroups = findViewById(R.id.btnAllGroupsViewFromStudOfGroup);
        btnAllGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stud_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addStudToGroup:
                Intent i = new Intent(mContext, AddStudent.class);
                startActivityForResult (i, ADD_STUD_TO_GROUP_ACTIVITY);
                updateStudList();
                return true;
            case R.id.deleteAllStudsFromGroup:
                mDBConnector.deleteAllStudsFromGroup(thisGroupId);
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
        inflater.inflate(R.menu.context_menu_stud_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.editStud:
                Intent i = new Intent(mContext, AddStudent.class);
                MatchesStud md = mDBConnector.selectStud(info.id);
                i.putExtra("Matches", md);
                i.putExtra("GroupID", thisGroupId);
                startActivityForResult(i, REDACT_STUD_IN_GROUP_ACTIVITY);
                updateStudList();
                return true;
            case R.id.deleteStud:
                mDBConnector.deleteStud(info.id);
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
            Log.i("AddStud", requestCode + " " + resultCode);
            MatchesStud md = (MatchesStud) data.getExtras().getSerializable("Matches");
            if (requestCode == REDACT_STUD_IN_GROUP_ACTIVITY)
                mDBConnector.updateStud(md);
            else if (requestCode == ADD_STUD_TO_GROUP_ACTIVITY) {
                Log.i("AddedStud", "yes " + thisGroupId);
                mDBConnector.insertStud(md.getName(), md.getSurname(), md.getSecond_name(), md.getBirthdate(), md.getGroup_id());
            }updateStudList();
        }
    }

    private void updateStudList () {
        Log.i("Studs", Arrays.toString(mDBConnector.selectStudsByGroup(thisGroupId).toArray()));
        studAdapter.setArrayMyData(mDBConnector.selectStudsByGroup(thisGroupId));
        studAdapter.notifyDataSetChanged();
    }

    class studListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<MatchesStud> arrayMyMatches;

        public studListAdapter (Context ctx, ArrayList<MatchesStud> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public ArrayList<MatchesStud> getArrayMyData() {
            return arrayMyMatches;
        }

        public void setArrayMyData(ArrayList<MatchesStud> arrayMyData) {
            this.arrayMyMatches = arrayMyData;
        }

        public int getCount () {
            return arrayMyMatches.size();
        }

        public Object getItem (int position) {

            return position;
        }

        public long getItemId (int position) {
            MatchesStud md = arrayMyMatches.get(position);
            if (md != null) {
                return md.getId();
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
            MatchesGroup mg = mDBConnector.selectGroup(md.getGroup_id());

            vName.setText(md.getName());
            vSurname.setText(md.getSurname());
            vSecondName.setText(md.getSecond_name());
            vBirthdate.setText(md.getBirthdate());
            vGroup.setText(mg.getFaculty() + " №" + mg.getNumber());

            return convertView;
        }
    }

}