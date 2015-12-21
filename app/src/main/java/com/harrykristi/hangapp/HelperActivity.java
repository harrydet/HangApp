package com.harrykristi.hangapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.ParseUser;

public class HelperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startingActivity;
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){
            startingActivity = new Intent(this, AuthenticatedActivity.class);
        } else {
            startingActivity = new Intent(this, LoginActivity.class);
        }
        startActivity(startingActivity);
    }
}
