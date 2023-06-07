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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreatorActivity extends AppCompatActivity {
    private static final int ADD_UNIVER_ACTIVITY = 11;
    private static final int REDACT_UNIVER_ACTIVITY = 12;
    final String TAG = "Firebase";
    Context mContext;
    Button btnSignOut, btnAccounts;
    ListView mListView;
    FirebaseConnector mFirebaseConnector;
    univerListAdapter univerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator);
        mContext = this;
        btnSignOut = findViewById(R.id.btnSignOutCreator);
        btnAccounts = findViewById(R.id.btnAccountsView);
        mListView = findViewById(R.id.universList);
        mFirebaseConnector = new FirebaseConnector();
        univerAdapter = new univerListAdapter(mContext, mFirebaseConnector.universities);
        mListView.setAdapter(univerAdapter);
        registerForContextMenu(mListView);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(mContext, MainActivity.class));
            }
        });
        btnAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AccountsActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mFirebaseConnector.universitiesEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFirebaseConnector.universities.clear();
                for(DataSnapshot un: snapshot.getChildren()){
                    MatchesUniver univer = un.getValue(MatchesUniver.class);
                    mFirebaseConnector.universities.add(univer);}
                updateUniverList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadUniver:onCancelled", error.toException());
            }
        });
        mFirebaseConnector.groupsEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFirebaseConnector.groups.clear();
                for(DataSnapshot gr: snapshot.getChildren()){
                    MatchesGroup group = gr.getValue(MatchesGroup.class);
                    mFirebaseConnector.groups.add(group);}
                updateUniverList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadGroup:onCancelled", error.toException());
            }
        });}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_creator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addUniver:
                Intent i = new Intent(mContext, AddUniver.class);
                startActivityForResult(i, ADD_UNIVER_ACTIVITY);
                updateUniverList();
                return true;
            case R.id.exitCreator:
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
        inflater.inflate(R.menu.context_menu_creator, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.editUniver:
                Intent i = new Intent(mContext, AddUniver.class);
                MatchesUniver md = (MatchesUniver) mListView.getItemAtPosition((int)info.id);
                i.putExtra("Matches", md);
                startActivityForResult(i, REDACT_UNIVER_ACTIVITY);
                updateUniverList();
                return true;
            case R.id.deleteGroup:
                MatchesUniver delete = (MatchesUniver) mListView.getItemAtPosition((int)info.id);
                MatchesGroup mu = null;
                for (MatchesGroup gr: mFirebaseConnector.groups) {
                    if (delete.getId().equals(gr.getUniversityId())){
                        mu = gr;
                        break;
                    }
                }
                if (mu == null){
                    mFirebaseConnector.deleteUniver(delete.getId());
                    updateUniverList();}
                else
                    Toast.makeText(this, "Сначала удали данные об университете", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            MatchesUniver md = (MatchesUniver) data.getExtras().getSerializable("Matches");
            if (requestCode == REDACT_UNIVER_ACTIVITY)
                mFirebaseConnector.updateUniversity(md.getId(), md.getTitle(), md.getCity());
            else
                mFirebaseConnector.writeNewUniversity(md.getId(), md.getTitle(), md.getCity());
            System.out.println(mFirebaseConnector.groups.toString());
            updateUniverList();
        }
    }
    private void updateUniverList(){
        univerAdapter.setArrayMyData(mFirebaseConnector.universities);
        univerAdapter.notifyDataSetChanged();
    }
    class univerListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<MatchesUniver> arrayMyMatches;

        public univerListAdapter (Context ctx, ArrayList<MatchesUniver> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public ArrayList<MatchesUniver> getArrayMyData() {
            return arrayMyMatches;
        }

        public void setArrayMyData(ArrayList<MatchesUniver> arrayMyData) {
            this.arrayMyMatches = arrayMyData;
        }

        public int getCount () {
            return arrayMyMatches.size();
        }

        public Object getItem (int position) {
            return arrayMyMatches.get(position);
        }

        @Override
        public long getItemId (int position) {
            MatchesUniver md = arrayMyMatches.get(position);
            if (md != null) {
                return position;
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.item_univer, null);
            TextView vInfo = convertView.findViewById(R.id.univerInfo);
            MatchesUniver md = arrayMyMatches.get(position);
            vInfo.setText("\"" + md.getTitle() + "\" г." + md.getCity());
            return convertView;
        }
    }
}