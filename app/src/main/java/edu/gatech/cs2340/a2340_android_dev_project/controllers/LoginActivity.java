package edu.gatech.cs2340.a2340_android_dev_project.controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.facebook.login.widget.LoginButton;
import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import edu.gatech.cs2340.a2340_android_dev_project.model.UserList;

/**
 * Activity that handles the login procedure. Capable of validating
 * users as well as displaying an error for incorrect logins.
 */
public class LoginActivity extends AppCompatActivity {
    private DatabaseReference dataUserList = WelcomeActivity.getDatabase().getReference("userList");
    private static UserList userList;
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // event handler for sign in button
        Button signInButton = (Button) findViewById(R.id.loginButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignInButtonPressed(view);
            }
        });

        // reads in the userList
        dataUserList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList = dataSnapshot.getValue(UserList.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast toast = Toast.makeText(getApplicationContext(), "Couldn't get the data...",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // initializes facebook SDK
        callbackManager = CallbackManager.Factory.create();
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.fb_login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // switch to MainActivity
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Function for the sign in button's onClick method. Validates
     * the login credentials (which have been hardcoded here for now)
     * and switches to the main activity if they're right. Otherwise,
     * displays a toast message saying the credentials were invalid.
     *
     * @param v the view the OnClickListener belongs to
     */
    public void onSignInButtonPressed(View v) {

        EditText username = (EditText) findViewById(R.id.loginUser);
        EditText password = (EditText) findViewById(R.id.loginPass);

        if (userList.authenticate(username.getText().toString(), password.getText().toString())) {

            if (userList.getUser(username.getText().toString()).getBanned() == true) {
                Toast noway = Toast.makeText(getApplicationContext(), "Sorry, this account is" +
                        "currently banned", Toast.LENGTH_SHORT);
                noway.show();
            } else {
                MainActivity.setUser(userList.getUser(username.getText().toString()));
                MainActivity.setUserList(userList);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

        } else {

            Toast fail = Toast.makeText(getApplicationContext(), "Invalid Username/Password",
                    Toast.LENGTH_SHORT);
            fail.show();

        }

    }

}
