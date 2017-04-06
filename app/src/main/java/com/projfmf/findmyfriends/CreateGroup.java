package com.projfmf.findmyfriends;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by traceys5 on 3/29/17.
 */
public class CreateGroup extends AppCompatActivity {

    private ArrayList<User> userDisplays = new ArrayList<>();
    RecyclerAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Group!");

        getUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_settings:
                break;
            case R.id.finished_icon:
                String data = "";
                ArrayList<User> chosen = (ArrayList<User>) ((RecyclerAdapter) adapter)
                        .getPersonList();
                for (int i = 0; i < chosen.size(); i++) {
                    User singleUser = chosen.get(i);
                    if (singleUser.getIsSelected() == true) {
                        data = data + "\n" + singleUser.getUsername().toString();
                        Toast.makeText(CreateGroup.this, data, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

        public void getUsers() {
            //Get datasnapshot at your "users" root node
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            ref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of users in datasnapshot
                            collectUserInfo((Map<String,Object>) dataSnapshot.getValue());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
    }

    private void collectUserInfo(Map<String,Object> users) {
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){
            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            userDisplays.add((User) new User(singleUser.get("username").toString(),
                    singleUser.get("email").toString(), false, singleUser.get("firstName").toString(),
                    singleUser.get("lastName").toString()));
        }
        createRecycler();
    }

    public void createRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(userDisplays);
        recyclerView.setAdapter(adapter);
    }
}

