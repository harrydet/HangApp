package com.harrykristi.hangapp;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.harrykristi.hangapp.Interfaces.AuthenticatedActivityCallbacks;
import com.harrykristi.hangapp.model.User;
import com.harrykristi.hangapp.model.UserProfileResponse;
import com.harrykristi.hangapp.model.VenueFoursquare;
import com.harrykristi.hangapp.events.DataLoadedUserEvent;
import com.harrykristi.hangapp.events.GetUserPictureEvent;
import com.harrykristi.hangapp.gcm.GcmIntentService;
import com.harrykristi.hangapp.helpers.BusProvider;
import com.harrykristi.hangapp.helpers.Config;
import com.parse.ParseUser;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AuthenticatedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, AuthenticatedActivityCallbacks{

    CircleImageView userProfilePicture;
    private Bus mBus;
    private CoordinatorLayout coordinatorLayout;
    private static final String ARG_VENUEID = "param_venueId";
    private static final String ARG_VENUE_NAME = "param_venueName";
    private static final String ARG_VENUE_RATING = "param_rating";

    // User - The current user
    User user;

    private String TAG = AuthenticatedActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticated);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check for active login, redirect to login activity if none
        if ((user = RootApplication.getmInstance().getPrefManager().getUser()) == null){
            launchLoginActivity();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.logo_actionbar);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_authenticated);
        TextView userName = (TextView) headerView.findViewById(R.id.header_user_name);
        TextView userEmail = (TextView) headerView.findViewById(R.id.header_user_email);
        userProfilePicture = (CircleImageView) headerView.findViewById(R.id.header_profile_picture);
        getBus().register(this);
        getBus().post(new GetUserPictureEvent(user.getId()));

        userName.setText(user.getFirst_name() + user.getLast_name());
        userEmail.setText(user.getEmail());

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_layout, SearchFragment.newInstance("a", "b"));
        ft.commit();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Check for type of intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // GCM successfully registered, subscribe to global topic for notifications
                    String token = intent.getStringExtra("token");

                    Toast.makeText(getApplicationContext(), "GCM Token: " + token, Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // GCM registration id is stored in server
                    Toast.makeText(getApplicationContext(), "GCM on server", Toast.LENGTH_LONG).show();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)){
                    // Push notification is received
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_layout);
                    if(currentFragment instanceof MessagesFragment) {
                        MessagesFragment messagesFragment = (MessagesFragment) currentFragment;
                        messagesFragment.handlePushNotification(intent);
                        Toast.makeText(getApplicationContext(), "Push notification received", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        if(checkPlayServices()) {
            registerGCM();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        // Register complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // Register new push message receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    // Register with the GCM service
    private void registerGCM(){
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    private boolean checkPlayServices(){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS){
            if (apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google play services are not installed");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google play services are not installed", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.authenticated, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_log_out:
                userLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (id) {
            case R.id.nav_match:
                ft.replace(R.id.fragment_layout, SearchFragment.newInstance("a", "b"));
                break;
            case R.id.nav_profile:
                ft.replace(R.id.fragment_layout, ProfileFragment.newInstance("a", "b"));
                break;
            case R.id.nav_messages:
                ft.replace(R.id.fragment_layout, MessagesFragment.newInstance("a", "b"));
                break;
            case R.id.nav_account:
                break;
            case R.id.nav_settings:
                break;
            default:
                break;
        }
        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Bus getBus() {
        if (mBus == null) {
            mBus = BusProvider.getInstance();
        }
        return mBus;
    }

    public void userLogout() {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    public void onSearchFragmentInteraction(VenueFoursquare venueFoursquare, View clickedView) {
        Intent i = new Intent(AuthenticatedActivity.this, VenueActivity.class);

        View sharedViewText = clickedView.findViewById(R.id.card_venue_name);
        View sharedRatingBar = clickedView.findViewById(R.id.venue_card_rating);
        String transitionNameText = getString(R.string.text_transit);
        String transitionNameRatingBar = getString(R.string.rating_bar_transit);

        View sharedViewImage = clickedView.findViewById(R.id.card_round_photo);
        String transitionNameImage = getString(R.string.image_transit);

        i.putExtra(ARG_VENUEID, venueFoursquare.getId());
        i.putExtra(ARG_VENUE_NAME, venueFoursquare.getName());
        i.putExtra(ARG_VENUE_RATING, venueFoursquare.getRating());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(AuthenticatedActivity.this, sharedRatingBar, transitionNameRatingBar);
            startActivity(i, transitionActivityOptions.toBundle());
        } else {
            startActivity(i);
        }

    }

    @Override
    public void onProfileFragmentInteraction(Uri uri) {

    }

    @Subscribe
    public void onDataLoaded(DataLoadedUserEvent event) {
        UserProfileResponse response = event.getResponse();
        int status = event.getStatus();
        if (response.isError()) {
            if (status != 404) {
                Toast.makeText(AuthenticatedActivity.this, "Something went wrong while getting the profile picture" +
                        response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            String url = response.getProfilePictureUrl();
            Picasso.with(this).load(url).placeholder(R.drawable.default_profile_icon).noFade().into(userProfilePicture);
            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("profile_picture_url", url);
            editor.commit();
        }

    }

    @Override
    public void OnImageUpdated(String url) {
        Picasso.with(this).load(url).placeholder(R.drawable.default_profile_icon).noFade().into(userProfilePicture);
    }

    @Override
    public void DisplaySnackBarWith(String message) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launchUploadProcess(true);
            }
        });
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(AuthenticatedActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public BroadcastReceiver getmRegistrationBroadcastReceiver(){
        return mRegistrationBroadcastReceiver;
    }
}
