package com.harrykristi.hangapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.harrykristi.hangapp.R;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.Collection;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private int mShortAnimationDuration;
    private EditText userEmailEdit;
    private EditText userPasswordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Light.otf");

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

        Button mButtonSignUp = (Button) findViewById(R.id.sign_up_button);
        mButtonSignUp.setOnClickListener(this);

        Button mButtonLoginFaded = (Button) findViewById(R.id.login_button_faded_in);
        mButtonLoginFaded.setOnClickListener(this);

        TextView forgotPassword = (TextView) findViewById(R.id.forgot_password_button);
        forgotPassword.setOnClickListener(this);
        forgotPassword.setTypeface(font);

        userEmailEdit = (EditText) findViewById(R.id.user_email_login);
        userEmailEdit.setTypeface(font);
        userPasswordEdit = (EditText) findViewById(R.id.user_password_login);
        userPasswordEdit.setTypeface(font);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_up_button:
                Intent signUpIntent = new Intent(this, SignUpActivity.class);
                startActivity(signUpIntent);
                break;
            case R.id.login_button_faded_in:
                loginUser();
                break;
            case R.id.facebook_login_button:
                loginUserFacebook();
                break;
            case R.id.forgot_password_button:
                break;
            default:
                break;
        }
    }

    public void loginUser(){
        String email = userEmailEdit.getText().toString();
        String password = userPasswordEdit.getText().toString();

        ParseUser.logInInBackground(email.split("@")[0], password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    //installation.put("objectId", ParseUser.getCurrentUser().getObjectId());
                    installation.put("user", ParseUser.getCurrentUser());
                    installation.saveInBackground();
                    ParsePush.subscribeInBackground("user_" + ParseUser.getCurrentUser().getObjectId());
                    Intent authenticatedIntent = new Intent(getApplicationContext(), AuthenticatedActivity.class);
                    startActivity(authenticatedIntent);
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to log in, try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loginUserFacebook(){
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, null, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                }
            }
        });
    }

    /*private void animateLoginScreen(){

        mFieldView.setVisibility(View.VISIBLE);
        mFieldView.setAlpha(0f);

        mFieldView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        mButtonView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mButtonView.setVisibility(View.GONE);
                    }
                });
    }

    private void animateLoginScreenReverse(){

        mButtonView.setVisibility(View.VISIBLE);
        mButtonView.setAlpha(0f);

        mButtonView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        mFieldView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFieldView.setVisibility(View.GONE);
                    }
                });
    }*/
}
