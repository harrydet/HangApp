package com.harrykristi.hangapp.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.harrykristi.hangapp.R;
import com.harrykristi.hangapp.RootApplication;
import com.harrykristi.hangapp.helpers.Config;
import com.harrykristi.hangapp.interfaces.EndPoints;
import com.harrykristi.hangapp.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Harry on 3/13/2016.
 */
public class GcmIntentService extends IntentService {

    private static final String TAG = GcmIntentService.class.getSimpleName();

    // Default constructor
    public GcmIntentService() {
        super(TAG);
    }

    public static final String KEY = "key";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";

    @Override
    protected void onHandleIntent(Intent intent) {
        String key = intent.getStringExtra(KEY);
        switch (key) {
            case SUBSCRIBE:
                // Subscribe to a topic
                String topic = intent.getStringExtra(TOPIC);
                subscribeToTopic(topic);
                break;
            case UNSUBSCRIBE:
                // Unsubscribe from topic
                String unsubTopic = intent.getStringExtra(TOPIC);
                unsubscribeFromTopic(unsubTopic);
                break;
            default:
                // If the key is not specified, register with GCM
                registerGCM();
        }
    }

    private void registerGCM() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = null;

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.e(TAG, "GCM Registration Token: " + token);

            // Send the registration id to HangApp server
            sendRegistrationToServer(token);
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception ex) {
            Log.e(TAG, "Failed to complete token refresh", ex);
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
        }

        // Notify the UI thread that registration has been completed
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // Check for a valid login session
        User user = RootApplication.getmInstance().getPrefManager().getUser();
        if(user == null) {
            // User not found, redirect to login screen
            return;
        }

        String endPoint = EndPoints.USER.replace("_ID_", user.getId());

        Log.e(TAG, "endpoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                endPoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    // Check for errors
                    if (obj.getBoolean("error") == false) {
                        // Send broadcasting token to server
                        Intent registrationComplete = new Intent(Config.SENT_TOKEN_TO_SERVER);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to send gcm registration id to our sever. " + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException ex) {
                    Log.e(TAG, "json parsing error: " + ex.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("gcm_registration_id", token);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        RootApplication.getmInstance().addToRequestQueue(strReq);
    }

    // Subscribe to a topic
    public void subscribeToTopic(String topic) {
        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        String token = null;
        try {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null) {
                pubSub.subscribe(token, "/topics/" + topic, null);
                Log.e(TAG, "Subscribed to topic: " + topic);
            } else {
                Log.e(TAG, "Error: GCM registration id is null");
            }
        } catch (IOException ex) {
            Log.e(TAG, "Topic subscribe error. Topic: " + topic + ", error: " + ex.getMessage());
            Toast.makeText(getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Unsubscribe from a topic
    public void unsubscribeFromTopic(String topic) {
        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        String token = null;
        try {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null) {
                pubSub.unsubscribe(token, "");
                Log.e(TAG, "Unsubscribed from topic: " + topic);
            } else {
                Log.e(TAG, "error: gcm registration id is null");
            }
        } catch (IOException e) {
            Log.e(TAG, "Topic unsubscribe error. Topic: " + topic + ", error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
