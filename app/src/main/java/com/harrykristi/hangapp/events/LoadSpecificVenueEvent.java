package com.harrykristi.hangapp.events;

public class LoadSpecificVenueEvent {
    String mVenueId;

    public LoadSpecificVenueEvent(String venueId){
        mVenueId = venueId;
    }

    public String getmVenueId() {
        return mVenueId;
    }
}
