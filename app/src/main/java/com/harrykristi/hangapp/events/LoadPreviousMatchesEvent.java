package com.harrykristi.hangapp.events;

public class LoadPreviousMatchesEvent {
    private String userId;
    private String venueId;

    public String getUserId() {
        return userId;
    }
    public String getVenueId(){return venueId;}

    public LoadPreviousMatchesEvent(String userId, String venueId){
        this.userId = userId;
        this.venueId = venueId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
