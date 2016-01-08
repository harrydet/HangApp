package com.harrykristi.hangapp.Models;

import java.util.List;

public class UserProfileResponse {
    String first_name;
    String last_name;
    String profilePictureUrl;
    int total_checkins, total_matches;
    List<User> match_user;
    List<VenueHangApp> match_venue;

    //API error checking
    boolean error;
    String message;

    public List<VenueHangApp> getMatch_venue() {
        return match_venue;
    }

    public List<User> getMatch_user() {
        return match_user;
    }

    public int getTotal_checkins() {
        return total_checkins;
    }

    public int getTotal_matches() {
        return total_matches;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
