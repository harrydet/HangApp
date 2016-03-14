package com.harrykristi.hangapp.gcm;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Harry on 3/14/2016.
 */
public class InstanceIdListenerService extends InstanceIDListenerService {
    private static final String TAG = InstanceIdListenerService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        Log.e(TAG, "onTokenRefresh");

        // Fetch the updated Instance ID token and notify app server of changes
        Intent intent = new Intent(this, GcmIntentService.class);
        startService(intent);
    }
}
