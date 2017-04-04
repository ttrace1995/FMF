package com.projfmf.findmyfriends;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by traceys5 on 3/31/17.
 */
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    public String MY_UID;

    private Button register;
    private EditText email;
    private EditText username;
    private EditText password;
    private ProgressDialog pbarR;
    private TextView loginIfAlreadyRegistered;
    DatabaseReference dataRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fireuser;
    public User user;
    private ProgressDialog pbarL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        register = (Button) findViewById(R.id.register_button);
        email = (EditText) findViewById(R.id.email_entry);
        password = (EditText) findViewById(R.id.password_entry);
        username = (EditText) findViewById(R.id.username_entry);
        loginIfAlreadyRegistered = (TextView) findViewById(R.id.login_ifRegistered);
        pbarR = new ProgressDialog(this);
        pbarL = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(this);
        loginIfAlreadyRegistered.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == register) {
            registerUser();
        }
        else if (view == loginIfAlreadyRegistered) {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }
    }

    private void registerUser() {

        final String emailHold = email.getText().toString().trim();
        final String passHold = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailHold)) {
            Toast.makeText(RegistrationActivity.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(passHold)) {
            Toast.makeText(RegistrationActivity.this, "Please enter a password.", Toast.LENGTH_SHORT).show();
            return;
        }

        pbarR.setMessage("Registering user...");
        pbarR.show();

        firebaseAuth.createUserWithEmailAndPassword(emailHold, passHold)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Registration Sccess", Toast.LENGTH_SHORT).show();
                            user = new User(username.getText().toString().trim(), emailHold, false, "first_name", "last_name");
                            checkInformation();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            pbarR.dismiss();
                        }
                    }
                });
    }

        public void checkInformation() {
            firebaseAuth = FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() == null) {
                logInIfNot();
            }
            else {
                pbarR.dismiss();
                pbarL.show();
                saveInformation(user);
            }
    }

    public void saveInformation(User user) {
        fireuser = firebaseAuth.getCurrentUser();
        dataRef = FirebaseDatabase.getInstance().getReference();
        MY_UID = fireuser.getUid();
        writeUIDToInternalStorage(MY_UID);
        dataRef.child("Users").child(MY_UID).setValue(user);
        pbarL.dismiss();
        Toast.makeText(RegistrationActivity.this, "Saved Info", Toast.LENGTH_SHORT).show();
        startSettings();
    }

    public void writeUIDToInternalStorage(String uid) {
        String filename = "fmf_user_profile_uid.txt";
        String string = uid;
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logInIfNot() {
        pbarR.dismiss();
        pbarL.show();
        firebaseAuth.signInWithEmailAndPassword(user.getEmail(), password.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            pbarL.dismiss();
                            saveInformation(user);
                            Toast.makeText(RegistrationActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(AddUserInformationToDatabase.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            pbarL.dismiss();
                            //Toast.makeText(AddUserInformationToDatabase.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void startSettings() {
        Intent startSettings = new Intent(this, SettingsActivity.class);
        startSettings.putExtra("uid", MY_UID);
        startActivity(startSettings);
    }
}
