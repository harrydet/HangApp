package com.harrykristi.hangapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.harrykristi.hangapp.Adapters.VenueCardAdapter;
import com.harrykristi.hangapp.Interfaces.RecyclerViewClickListener;
import com.harrykristi.hangapp.events.DataLoadedVenueEvent;
import com.harrykristi.hangapp.events.LoadVenuesEvent;
import com.harrykristi.hangapp.helpers.BusProvider;
import com.harrykristi.hangapp.Models.Response;
import com.harrykristi.hangapp.Models.VenueFoursquare;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
                                                        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,
                                                        EndlessRecyclerViewAdapter.RequestToLoadMoreListener, RecyclerViewClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_updates";
    private static final String LOCATION_KEY = "location_key";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last update time";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Bus mBus;

    private int mAnimationDuration;

    private RecyclerView recyclerView;
    private LinearLayout initialView;

    private RecyclerView.LayoutManager mLayoytManager;
    private VenueCardAdapter mAdapter;

    private Response response;

    private OnFragmentInteractionListener mListener;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates;

    private EditText mAreaSearchEdit;
    private Spinner rightSpinner;

    private boolean loadMoreEventProcessed;
    private boolean shouldLoadMore;

    private int mOffset;

    private final int[] radi = {1000, 5000, 10000, 20000};
    private EndlessRecyclerViewAdapter endlessRecyclerViewAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mOffset = 0;
        loadMoreEventProcessed = false;
        shouldLoadMore = true;

        mRequestingLocationUpdates = true;
        createLocationRequest();
        buildGoogleApiClient();
        response = null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initialView = (LinearLayout) view.findViewById(R.id.initial_view);

        final Spinner leftSpinner = (Spinner) view.findViewById(R.id.left_spinner);
        final ArrayAdapter leftAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.left_spinner_data, R.layout.left_spinner_item_layout);
        leftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leftSpinner.setAdapter(leftAdapter);
        leftSpinner.setOnItemSelectedListener(this);

        rightSpinner = (Spinner) view.findViewById(R.id.right_spinner);
        final ArrayAdapter rightAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.right_spinner_data, R.layout.right_spinner_item_layout);
        rightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rightSpinner.setAdapter(rightAdapter);

        final ProgressBar loadingSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);

        mAreaSearchEdit = (EditText) view.findViewById(R.id.edit);
        mAreaSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mLastLocation != null) {
                    int radius = radi[rightSpinner.getSelectedItemPosition()];
                    shouldLoadMore = true;
                    getBus().post(new LoadVenuesEvent(LoadVenuesEvent.FOURSQUARE, String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()),
                            mAreaSearchEdit.getText().toString(), true, radius, 0, true));
                }
                loadingSpinner.setVisibility(View.VISIBLE);
            }
        });

        rightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mLastLocation != null) {
                    int radius = radi[rightSpinner.getSelectedItemPosition()];
                    shouldLoadMore = true;
                    getBus().post(new LoadVenuesEvent(LoadVenuesEvent.FOURSQUARE, String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()),
                            mAreaSearchEdit.getText().toString(), true, radius, 0, true));
                }
                //loadingSpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadingSpinner.setVisibility(View.INVISIBLE);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_container);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);

        mLayoytManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoytManager);

        mAdapter = new VenueCardAdapter(response, getContext(), this);
        endlessRecyclerViewAdapter = new EndlessRecyclerViewAdapter(getContext(), mAdapter, this);
        recyclerView.setAdapter(endlessRecyclerViewAdapter);

        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        getBus().register(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null){
            updateValuesFromBundle(savedInstanceState);
        }
    }

    //TODO proper handling of returned event
    @Subscribe
    public void onDataLoaded(DataLoadedVenueEvent event) {
        ProgressBar loadingSpinner = (ProgressBar) this.getView().findViewById(R.id.progress_spinner);
        loadingSpinner.setVisibility(View.INVISIBLE);
        if(mAdapter.getItemCount() == 0 || event.getRefresh()){
            response = event.getResponse();
            mAdapter = new VenueCardAdapter(response, getContext(), this);
            endlessRecyclerViewAdapter = new EndlessRecyclerViewAdapter(getContext(), mAdapter, this);
            recyclerView.setAdapter(endlessRecyclerViewAdapter);
            animateResultsScreen();
        } else {
            if(!loadMoreEventProcessed){
                loadMoreEventProcessed = true;
                shouldLoadMore = response.extend(event.getResponse());
                mAdapter.appendResponse(response);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
        getBus().unregister(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    @Override
    public void onLoadMoreRequested() {
        final String terms = mAreaSearchEdit.getText().toString();
        final int radius = radi[rightSpinner.getSelectedItemPosition()];
        mOffset = response.getTotalVenues();
        loadMoreEventProcessed = false;
        new AsyncTask<Void, Void, Response>(){
            @Override
            protected Response doInBackground(Void... params){
                try{
                    Thread.sleep(1000);
                    getBus().post(new LoadVenuesEvent(LoadVenuesEvent.FOURSQUARE, String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()),
                            terms, true, radius, mOffset, false));
                } catch (InterruptedException e){
                    e.printStackTrace();
                }

                return response;
            }

            @Override
            protected void onPostExecute(Response response){
                //mAdapter.appendResponse(response);
                if(!shouldLoadMore){
                    endlessRecyclerViewAdapter.onDataReady(false);
                }else{
                    endlessRecyclerViewAdapter.onDataReady(true);
                }
            }
        }.execute();
    }

    @Override
    public void onRecyclerViewClicked(View v, int position) {
        //Toast.makeText(getContext(), "Clicked on card number " + position, Toast.LENGTH_SHORT).show();
        mListener.onSearchFragmentInteraction(response.getVenueAt(position));
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSearchFragmentInteraction(VenueFoursquare venueFoursquare);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void startLocationUpdates() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private Bus getBus() {
        if (mBus == null) {
            mBus = BusProvider.getInstance();
        }
        return mBus;
    }

    public void setBus(Bus bus) {
        mBus = bus;
    }

    private void animateResultsScreen(){

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAlpha(0f);

        recyclerView.animate()
                .alpha(1f)
                .setDuration(mAnimationDuration)
                .setListener(null);

        initialView.animate()
                .alpha(0f)
                .setDuration(mAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        initialView.setVisibility(View.GONE);
                    }
                });
    }

    private void animateSearchScreen(){

        initialView.setVisibility(View.VISIBLE);
        initialView.setAlpha(0f);

        initialView.animate()
                .alpha(1f)
                .setDuration(mAnimationDuration)
                .setListener(null);

        recyclerView.animate()
                .alpha(0f)
                .setDuration(mAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        recyclerView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, mLocationRequest, this);
                } else {
                    Toast.makeText(getActivity(), "Permission denied :(", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}
