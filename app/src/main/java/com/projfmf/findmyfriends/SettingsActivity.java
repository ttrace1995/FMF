package com.projfmf.findmyfriends;

/**
 * Created by traceys5 on 3/26/17.
 */

import android.app.FragmentManager;
        import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.preference.EditTextPreference;
        import android.preference.Preference;
        import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
        import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class SettingsActivity extends PreferenceActivity  {

    public static String MY_UID;

    //SharedPreferences preferences;
    PreferenceFragment myPrefs;
    FragmentManager fragMan;
    FragmentTransaction fragTran;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MY_UID = readUIDFromInternalStorage();

        // Display the fragment as the main content.
        fragMan = getFragmentManager();
        fragTran = fragMan.beginTransaction();
        myPrefs = new PrefsFragment();
        fragTran.replace(android.R.id.content, myPrefs);
        fragTran.commit();
    }

    public String readUIDFromInternalStorage() {
        FileInputStream fis = null;
        try {
            fis = this.openFileInput("fmf_user_profile_uid.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString().trim();
    }

    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        public String MY_UID;

        //Database Connection
        DatabaseReference dataRef;
        FirebaseAuth firebaseAuth;
        FirebaseUser fireuser;

        //Initialize Preference Boxes
        EditTextPreference user;
        EditTextPreference fname;
        EditTextPreference lname;

        //Initialize value holders
        PreferenceCategory profTitle;
        String username_value;
        String email_value;
        String fname_value;
        String lname_value;
        SharedPreferences prefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            MY_UID = SettingsActivity.MY_UID;

            firebaseAuth = FirebaseAuth.getInstance();

            profTitle = (PreferenceCategory)  findPreference("prof_settings_title");
            user = (EditTextPreference) findPreference("username");
            fname = (EditTextPreference) findPreference("firstName");
            lname = (EditTextPreference) findPreference("lastName");

            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

            if (firebaseAuth.getCurrentUser() == null) {
                profTitle.setTitle("Profile Settings (please sign in)");
                user.setSummary(sp.getString("username", "please sign in"));
                fname.setSummary(sp.getString("firstName", "please sign in"));
                lname.setSummary(sp.getString("lastName", "please sign in"));
            }
            else {
                fireuser = firebaseAuth.getCurrentUser();
                dataRef = FirebaseDatabase.getInstance().getReference();
                email_value = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                profTitle.setTitle("Profile Settings (" + email_value + ")");
                user.setSummary(sp.getString("username", username_value));
                fname.setSummary(sp.getString("firstName", fname_value));
                lname.setSummary(sp.getString("lastName", lname_value));
                //MY_UID = prefs.getString("UID", MY_UID);


                dataRef.child("Users").child(MY_UID).child("username").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        username_value = snapshot.getValue().toString();
                        user.setSummary(username_value);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                dataRef.child("Users").child(MY_UID).child("firstName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        fname_value = snapshot.getValue().toString();
                        fname.setSummary(fname_value);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                dataRef.child("Users").child(MY_UID).child("lastName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        lname_value = snapshot.getValue().toString();
                        lname.setSummary(lname_value);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = findPreference(key);
            if (pref instanceof EditTextPreference && pref.getKey().equals("username")) {
                EditTextPreference newText = (EditTextPreference) pref;
                if (firebaseAuth.getCurrentUser() != null) {
                    username_value = newText.getText();
                    dataRef.child("Users").child(MY_UID).child(key).setValue(username_value);
                }
            }
            else if (pref instanceof EditTextPreference && pref.getKey().equals("firstName")) {
                EditTextPreference newText = (EditTextPreference) pref;
                if (firebaseAuth.getCurrentUser() != null) {
                    fname_value = newText.getText();
                    dataRef.child("Users").child(MY_UID).child(key).setValue(fname_value);
                }
            }
            else if (pref instanceof EditTextPreference && pref.getKey().equals("lastName")) {
                EditTextPreference newText = (EditTextPreference) pref;
                if (firebaseAuth.getCurrentUser() != null) {
                    lname_value = newText.getText();
                    dataRef.child("Users").child(MY_UID).child(key).setValue(lname_value);
                }
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("username", username_value);
            outState.putString("firstName", fname_value);
            outState.putString("lastName", lname_value);
            outState.putString("UID", MY_UID);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (savedInstanceState != null) {
                username_value = savedInstanceState.getString("username");
                fname_value = savedInstanceState.getString("firstName");
                lname_value = savedInstanceState.getString("lastName");
                MY_UID = savedInstanceState.getString("UID");
            }
        }



    }
}