package com.harrykristi.hangapp.Models;

import java.util.List;

public class UserProfileResponse {
    int total_checkins, total_matches;
    List<User> match_user;
    List<VenueHangApp> match_venue;

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
}
