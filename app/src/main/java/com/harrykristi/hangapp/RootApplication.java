package com.harrykristi.hangapp;

import android.app.Application;
import android.widget.Toast;

import com.harrykristi.hangapp.Interfaces.HangAppAPI;
import com.harrykristi.hangapp.events.ApiErrorEvent;
import com.harrykristi.hangapp.helpers.BusProvider;
import com.harrykristi.hangapp.Interfaces.FoursquareAPI;
import com.harrykristi.hangapp.helpers.HangAppPreferenceManager;
import com.harrykristi.hangapp.services.DataService;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit.RestAdapter;

public class RootApplication extends Application {

    private DataService mDataService;
    private Bus mBus = BusProvider.getInstance();

    public static final String TAG = RootApplication.class
            .getSimpleName();

    private static RootApplication mInstance;

    private HangAppPreferenceManager pref;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this, "ksrIjYDdes638QylApGYcrWTyn1bGp3ra4ED8N41", "hjMXCVvA4j20ct80jGVnmDhRygEzTGwEGwk9jfhY");
        ParseFacebookUtils.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        mDataService = new DataService(buildFoursquareApi(), buildHangAppApi(), mBus);
        mBus.register(mDataService);

        mBus.register(this);
    }

    public static synchronized RootApplication getmInstance(){
        return mInstance;
    }

    public HangAppPreferenceManager getPrefManager(){
        if (pref == null){
            pref = new HangAppPreferenceManager(this);
        }

        return pref;
    }

    private HangAppAPI buildHangAppApi() {
        return new RestAdapter.Builder()
                        .setEndpoint("http://178.62.117.251/rest_endpoints/v1")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .build()
                        .create(HangAppAPI.class);
    }

    private FoursquareAPI buildFoursquareApi(){
        return new RestAdapter.Builder()
                        .setEndpoint("https://api.foursquare.com/v2")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .build()
                        .create(FoursquareAPI.class);
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event){
        Toast.makeText(RootApplication.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();

    }
}
