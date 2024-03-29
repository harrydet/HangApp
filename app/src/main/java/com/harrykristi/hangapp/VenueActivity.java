package com.harrykristi.hangapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.harrykristi.hangapp.model.CompactVenue;
import com.harrykristi.hangapp.model.TipVenue;
import com.harrykristi.hangapp.model.User;
import com.harrykristi.hangapp.model.UserProfileResponse;
import com.harrykristi.hangapp.events.DataLoadedPreviousMatchesEvent;
import com.harrykristi.hangapp.events.DataLoadedSpecificVenue;
import com.harrykristi.hangapp.events.LoadPreviousMatchesEvent;
import com.harrykristi.hangapp.events.LoadSimilarVenuesEvent;
import com.harrykristi.hangapp.events.LoadSpecificVenueEvent;
import com.harrykristi.hangapp.events.ResponseUserSearchingEvent;
import com.harrykristi.hangapp.events.SimilarVenuesLoadedEvent;
import com.harrykristi.hangapp.events.StartUserSearchingEvent;
import com.harrykristi.hangapp.helpers.BusProvider;
import com.harrykristi.hangapp.helpers.ResizeAnimation;
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
    private final String PUB_URL="https://ss3.4sqi.net/img/categories_v2/nightlife/pub_88.png";

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

    private boolean similarVenuesSent;
    private boolean tipsSent;

    // User - The current user
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check for active login, redirect to login activity if none
        if ((user = RootApplication.getmInstance().getPrefManager().getUser()) == null){
            launchLoginActivity();
        }

        similarVenuesSent = false;
        tipsSent = false;

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
            if(imageViews[i] != null){
                imageViews[i].setImageBitmap(null);
                imageViews[i] = null;
            }
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
        getBus().post(new LoadPreviousMatchesEvent(user.getId(), venueId));
        if(!similarVenuesSent){
            getBus().post(new LoadSimilarVenuesEvent(venueId));
        }
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
                mBus.post(new StartUserSearchingEvent(RootApplication.getmInstance().getPrefManager().getUser().getId(), venueId));
                break;
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
        final String [] urlsFullRes = new String[event.getmFoursquareResponse().getResponse().getVenue().getTotalPhotos()];
        imageViews = new ImageView[event.getmFoursquareResponse().getResponse().getVenue().getTotalPhotos()];

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();

        //We get width and height in pixels here
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        for(int i = 0; i < totalPhotos; i++){
            String prefix = event.getmFoursquareResponse()
                    .getResponse()
                    .getVenue()
                    .getPhotoPrefix(i);

            String suffix = event.getmFoursquareResponse()
                    .getResponse()
                    .getVenue()
                    .getPhotoSuffix(i);
            String url = prefix + layoutHeight + "x" + layoutHeight + suffix;
            urls[i] = url;
            urlsFullRes[i] = prefix + width + "x" + height + suffix;
        }
        for (int i = 0; i < totalPhotos; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setPadding(0, 0, 0, 0);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            final int position = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VenueActivity.this, FullScreenViewActivity.class);
                    intent.putExtra("position", position);

                    Bundle b = new Bundle();
                    b.putStringArray("data", urlsFullRes);

                    intent.putExtras(b);
                    VenueActivity.this.startActivity(intent);
                }
            });

            imageCarousel.addView(imageView);
            imageViews[i] = imageView;
            Picasso.with(this).load(urls[i]).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).placeholder(R.drawable.grey_placeholder).into(imageView);
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

        if(!tipsSent){

            //Populate tips
            LinearLayout lv = (LinearLayout)findViewById(R.id.comments_listview);
            LayoutInflater li = LayoutInflater.from(this);

            List<TipVenue> tipVenues = event.getmFoursquareResponse().getResponse().getVenue().getTips();
            int totalTips  = tipVenues.size();
            if(totalTips > 4){
                totalTips = 4;
            }
            for(int i = 0; i < totalTips; i++){
                final View viewText = li.inflate(R.layout.tip_list_item, lv, false);
                viewText.setId(0);
                final TextView tv = (TextView) viewText.findViewById(R.id.tip_text);
                tv.setText(tipVenues.get(i).getText());
                final int textCharacters = tipVenues.get(i).getText().length();

                CircleImageView iv = (CircleImageView) viewText.findViewById(R.id.tip_profile_picture);
                Picasso.with(this).load(tipVenues.get(i).getUser().getPhoto().getPrefix()+"300x300"+
                        tipVenues.get(i).getUser().getPhoto().getSuffix()).noFade().placeholder(R.drawable.grey_placeholder_small).into(iv);
                final int generatedHeight = tv.getHeight();
                viewText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ResizeAnimation resizeAnimation;
                        if(v.getId() == 0){
                            resizeAnimation = new ResizeAnimation(v, dpToPx(((int)Math.floor(textCharacters/33) + 1)*generatedHeight));
                            tv.setSingleLine(false);
                            tv.setMaxLines((int)Math.floor(textCharacters/33) + 1);
                            //noinspection ResourceType
                            v.setId(1);
                        } else {
                            resizeAnimation = new ResizeAnimation(v, dpToPx(40));
                            tv.setSingleLine(true);
                            tv.setMaxLines(1);
                            v.setId(0);
                        }
                        resizeAnimation.setDuration(600);
                        v.startAnimation(resizeAnimation);

                    }
                });

                lv.addView(viewText);
            }
            tipsSent = true;
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
                Picasso.with(this).load(user.getProfilePictureUrl()).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).noFade().placeholder(R.drawable.grey_placeholder).into(imageView);
            }

        } else {
            recentCheckinsText.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void OnUserSearching(final ResponseUserSearchingEvent event){
        Toast.makeText(VenueActivity.this, "Reposne: " + event.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void OnSimilarVenuesLoaded(final SimilarVenuesLoadedEvent event){
        if(!similarVenuesSent){
            //Populate similar venues
            LinearLayout lvVenues = (LinearLayout)findViewById(R.id.similar_places_listview);
            LayoutInflater liVenues = LayoutInflater.from(this);

            CompactVenue[] venues = event.getResponse().getResponse().getSimilarVenues().getItems();
            int totalVenuesToDisplay = venues.length;
            if(totalVenuesToDisplay > 4)
                totalVenuesToDisplay = 4;
            for(int i = 0; i < totalVenuesToDisplay; i++){
                View viewText = liVenues.inflate(R.layout.similar_venue_list_item, lvVenues, false);
                TextView tv = (TextView) viewText.findViewById(R.id.similar_venue_text);
                tv.setText(venues[i].getName());

                CircleImageView iv = (CircleImageView) viewText.findViewById(R.id.similar_venue_profile_picture);
                Picasso.with(this).load(venues[i].getCategories()[0].getIcon().getPrefix()+"64"+venues[i].getCategories()[0].getIcon().getSuffix()).noFade().placeholder(R.drawable.grey_placeholder_small).into(iv);

                lvVenues.addView(viewText);

            }
            similarVenuesSent = true;
        }

    }

    private Bus getBus() {
        if (mBus == null) {
            mBus = BusProvider.getInstance();
        }
        return mBus;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(VenueActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
