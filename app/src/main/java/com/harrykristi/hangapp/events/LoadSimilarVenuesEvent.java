package com.harrykristi.hangapp.events;


public class LoadSimilarVenuesEvent {
    String venueId;

    public LoadSimilarVenuesEvent(String venueId){
        this.venueId = venueId;
    }

    public String getVenueId() {
        return venueId;
    }

}
