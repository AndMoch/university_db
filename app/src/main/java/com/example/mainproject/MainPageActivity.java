package com.example.mainproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {
    private static final int ADD_GROUP_ACTIVITY = 11;
    private static final int REDACT_GROUP_ACTIVITY = 12;
    DBMatches mDBConnector;

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
        mDBConnector = new DBMatches(this);
        mListView =  findViewById(R.id.groupList);
        groupAdapter = new groupListAdapter(mContext, mDBConnector.selectAllGroups());
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
                Intent intent = new Intent(mContext, StudentsListActivity.class);
                intent.putExtra("thisGroupId", (long) position);
                startActivity(intent);
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
                mDBConnector.deleteAllGroups();
                updateGroupList();
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
                MatchesGroup delete = (MatchesGroup) mListView.getItemAtPosition((int)info.id);
                mFirebaseConnector.deleteGroup(delete.getId());
                updateGroupList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void updateGroupList () {
        groupAdapter.setArrayMyData(mFirebaseConnector.getAllGroups());
        groupAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            MatchesGroup md = (MatchesGroup) data.getExtras().getSerializable("Matches");
            if (requestCode == REDACT_GROUP_ACTIVITY)
                mDBConnector.updateGroup(md);
            else
                mDBConnector.insertGroup(md.getNumber(), md.getFaculty());
            updateGroupList();
        }
    }

    class groupListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<MatchesGroup> arrayMyMatches;

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

        public int getCount () {
            return arrayMyMatches.size();
        }

        public Object getItem (int position) {
            return position;
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