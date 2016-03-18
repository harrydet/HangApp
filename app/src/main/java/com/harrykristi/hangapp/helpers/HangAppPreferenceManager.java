package com.harrykristi.hangapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.harrykristi.hangapp.model.User;

public class HangAppPreferenceManager {
    private String TAG = HangAppPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared preferences mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "hangapp_sp";

    // Shared preference keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_FIRST_NAME = "first_name";
    private static final String KEY_USER_LAST_NAME = "last_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";

    // Constructor
    public HangAppPreferenceManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // Store a user in shared prefs
    public void storeUser(User user){
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_FIRST_NAME, user.getFirst_name());
        editor.putString(KEY_USER_LAST_NAME, user.getLast_name());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.commit();

        Log.e(TAG, "User is stored in shared preferences. " + user.getFirst_name() + " " + user.getLast_name() + ", " + user.getEmail());
    }

    // Get a user from shared prefs
    public User getUser(){
        if (pref.getString(KEY_USER_ID, null) != null){
            String id, first_name, last_name, email;
            id = pref.getString(KEY_USER_ID, null);
            first_name = pref.getString(KEY_USER_FIRST_NAME, null);
            last_name = pref.getString(KEY_USER_LAST_NAME, null);
            email = pref.getString(KEY_USER_EMAIL, null);

            User user = new User(id, first_name, last_name, email);
            return user;
        }
        return null;
    }

    // Add a notification
    public void addNotification(String notification){
        // Get the old notifications
        String oldNotifications = getNotifications();

        if(oldNotifications != null){
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    // Get the notifications
    public String getNotifications(){
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    // Clear the editor values
    public void clear(){
        editor.clear();
        editor.commit();
    }


}
