package com.harrykristi.hangapp.events;

import com.harrykristi.hangapp.Models.FoursquareResponse;

import retrofit.client.Response;

public class DataLoadedVenueEvent {
    private String mData;
    private FoursquareResponse mFoursquareResponse;
    private Response mResponse;
    private boolean mRefresh;

    public DataLoadedVenueEvent(FoursquareResponse foursquareResponse, Response response, boolean refresh) {
        mData = response.toString();
        mResponse = response;
        mFoursquareResponse = foursquareResponse;
        mRefresh = refresh;
    }

    public boolean getRefresh(){
        return mRefresh;
    }

    public String getData() { return mData;}
    public FoursquareResponse getFoursquareResponseResponse(){ return mFoursquareResponse;}
    public com.harrykristi.hangapp.Models.Response getResponse(){ return mFoursquareResponse.getResponse();}

}
