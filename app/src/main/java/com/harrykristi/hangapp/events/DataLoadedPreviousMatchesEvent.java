package com.harrykristi.hangapp.events;

import com.harrykristi.hangapp.model.UserProfileResponse;

public class DataLoadedPreviousMatchesEvent {

    private UserProfileResponse response;

    public DataLoadedPreviousMatchesEvent(UserProfileResponse response){
        this.response = response;
    }

    public UserProfileResponse getResponse(){
        return response;
    }
}
