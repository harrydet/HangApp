package com.harrykristi.hangapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.harrykristi.hangapp.Models.User;
import com.harrykristi.hangapp.Models.UserProfileResponse;
import com.harrykristi.hangapp.events.DataLoadedPreviousMatchesEvent;
import com.harrykristi.hangapp.events.DataLoadedSpecificVenue;
import com.harrykristi.hangapp.events.LoadPreviousMatchesEvent;
import com.harrykristi.hangapp.events.LoadSpecificVenueEvent;
import com.harrykristi.hangapp.events.ResponseUserSearchingEvent;
import com.harrykristi.hangapp.events.StartUserSearchingEvent;
import com.harrykristi.hangapp.helpers.BusProvider;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import com.parse.ParseUser;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class VenueActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ARG_VENUEID = "param_venueId";
    private static final String ARG_VENUE_NAME = "param_venueName";
    private static final String ARG_VENUE_RATING = "param_rating";
    private Bus mBus;

    private LinearLayout imageCarousel;
    private ObservableScrollView scrollView;
    private TextView titleText;
    private TextView addressText;
    private TextView recentCheckinsText;
    private ImageView mapImage;
    private RatingBar ratingBar;
    private FloatingActionButton fab;

    private String venueId;
    private String venueName;
    private float venueRating;

    private String[] urls;
    private ImageView[] imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras() != null) {
            venueId = getIntent().getExtras().getString(ARG_VENUEID);
            venueName = getIntent().getExtras().getString(ARG_VENUE_NAME);
            venueRating = getIntent().getExtras().getFloat(ARG_VENUE_RATING);
        }

        setTitle(venueName);

        imageCarousel = (LinearLayout) findViewById(R.id.image_carousel);
        /*titleText = (TextView)findViewById(R.id.venue_activity_title);
        titleText.setText(venueName);*/
        ratingBar = (RatingBar) findViewById(R.id.venue_activity_rating);
        ratingBar.setRating(venueRating);

        addressText = (TextView) findViewById(R.id.venue_activity_address);
        mapImage = (ImageView) findViewById(R.id.venue_activity_map);

        recentCheckinsText = (TextView) findViewById(R.id.recent_checkins_textview);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        scrollView = (ObservableScrollView)findViewById(R.id.scroll_view_container);
        fab.attachToScrollView((ObservableScrollView) scrollView);
        fab.setOnClickListener(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onStop() {
        for (int i = 0, urlsLength = urls.length; i < urlsLength; i++) {
            String url = urls[i];
            imageViews[i].setImageBitmap(null);
            imageViews[i] = null;
            Picasso.with(this).invalidate(url);
        }

        System.gc();
        super.onStop();
    }

    @Override
    protected void onResume() {
        mapImage = (ImageView) findViewById(R.id.venue_activity_map);
        try {
            getBus().register(this);
        } catch (Exception e) {
            Log.d("Debug", "Bus already registered");
        }
        getBus().post(new LoadSpecificVenueEvent(venueId));
        getBus().post(new LoadPreviousMatchesEvent(ParseUser.getCurrentUser().getObjectId(), venueId));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        for (int i = 0, urlsLength = urls.length; i < urlsLength; i++) {
            String url = urls[i];
            if (imageViews[i] != null) {
                imageViews[i].setImageBitmap(null);
                imageViews[i] = null;
                urls[i] = null;
            }
            Picasso.with(this).invalidate(url);
        }
        imageViews = null;
        urls = null;

        System.gc();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                mBus.post(new StartUserSearchingEvent(ParseUser.getCurrentUser().getObjectId(), venueId));
        }
    }

    //EVENT BUS
    @Subscribe
    public void OnVenueLoaded(final DataLoadedSpecificVenue event) {
        int totalPhotos = event.getmFoursquareResponse()
                .getResponse()
                .getVenue()
                .getTotalPhotos();

        int layoutHeight = imageCarousel.getHeight();
        urls = new String[event.getmFoursquareResponse().getResponse().getVenue().getTotalPhotos()];
        imageViews = new ImageView[event.getmFoursquareResponse().getResponse().getVenue().getTotalPhotos()];
        for (int i = 0; i < totalPhotos; i++) {
            String prefix = event.getmFoursquareResponse()
                    .getResponse()
                    .getVenue()
                    .getPhotoPrefix(i);

            String suffix = event.getmFoursquareResponse()
                    .getResponse()
                    .getVenue()
                    .getPhotoSuffix(i);

            String url = prefix + layoutHeight + "x" + layoutHeight + suffix;
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setPadding(0, 0, 0, 0);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            imageCarousel.addView(imageView);
            urls[i] = url;
            imageViews[i] = imageView;
            Picasso.with(this).load(url).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.grey_placeholder).into(imageView);
        }

        final float lat = event.getmFoursquareResponse()
                .getResponse()
                .getVenue()
                .getLocation()
                .getLat();
        final float lng = event.getmFoursquareResponse()
                .getResponse()
                .getVenue()
                .getLocation()
                .getLng();
        String[] address = event.getmFoursquareResponse()
                .getResponse()
                .getVenue()
                .getLocation()
                .getFormattedAddress();
        String addressFormatted = "";
        if (address != null) {
            for (String line : address) {
                addressFormatted += line + "\n";
            }
        }

        addressText.setText(addressFormatted);
        if (mapImage != null) {
            final String finalAddressFormatted = addressFormatted;
            mapImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%s(%s)", lat, lng, finalAddressFormatted, venueName);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            });
        }

    }

    @Subscribe
    public void OnMatchesLoaded(final DataLoadedPreviousMatchesEvent event) {
        UserProfileResponse response = event.getResponse();
        List<User> users = response.getMatch_user();
        if (users != null) {
            LinearLayout recentCheckinsContainer = (LinearLayout) findViewById(R.id.recent_checkins_container);
            recentCheckinsContainer.removeAllViews();
            for (User user :
                    users) {
                CircleImageView imageView = new CircleImageView(this);
                int widthHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthHeight, widthHeight);
                params.setMargins(0, 0, 10, 0);
                imageView.setLayoutParams(params);
                imageView.setBorderColor(ContextCompat.getColor(this, R.color.pink));
                imageView.setBorderWidth(1);

                recentCheckinsContainer.addView(imageView);
                Picasso.with(this).load(event.getResponse().getProfilePictureUrl()).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).noFade().placeholder(R.drawable.grey_placeholder).into(imageView);
            }

        } else {
            recentCheckinsText.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void OnUserSearching(final ResponseUserSearchingEvent event){
        Toast.makeText(VenueActivity.this, "Reposne: " + event.getMessage(), Toast.LENGTH_LONG).show();
    }

    private Bus getBus() {
        if (mBus == null) {
            mBus = BusProvider.getInstance();
        }
        return mBus;
    }

}
