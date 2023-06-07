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

public class AccountsActivity extends AppCompatActivity {
    private static final int ADD_USER_ACTIVITY = 61;
    private static final int REDACT_USER_ACTIVITY = 62;
    final String TAG = "Firebase";

    FirebaseConnector mFirebaseConnector;
    Context mContext;
    ListView mListView;
    Button btnAccountsBack, btnSignOut;
    accountListAdapter accountsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        mContext = this;
        mFirebaseConnector = new FirebaseConnector();
        mListView =  findViewById(R.id.accountsList);
        accountsAdapter = new accountListAdapter(mContext, mFirebaseConnector.users);
        mListView.setAdapter(accountsAdapter);
        registerForContextMenu(mListView);
        btnAccountsBack = findViewById(R.id.btnAccountsBack);
        btnSignOut = findViewById(R.id.btnSignOutAcc);
        btnAccountsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(mContext, MainActivity.class));
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mFirebaseConnector.usersEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFirebaseConnector.users.clear();
                for(DataSnapshot gr: snapshot.getChildren()){
                    MatchesUser group = gr.getValue(MatchesUser.class);
                    mFirebaseConnector.users.add(group);
                    mFirebaseConnector.groupsIds.add(group.getId());}
                System.out.println(mFirebaseConnector.groups.toString());
                updateAccountsList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadUser:onCancelled", error.toException());
            }
        });
        mFirebaseConnector.universitiesEndpoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFirebaseConnector.universities.clear();
                for(DataSnapshot un: snapshot.getChildren()){
                    MatchesUniver univer = un.getValue(MatchesUniver.class);
                    mFirebaseConnector.universities.add(univer);
                    }
                updateAccountsList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadUniver:onCancelled", error.toException());
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accounts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addUser) {
            Intent i = new Intent(mContext, RegistrationActivity.class);
            startActivityForResult(i, ADD_USER_ACTIVITY);
            updateAccountsList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_accounts, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.editUser:
                Intent i = new Intent(mContext, RegistrationActivity.class);
                MatchesUser md = (MatchesUser) mListView.getItemAtPosition((int)info.id);
                i.putExtra("Matches", md);
                startActivityForResult(i, REDACT_USER_ACTIVITY);
                updateAccountsList();
                return true;
            case R.id.deleteUser:
                MatchesUser delete = (MatchesUser) mListView.getItemAtPosition((int)info.id);
                mFirebaseConnector.deleteUser(delete.getId());
                updateAccountsList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void updateAccountsList () {
        accountsAdapter.setArrayMyData(mFirebaseConnector.users);
        accountsAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(mContext, FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
        if (resultCode == 1) {
            MatchesUser md = (MatchesUser) data.getExtras().getSerializable("Matches");
            if (requestCode == REDACT_USER_ACTIVITY)
                mFirebaseConnector.updateUser(md.getId(), md.getName(), md.getSurname(), md.getSecond_name(), md.getEmail(), md.getUniversityId());
            else
                mFirebaseConnector.writeNewUser(md.getId(), md.getName(), md.getSurname(), md.getSecond_name(), md.getEmail(), md.getUniversityId());
            updateAccountsList();
        }
    }

    class accountListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<MatchesUser> arrayMyMatches;


        public accountListAdapter(Context ctx, ArrayList<MatchesUser> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);
        }

        public ArrayList<MatchesUser> getArrayMyData() {
            return arrayMyMatches;
        }

        public void setArrayMyData(ArrayList<MatchesUser> arrayMyData) {
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
            MatchesUser md = arrayMyMatches.get(position);
            if (md != null) {
                return position;
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.item_account, null);
            TextView vName = convertView.findViewById(R.id.accountName);
            TextView vSurname = convertView.findViewById(R.id.accountSurname);
            TextView vSecondName = convertView.findViewById(R.id.accountSecondName);
            TextView vEmail = convertView.findViewById(R.id.accountEmail);
            TextView vUniver = convertView.findViewById(R.id.accountUniver);
            MatchesUser md = arrayMyMatches.get(position);
            vName.setText(md.getName() + " ");
            vSurname.setText(md.getSurname() + " ");
            vSecondName.setText(md.getSecond_name());
            vEmail.setText("Почта: " + md.getEmail() + "; ");
            MatchesUniver mu = mFirebaseConnector.getUniver(md.getUniversityId());
            vUniver.setText("Университет: " + "\"" + mu.getTitle() + "\" г." + mu.getCity());
            return convertView;
        }
    }
}