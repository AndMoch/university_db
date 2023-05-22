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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {
    private static final int ADD_GROUP_ACTIVITY = 11;
    private static final int REDACT_GROUP_ACTIVITY = 12;
    final String TAG = "Firebase";

    FirebaseConnector mFirebaseConnector;
    Context mContext;
    ListView mListView;
    Button btnAllStuds, btnSignOut;
    groupListAdapter groupAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        mContext = this;
        mFirebaseConnector = new FirebaseConnector();
        mListView =  findViewById(R.id.groupList);
        groupAdapter = new groupListAdapter(mContext, mFirebaseConnector.groups);
        mListView.setAdapter(groupAdapter);
        registerForContextMenu(mListView);
        btnAllStuds = findViewById(R.id.btnAllStudsView);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnAllStuds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AllStudentsList.class);
                startActivity(intent);
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(mContext, MainActivity.class));
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MatchesGroup md = (MatchesGroup) mListView.getItemAtPosition((int)id);
                Intent intent = new Intent(mContext, StudentsListActivity.class);
                intent.putExtra("thisGroupId", md.getId());
                startActivity(intent);
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
                    mFirebaseConnector.groups.add(group);
                    mFirebaseConnector.groupsIds.add(group.getId());}
                System.out.println(mFirebaseConnector.groups.toString());
                updateGroupList();
                updateGroupIdsList();
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
                    mFirebaseConnector.students.add(student);
                    updateStudList();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadStud:onCancelled", error.toException());
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addGroup:
                Intent i = new Intent(mContext, AddGroup.class);
                startActivityForResult(i, ADD_GROUP_ACTIVITY);
                updateGroupList();
                return true;
            case R.id.deleteAllGroups:
                if (mFirebaseConnector.students.size() == 0){
                    mFirebaseConnector.deleteAllGroups();
                    updateGroupList();}
                else{
                    System.out.println(mFirebaseConnector.students.size());
                    Toast.makeText(this, "Пока хотя бы в одной группе состоят студенты, вы не можете удалить все группы.", Toast.LENGTH_LONG).show();}
                return true;
            case R.id.exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_groups, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.editGroup:
                Intent i = new Intent(mContext, AddGroup.class);
                MatchesGroup md = (MatchesGroup) mListView.getItemAtPosition((int)info.id);
                i.putExtra("Matches", md);
                startActivityForResult(i, REDACT_GROUP_ACTIVITY);
                updateGroupList();
                return true;
            case R.id.deleteGroup:
                MatchesStud ms = null;
                for (MatchesStud stud: mFirebaseConnector.students) {
                    if (mFirebaseConnector.groupsIds.contains(stud.getGroup_id())){
                        ms = stud;
                        break;
                    }
                }
                if (ms == null){
                    MatchesGroup delete = (MatchesGroup) mListView.getItemAtPosition((int)info.id);
                    mFirebaseConnector.deleteGroup(delete.getId());
                    updateGroupList();}
                else
                    Toast.makeText(this, "В этой группе состоят студенты. Удалите их, прежде чем удалить группу.", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void updateGroupList () {
        groupAdapter.setArrayMyData(mFirebaseConnector.groups);
        groupAdapter.notifyDataSetChanged();
    }
    private void updateGroupIdsList () {
        groupAdapter.setArrayMyIds(mFirebaseConnector.groupsIds);
        groupAdapter.notifyDataSetChanged();
    }
    private void updateStudList () {
        groupAdapter.setArrayStuds(mFirebaseConnector.students);
        groupAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            MatchesGroup md = (MatchesGroup) data.getExtras().getSerializable("Matches");
            if (requestCode == REDACT_GROUP_ACTIVITY)
                mFirebaseConnector.updateGroup(md.getId(), md.getNumber(), md.getFaculty());
            else
                mFirebaseConnector.writeNewGroup(md.getId(), md.getNumber(), md.getFaculty());
            System.out.println(mFirebaseConnector.groups.toString());
            updateGroupList();
        }
    }

    class groupListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<MatchesGroup> arrayMyMatches;
        private ArrayList<String> arrayMyIds;
        private ArrayList<MatchesStud> arrayStuds;

        public groupListAdapter (Context ctx, ArrayList<MatchesGroup> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public ArrayList<MatchesGroup> getArrayMyData() {
            return arrayMyMatches;
        }

        public void setArrayMyData(ArrayList<MatchesGroup> arrayMyData) {
            this.arrayMyMatches = arrayMyData;
        }
        public void setArrayMyIds(ArrayList<String> arrayMyData) {
            this.arrayMyIds = arrayMyData;
        }
        public void setArrayStuds(ArrayList<MatchesStud> arrayMyData) {
            this.arrayStuds = arrayMyData;
        }

        public int getCount () {
            return arrayMyMatches.size();
        }

        public Object getItem (int position) {
            return arrayMyMatches.get(position);
        }

        @Override
        public long getItemId (int position) {
            MatchesGroup md = arrayMyMatches.get(position);
            if (md != null) {
                return position;
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.item_group, null);
            TextView vInfo = convertView.findViewById(R.id.groupInfo);
            MatchesGroup md = arrayMyMatches.get(position);
            vInfo.setText(md.getFaculty() + " №" + md.getNumber());
            return convertView;
        }
    }
}