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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by traceys5 on 3/29/17.
 */
public class CreateGroup extends AppCompatActivity {

    private ArrayList<User> names = new ArrayList<>();
    RecyclerAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Group!");

        ActivityCompat.requestPermissions(CreateGroup.this,
                new String[]{Manifest.permission.READ_CONTACTS}, 1);
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


    private void getContacts() {
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        //String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        //String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        //Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        //String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        ContentResolver content = getContentResolver();
        Cursor cursor = content.query(CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                names.add(new User(cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)), "", false, null, null));
            }
        }
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(names);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(CreateGroup.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

