package com.harrykristi.hangapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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

import com.harrykristi.hangapp.Models.VenueFoursquare;
import com.parse.ParseUser;

public class AuthenticatedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchFragment.OnFragmentInteractionListener,
                    ProfileFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticated);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        userName.setText(ParseUser.getCurrentUser().get("Full_Name").toString());
        userEmail.setText(ParseUser.getCurrentUser().getEmail());

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_layout, SearchFragment.newInstance("a", "b"));
        ft.commit();
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
        switch (id){
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
        switch (id){
            case R.id.nav_match:
                ft.replace(R.id.fragment_layout, SearchFragment.newInstance("a", "b"));
                break;
            case R.id.nav_profile:
                ft.replace(R.id.fragment_layout, ProfileFragment.newInstance("a", "b"));
                break;
            case R.id.nav_messages:
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

    public void userLogout(){
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    public void onSearchFragmentInteraction(VenueFoursquare venueFoursquare) {
        Toast.makeText(AuthenticatedActivity.this, venueFoursquare.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProfileFragmentInteraction(Uri uri) {

    }
}