package com.projfmf.findmyfriends;

import android.app.ProgressDialog;
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

/**
 * Created by traceys5 on 4/3/17.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signinButton;
    private EditText signinEmail;
    private EditText signinPass;
    ProgressDialog pbar;
    private TextView textViewRegisterIfNot;
    private FirebaseAuth fireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        fireAuth = FirebaseAuth.getInstance();
        if (fireAuth.getCurrentUser() != null) {
            //shit here
        }

        signinButton = (Button) findViewById(R.id.login_button);
        signinEmail = (EditText) findViewById(R.id.email_entry_login);
        signinPass = (EditText) findViewById(R.id.password_entry_login);
        textViewRegisterIfNot = (TextView) findViewById(R.id.textview_ifnotregister);
        pbar = new ProgressDialog(this);

        signinButton.setOnClickListener(this);
        textViewRegisterIfNot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == signinButton) {
            userLogin();
        }
        if (v == textViewRegisterIfNot) {
            finish();
            Intent register = new Intent(this, RegistrationActivity.class);
            startActivity(register);
        }
    }

    public void userLogin() {
        final String emailHold = signinEmail.getText().toString().trim();
        String passHold = signinPass.getText().toString().trim();

        if (TextUtils.isEmpty(emailHold)) {
            Toast.makeText(LoginActivity.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(passHold)) {
            Toast.makeText(LoginActivity.this, "Please enter a password.", Toast.LENGTH_SHORT).show();
            return;
        }

        pbar.setMessage("Logging in...");
        pbar.show();

        fireAuth.signInWithEmailAndPassword(emailHold, passHold)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            finish();
                            pbar.dismiss();
                            Toast.makeText(LoginActivity.this, "Welcome, "+emailHold, Toast.LENGTH_SHORT).show();
                            Intent main = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(main);

                        }
                        else {
                            pbar.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Failed! :(", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


}
