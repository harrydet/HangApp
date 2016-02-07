package com.harrykristi.hangapp.events;


import com.harrykristi.hangapp.Models.FoursquareResponse;

import retrofit.client.Response;

public class DataLoadedSpecificVenue {
    private FoursquareResponse mFoursquareResponse;
    private Response mResponse;

    public DataLoadedSpecificVenue(FoursquareResponse foursquareResponse, Response response){
        mFoursquareResponse = foursquareResponse;
        mResponse = response;
    }

    public FoursquareResponse getmFoursquareResponse() {
        return mFoursquareResponse;
    }

    public Response getmResponse() {
        return mResponse;
    }
}
