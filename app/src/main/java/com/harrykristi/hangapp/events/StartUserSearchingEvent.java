package com.harrykristi.hangapp.events;

/**
 * Created by Harry on 2/3/2016.
 */
public class StartUserSearchingEvent {
    private String user_id;
    private String venue_id;

    public StartUserSearchingEvent(String user_id, String venue_id){
        this.user_id = user_id;
        this.venue_id = venue_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getVenue_id() {
        return venue_id;
    }
}
