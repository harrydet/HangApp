package com.harrykristi.hangapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.harrykristi.hangapp.Interfaces.EndPoints;
import com.harrykristi.hangapp.model.User;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.harrykristi.hangapp.R;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG = LoginActivity.class.getSimpleName();
    private EditText inputName, inputEmail;
    private TextInputLayout inputLayoutName, inputLayoutEmail;
    private Button btnEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Default on Create stuff
        super.onCreate(savedInstanceState);

        // Set the content view
        setContentView(R.layout.activity_login);

        // Check for login session. Redirect to main activity if already logged in
        if (RootApplication.getmInstance().getPrefManager().getUser() != null){
            startActivity(new Intent(this, AuthenticatedActivity.class));
            finish();
        }

        // Set the variables
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        btnEnter = (Button) findViewById(R.id.btn_enter);

        // Set the fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Light.otf");
        inputName.setTypeface(font);
        inputEmail.setTypeface(font);
        btnEnter.setTypeface(font);

        // On click listeners
        btnEnter.setOnClickListener(this);

        // Parse stuff
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
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
            case R.id.btn_enter:
                loginUser();
                break;
            default:
                break;
        }
    }

    public void loginUser(){
        // Front end form validation
        if (!validateName()) {
            return;
        }
        if (!validateName()) {
            return;
        }

        final String first_name = inputName.getText().toString().split(" ")[0];
        final String last_name = inputName.getText().toString().split(" ")[1];
        final String email = inputEmail.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                EndPoints.USER_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // Check for error flags
                    if (obj.getBoolean("error") == false){
                        // User was logged in successfully
                        JSONObject userObj = obj.getJSONObject("user");
                        User user = new User(userObj.getString("user_id"),
                                userObj.getString("first_name"),
                                userObj.getString("last_name"),
                                userObj.getString("email"));

                        // Store the user in shared prefs
                        RootApplication.getmInstance().getPrefManager().storeUser(user);

                        // Start the authenticated activity
                        startActivity(new Intent(getApplicationContext(), AuthenticatedActivity.class));
                        finish();
                    } else {
                        // User was not logged in - toast
                        Toast.makeText(LoginActivity.this, "" + obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException ex) {
                    Log.e(TAG, "Json parsing error: " + ex.getMessage());
                    Toast.makeText(LoginActivity.this, "Json parsing error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
        protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("name", first_name + last_name);
                params.put("email", email);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        // Add request to queue
        RootApplication.getmInstance().addToRequestQueue(stringRequest);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Validating name
    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    // Validating email
    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view){
            this.view = view;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
            }
        }
    }
}
