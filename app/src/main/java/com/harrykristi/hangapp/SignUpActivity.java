package com.harrykristi.hangapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.harrykristi.hangapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEditTextEmail;
    private EditText mEditTextName;
    private EditText mEditTextPassword;
    private EditText mConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button mSignUpButton = (Button) findViewById(R.id.sign_up_form_button);
        mSignUpButton.setOnClickListener(this);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Light.otf");

        mEditTextEmail = (EditText) findViewById(R.id.signup_email_field);
        mEditTextEmail.setTypeface(font);
        mEditTextName = (EditText) findViewById(R.id.signup_full_name_field);
        mEditTextName.setTypeface(font);
        mEditTextPassword = (EditText) findViewById(R.id.signup_password_field);
        mEditTextPassword.setTypeface(font);
        mConfirmPassword = (EditText) findViewById(R.id.signup_confirm_password_field);
        mConfirmPassword.setTypeface(font);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_up_form_button:
                userSignUp();
                break;
            default:
                break;
        }
    }

    private void userSignUp(){
        if(mEditTextPassword.getText().toString().equals(mConfirmPassword.getText().toString())){
            final ParseUser newUser = new ParseUser();
            String email = mEditTextEmail.getText().toString();

            if(isEmailValid(email)) {
                newUser.setUsername(email.split("@")[0]);
                newUser.setPassword(mEditTextPassword.getText().toString());
                newUser.setEmail(mEditTextEmail.getText().toString());
                newUser.put("Full_Name", mEditTextName.getText().toString());

                newUser.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("objectId", ParseUser.getCurrentUser().getObjectId());
                            installation.saveInBackground();
                            ParsePush.subscribeInBackground("user_" + ParseUser.getCurrentUser().getObjectId());
                            Intent authenticatedActivityIntent = new Intent(getApplicationContext(), AuthenticatedActivity.class);
                            startActivity(authenticatedActivityIntent);
                        } else {
                            //TODO: better error handling in sign-up fails
                            Toast.makeText(SignUpActivity.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
