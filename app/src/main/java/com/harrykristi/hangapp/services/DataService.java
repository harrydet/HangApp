package com.harrykristi.hangapp.services;

import com.harrykristi.hangapp.Interfaces.HangAppAPI;
import com.harrykristi.hangapp.Models.ResponseUserSearching;
import com.harrykristi.hangapp.Models.UserProfileResponse;
import com.harrykristi.hangapp.events.ApiErrorEvent;
import com.harrykristi.hangapp.events.DataLoadedPreviousMatchesEvent;
import com.harrykristi.hangapp.events.DataLoadedSpecificVenue;
import com.harrykristi.hangapp.events.DataLoadedUserEvent;
import com.harrykristi.hangapp.events.DataLoadedVenueEvent;
import com.harrykristi.hangapp.events.GetUserPictureEvent;
import com.harrykristi.hangapp.events.LoadPreviousMatchesEvent;
import com.harrykristi.hangapp.events.LoadSpecificVenueEvent;
import com.harrykristi.hangapp.events.LoadVenuesEvent;
import com.harrykristi.hangapp.Interfaces.FoursquareAPI;
import com.harrykristi.hangapp.events.RegisterUserEvent;
import com.harrykristi.hangapp.events.ResponseUserSearchingEvent;
import com.harrykristi.hangapp.events.StartUserSearchingEvent;
import com.harrykristi.hangapp.events.UserRegisteredResponseEvent;
import com.harrykristi.hangapp.Models.FoursquareResponse;
import com.harrykristi.hangapp.Models.HangAppResponse;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DataService {
    private FoursquareAPI mFoursquareApi;
    private HangAppAPI mHangAppApi;
    private Bus mBus;

    public DataService(FoursquareAPI foursquareApi, HangAppAPI hangAppApi, Bus bus) {
        mFoursquareApi = foursquareApi;
        mHangAppApi = hangAppApi;
        mBus = bus;
    }

    @Subscribe
    public void onLoadData(final LoadVenuesEvent event) {
        String latlongString = event.getmLatitude() + "," + event.getmLongitude();
        String searchTerms = event.getmSearchTerms();
        boolean getPhotos = event.ismGetPhotos();
        int radius = event.getmSearchRadius();
        int offset = event.getmOffset();

        mFoursquareApi.fetchVenues(latlongString,
                searchTerms,
                getPhotos,
                radius,
                "drinks",
                10,
                offset,
                "NMPSMEOV355DFL0B0QXFNNHEDXTNKFEQWUHBXQMFTOERMJLY",
                "20151123",
                new Callback<FoursquareResponse>() {
                    @Override
                    public void success(FoursquareResponse foursquareResponse, Response response) {
                        mBus.post(new DataLoadedVenueEvent(foursquareResponse, response, event.getRefresh()));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mBus.post(new ApiErrorEvent(error));
                    }
                });
    }

    @Subscribe
    public void onLoadData(final LoadPreviousMatchesEvent event){
        String id = event.getUserId();
        String venueId = event.getVenueId();
        mHangAppApi.fetchPreviousMatches(id,
                venueId,
                new Callback<UserProfileResponse>() {
                    @Override
                    public void success(UserProfileResponse hangAppResponse, Response response) {
                        mBus.post(new DataLoadedPreviousMatchesEvent(hangAppResponse));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mBus.post(error);
                    }
                });
    }

    @Subscribe
    public void onLoadData(final RegisterUserEvent event){
        String objectId = event.getObject_id();
        String email = event.getEmail();
        String firstName = event.getFirst_name();
        String lastName = event.getLast_name();

        mHangAppApi.registerUser(objectId,
                email,
                firstName,
                lastName,
                new Callback<HangAppResponse>() {
                    @Override
                    public void success(HangAppResponse hangAppResponse, Response response) {
                        mBus.post(new UserRegisteredResponseEvent(hangAppResponse));
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    @Subscribe
    public void onLoadData(final GetUserPictureEvent event){
        String objectId = event.getObjectId();

        mHangAppApi.fetchUserWithParams(objectId,
                true,
                new Callback<UserProfileResponse>() {
                    @Override
                    public void success(UserProfileResponse userProfileResponse, Response response) {
                        mBus.post(new DataLoadedUserEvent(userProfileResponse, response.getStatus()));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        int i = 0;
                    }
                });
    }

    @Subscribe
    public void onLoadData(final LoadSpecificVenueEvent event){
        mFoursquareApi.fetchSpecificVenue(event.getmVenueId(),
                true,
                "NMPSMEOV355DFL0B0QXFNNHEDXTNKFEQWUHBXQMFTOERMJLY",
                "20151123",
                new Callback<FoursquareResponse>() {
                    @Override
                    public void success(FoursquareResponse foursquareResponse, Response response) {
                        mBus.post(new DataLoadedSpecificVenue(foursquareResponse, response));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mBus.post(new ApiErrorEvent(error));
                    }
                });
    }

    @Subscribe
    public void onLoadData(final StartUserSearchingEvent event){
        mHangAppApi.startSearching(event.getUser_id(),
                event.getVenue_id(),
                new Callback<ResponseUserSearching>(){
                    @Override
                    public void success(ResponseUserSearching responseUserSearching, Response response) {
                        mBus.post(new ResponseUserSearchingEvent(responseUserSearching.isError(), responseUserSearching.getMessage()));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mBus.post(new ApiErrorEvent(error));
                    }
                });
    }

}
