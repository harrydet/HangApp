package com.harrykristi.hangapp.events;


public class LoadVenuesEvent {
    public static final int FOURSQUARE = 0;
    public static final int BACKEND = 1;

    String mLongitude;
    String mLatitude;
    String mSearchTerms;
    boolean mGetPhotos;
    private int mSearchRadius;
    private int mOffset;
    private boolean mRefresh;
    private int mServer;

    public LoadVenuesEvent(int server, String latitude, String longitude, String searchTerms, boolean getPhotos, int searchRadius, int offset, boolean refresh){
        mLongitude = longitude;
        mLatitude = latitude;
        mSearchTerms = searchTerms;
        mGetPhotos = getPhotos;
        mSearchRadius = searchRadius;
        mOffset = offset;
        mRefresh = refresh;
        mServer = server;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public String getmSearchTerms() {
        return mSearchTerms;
    }

    public void setmSearchTerms(String mSearchTerms) {
        this.mSearchTerms = mSearchTerms;
    }

    public boolean ismGetPhotos() {
        return mGetPhotos;
    }

    public void setmGetPhotos(boolean mGetPhotos) {
        this.mGetPhotos = mGetPhotos;
    }

    public int getmSearchRadius() {
        return mSearchRadius;
    }

    public int getmOffset() {
        return mOffset;
    }

    public boolean getRefresh() {
        return mRefresh;
    }

    public int getmServer() {
        return mServer;
    }

    public void setmServer(int mServer) {
        this.mServer = mServer;
    }
}
