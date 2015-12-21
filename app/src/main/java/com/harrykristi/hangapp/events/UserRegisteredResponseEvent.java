package com.harrykristi.hangapp.events;

import com.harrykristi.hangapp.Models.HangAppResponse;

public class UserRegisteredResponseEvent {
    HangAppResponse response;

    public UserRegisteredResponseEvent(HangAppResponse response){
        this.response = response;
    }

    public HangAppResponse getResponse(){
        return response;
    }
}
