package com.harrykristi.hangapp.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.harrykristi.hangapp.AuthenticatedActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;


public class ExtendedPushReceiver extends ParsePushBroadcastReceiver {
    private enum REQUEST {
        MATCHED_TO, BROADCAST
    }

    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        try {
            JSONObject payload = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            if (payload.getString("action").equals("MATCHED_TO")) {
                Intent i = new Intent(context, AuthenticatedActivity.class);
                i.putExtras(intent.getExtras());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
