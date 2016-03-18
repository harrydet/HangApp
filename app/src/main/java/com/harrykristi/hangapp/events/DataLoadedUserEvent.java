package com.harrykristi.hangapp.events;

import com.harrykristi.hangapp.model.UserProfileResponse;


public class DataLoadedUserEvent {
    UserProfileResponse response;
    int status;

    public DataLoadedUserEvent(UserProfileResponse userProfileResponse, int status) {
        this.response = userProfileResponse;
        this.status = status;
    }

    public UserProfileResponse getResponse() {
        return response;
    }

    public int getStatus() {
        return status;
    }
}
